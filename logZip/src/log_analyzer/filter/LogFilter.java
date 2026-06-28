//Фильтр и сортировка для логов
package log_analyzer.filter;

import log_analyzer.model.LogEntry;
import log_analyzer.model.LogLevel;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

public class LogFilter {

    public enum Order { ASC, DESC }

    private LocalDate from;
    private LocalDate to;
    private final Set<LogLevel> levels = EnumSet.noneOf(LogLevel.class);
    private Order order = Order.ASC;

    public LogFilter from(LocalDate from) { this.from = from; return this; }
    public LogFilter to(LocalDate to)     { this.to = to;     return this; }
    public LogFilter addLevel(LogLevel l) { this.levels.add(l); return this; }
    public LogFilter order(Order o)       { this.order = o;   return this; }

    public boolean hasLevels() { return !levels.isEmpty(); }

    public Stream<LogEntry> apply(Stream<LogEntry> stream) {
        Stream<LogEntry> s = stream;

        if (from != null) {
            s = s.filter(e -> !e.getTimestamp().toLocalDate().isBefore(from));
        }
        if (to != null) {
            s = s.filter(e -> !e.getTimestamp().toLocalDate().isAfter(to));
        }
        if (!levels.isEmpty()) {
            s = s.filter(e -> levels.contains(e.getLevel()));
        }

        Comparator<LogEntry> cmp = Comparator.comparing(LogEntry::getTimestamp);
        if (order == Order.DESC) cmp = cmp.reversed();

        return s.sorted(cmp);
    }
}