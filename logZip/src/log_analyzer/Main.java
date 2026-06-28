package log_analyzer;

import log_analyzer.analyzer.LogAnalyzer;
import log_analyzer.filter.LogFilter;
import log_analyzer.model.LogEntry;
import log_analyzer.model.LogLevel;
import log_analyzer.reader.LogReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) throws IOException {
        // По умолчанию читаем trace.log
        Path source = args.length > 0 ? Paths.get(args[0]) : Paths.get("logs/trace.log");
        System.out.println("Читаю логи из: " + source.toAbsolutePath());

        List<LogEntry> entries = LogReader.read(source);
        System.out.println("Загружено записей: " + entries.size());

        new LogAnalyzer(entries).writeReport(Paths.get("report.txt"));

        runConsole(entries);
    }

    private static void runConsole(List<LogEntry> entries) {
        Scanner sc = new Scanner(System.in);
        LogFilter filter = new LogFilter();

        System.out.println("\n=== Консоль анализа логов ===");
        printHelp();

        while (true) {
            System.out.print("\n> ");
            String cmd = sc.nextLine().trim().toLowerCase();

            try {
                switch (cmd) {
                    case "help"       -> printHelp();
                    case "show"       -> show(entries, filter);
                    case "reset"      -> filter = new LogFilter();
                    case "asc"        -> filter.order(LogFilter.Order.ASC);
                    case "desc"       -> filter.order(LogFilter.Order.DESC);
                    case "exit", "q"  -> { System.out.println("Bye!"); return; }
                    default           -> handleParamCommand(cmd, filter, sc);
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void handleParamCommand(String cmd, LogFilter filter, Scanner sc) {
        switch (cmd) {
            case "from" -> {
                System.out.print("Дата начала (yyyy-MM-dd): ");
                filter.from(parseDate(sc.nextLine()));
            }
            case "to" -> {
                System.out.print("Дата конца (yyyy-MM-dd): ");
                filter.to(parseDate(sc.nextLine()));
            }
            case "level" -> {
                System.out.print("Уровни через пробел (INFO WARN ERROR TRACE DEBUG): ");
                for (String part : sc.nextLine().trim().split("\\s+")) {
                    if (part.isEmpty()) continue;
                    try {
                        filter.addLevel(LogLevel.valueOf(part.toUpperCase()));
                    } catch (IllegalArgumentException ignored) {
                        System.out.println("Неизвестный уровень: " + part);
                    }
                }
            }
            default -> System.out.println("Неизвестная команда. Введите 'help'.");
        }
    }

    private static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты: " + s);
        }
    }

    private static void show(List<LogEntry> entries, LogFilter filter) {
        List<LogEntry> result = filter.apply(entries.stream()).toList();
        System.out.println("--- Результат (" + result.size() + " записей) ---");
        result.forEach(System.out::println);
    }

    private static void printHelp() {
        System.out.println("""
            Команды:
              from          — задать начало периода (дата)
              to            — задать конец периода (дата)
              level         — задать уровни (INFO WARN ERROR ...)
              asc / desc    — сортировка по времени
              show          — показать результат с текущими фильтрами
              reset         — сбросить все фильтры
              help          — эта подсказка
              exit / q      — выход

            Команды можно комбинировать:
              level → from → to → desc → show
            """);
    }
}