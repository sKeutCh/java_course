//Анализирует логи и генерирует отчёт
package log_analyzer.analyzer;

import log_analyzer.model.LogEntry;
import log_analyzer.model.LogLevel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LogAnalyzer {

    private final List<LogEntry> entries;

    public LogAnalyzer(List<LogEntry> entries) {
        this.entries = entries;
    }

    public void writeReport(Path out) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ОТЧЕТ ПО ЛОГАМ ===\n\n");

        // 1. Общее количество
        sb.append("1) Всего записей: ").append(entries.size()).append("\n\n");

        // 2. Количество по уровням
        sb.append("2) Количество по уровням:\\n");
        Map<LogLevel, Long> byLevel = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getLevel, Collectors.counting()));
        for (LogLevel lvl : LogLevel.values()) {
            long cnt = byLevel.getOrDefault(lvl, 0L);
            if (cnt > 0) sb.append("   ").append(lvl).append(": ").append(cnt).append("\n");
        }
        sb.append("\n");

        // 3. Промежуток времени
        if (!entries.isEmpty()) {
            LocalDateTime min = entries.stream()
                    .map(LogEntry::getTimestamp)
                    .min(Comparator.naturalOrder()).get();
            LocalDateTime max = entries.stream()
                    .map(LogEntry::getTimestamp)
                    .max(Comparator.naturalOrder()).get();
            sb.append("3) Временной диапазон:\\n")
                    .append("   начало: ").append(min).append("\n")
                    .append("   конец:   ").append(max).append("\n\n");
        }

        // 4. Группировка по дням
        sb.append("4) Количество по дням:\\n");
        Map<LocalDate, Long> byDay = entries.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getTimestamp().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()));
        byDay.forEach((d, c) -> sb.append("   ").append(d).append(": ").append(c).append("\n"));
        sb.append("\n");

        // 5. Самый "ошибочный" день
        Map<LocalDate, Long> errorsByDay = entries.stream()
                .filter(e -> e.getLevel() == LogLevel.ERROR)
                .collect(Collectors.groupingBy(
                        e -> e.getTimestamp().toLocalDate(),
                        Collectors.counting()));
        if (!errorsByDay.isEmpty()) {
            Map.Entry<LocalDate, Long> worst = errorsByDay.entrySet().stream()
                    .max(Comparator.comparingLong(Map.Entry::getValue))
                    .get();
            sb.append("5) Самый 'ошибочный' день: ")
                    .append(worst.getKey()).append(" (").append(worst.getValue()).append(" errors)\n\n");
        }

        // 6. Топ сообщений
        sb.append("6) Топ сообщений:\\n");
        Map<String, Long> byMsg = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getMessage, Collectors.counting()));
        byMsg.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> sb.append("   ").append(e.getValue())
                        .append("  ").append(e.getKey()).append("\n"));

        Files.writeString(out, sb.toString());
        System.out.println("Отчёт сохранён в: " + out.toAbsolutePath());
    }
}