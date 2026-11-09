package coursehub;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Task4Collectors {
    record LevelStats(Level level, long count, long sumMinutes, long minMinutes, long maxMinutes) {
        double averageMinutes() { return count == 0 ? 0 : (double) sumMinutes / count; }
    }

    record CatalogSummary(Map<Level, LevelStats> byLevel, long totalCourses, long totalMinutes) {
        double overallAverage() { return totalCourses == 0 ? 0 : (double) totalMinutes / totalCourses; }
    }

    static Collector<Course, ?, CatalogSummary> summaryCollector() {
        return Collectors.collectingAndThen(
            Collectors.groupingBy(Course::level,
                () -> new EnumMap<>(Level.class),
                Collectors.summarizingLong(Course::minutes)),
            (Map<Level, LongSummaryStatistics> m) -> {
                Map<Level, LevelStats> stats = new EnumMap<>(Level.class);
                long total = 0, minutes = 0;
                for (var e : m.entrySet()) {
                    LongSummaryStatistics s = e.getValue();
                    stats.put(e.getKey(), new LevelStats(e.getKey(), s.getCount(), s.getSum(), s.getMin(), s.getMax()));
                    total += s.getCount();
                    minutes += s.getSum();
                }
                return new CatalogSummary(Collections.unmodifiableMap(stats), total, minutes);
            });
    }

    record CountAvg(long count, double avg) {}

    public static void main(String[] args) {
        List<Course> courses = Catalog.sample();
        AtomicInteger visited = new AtomicInteger();
        Supplier<Stream<Course>> source = () -> courses.stream().peek(c -> visited.incrementAndGet());

        System.out.println("== Один проход: groupingBy + summarizingLong + collectingAndThen ==");
        CatalogSummary s = source.get().collect(summaryCollector());
        int onePass = visited.get();
        s.byLevel().values().forEach(l -> System.out.printf(
            "  %-12s count=%d avg=%.1f min=%d max=%d%n",
            l.level(), l.count(), l.averageMinutes(), l.minMinutes(), l.maxMinutes()));
        System.out.printf("  ИТОГО: courses=%d, minutes=%d, avg=%.1f%n", s.totalCourses(), s.totalMinutes(), s.overallAverage());
        System.out.printf("  Элементов обработано: %d (в каталоге %d) → 1 проход%n", onePass, courses.size());

        System.out.println("\n== Для сравнения: три полных прохода ==");
        visited.set(0);
        Map<Level, Long> counts = source.get().collect(Collectors.groupingBy(Course::level, Collectors.counting()));
        Map<Level, Double> avgs = source.get().collect(Collectors.groupingBy(Course::level, Collectors.averagingLong(Course::minutes)));
        long total = source.get().collect(Collectors.counting());
        System.out.printf("  Элементов обработано: %d = 3 * %d → 3 прохода; результат тот же: %s%n",
                          visited.get(), courses.size(), counts.equals(countsOf(s)) && avgsEqual(avgs, s) && total == s.totalCourses());
        System.out.println("  Для List это лишь 3x работы, для одноразового источника (Kafka, файл, сокет) — невозможно.");

        System.out.println("\n== Альтернатива: teeing(counting, averaging) на каждую группу ==");
        visited.set(0);
        Map<Level, CountAvg> tee = source.get().collect(Collectors.groupingBy(Course::level,
            () -> new EnumMap<>(Level.class),
            Collectors.teeing(Collectors.counting(), Collectors.averagingLong(Course::minutes), CountAvg::new)));
        tee.forEach((l, ca) -> System.out.printf("  %-12s %s%n", l, ca));
        System.out.printf("  Элементов обработано: %d → teeing подаёт элемент в оба downstream за ОДИН проход%n", visited.get());

        System.out.println("\n== Поток одноразовый ==");
        Stream<Course> once = courses.stream();
        once.collect(Collectors.counting());
        try { once.collect(Collectors.counting()); }
        catch (IllegalStateException e) { System.out.println("  Повторный терминал: IllegalStateException: " + e.getMessage()); }
        System.out.println("OK");
    }

    private static Map<Level, Long> countsOf(CatalogSummary s) {
        Map<Level, Long> m = new HashMap<>();
        s.byLevel().forEach((l, st) -> m.put(l, st.count()));
        return m;
    }

    private static boolean avgsEqual(Map<Level, Double> avgs, CatalogSummary s) {
        return avgs.entrySet().stream().allMatch(e -> Math.abs(e.getValue() - s.byLevel().get(e.getKey()).averageMinutes()) < 1e-9);
    }
}
