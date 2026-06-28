//Читает логи из .log или .zip файла.
package log_analyzer.reader;

import log_analyzer.model.LogEntry;
import log_analyzer.parser.LogParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LogReader {

    public static List<LogEntry> read(Path path) throws IOException {
        String name = path.getFileName().toString().toLowerCase();

        if (name.endsWith(".zip")) {
            return readFromZip(path);
        } else if (name.endsWith(".log")) {
            return readFromLog(path);
        }
        throw new IllegalArgumentException("Неподдерживаемый формат: " + name);
    }

    private static List<LogEntry> readFromLog(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return parseLines(lines);
    }

    private static List<LogEntry> readFromZip(Path path) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(path))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".log")) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zis, StandardCharsets.UTF_8))) {
                        List<LogEntry> result = new ArrayList<>();
                        String line;
                        while ((line = br.readLine()) != null) {
                            LogParser.parse(line).ifPresent(result::add);
                        }
                        return result;
                    }
                }
            }
        }
        throw new IOException("В архиве не найден ни один .log файл");
    }

    private static List<LogEntry> parseLines(List<String> lines) {
        List<LogEntry> result = new ArrayList<>(lines.size());
        for (String line : lines) {
            LogParser.parse(line).ifPresent(result::add);
        }
        return result;
    }
}