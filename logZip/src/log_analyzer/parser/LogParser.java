//Парсит строки логов в объекты LogEntry.
package log_analyzer.parser;

import log_analyzer.model.LogEntry;
import log_analyzer.model.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LogParser {

    private static final DateTimeFormatter FMT_WITH_MS    = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter FMT_WITHOUT_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Optional<LogEntry> parse(String line) {
        if (line == null || line.isBlank()) return Optional.empty();

        try {
            // Пробуем формат с миллисекундами (23 символа)
            if (line.length() > 19 && line.charAt(19) == '.') {
                String tsStr = line.substring(0, 23);
                String rest  = line.substring(24).trim();
                return buildEntry(tsStr, FMT_WITH_MS, rest);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<LogEntry> buildEntry(String tsStr, DateTimeFormatter fmt, String rest) {
        LocalDateTime ts = LocalDateTime.parse(tsStr, fmt);

        int spaceIdx = rest.indexOf(' ');
        if (spaceIdx == -1) return Optional.empty();

        String levelStr = rest.substring(0, spaceIdx);
        String message  = rest.substring(spaceIdx + 1).trim();

        // Проверяем, является ли levelStr действительным уровнем
        LogLevel level;
        try {
            level = LogLevel.valueOf(levelStr);
        } catch (IllegalArgumentException e) {
            // Если уровня нет (например, "Failed to validate...") — пропускаем строку
            return Optional.empty();
        }

        return Optional.of(new LogEntry(ts, level, message));
    }
}