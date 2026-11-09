package coursehub;

import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public final class Task6CustomCollector {
    public record MinutesStats(long count, long sum, long rawMin, long rawMax) {
        public OptionalLong min() { return count == 0 ? OptionalLong.empty() : OptionalLong.of(rawMin); }
        public OptionalLong max() { return count == 0 ? OptionalLong.empty() : OptionalLong.of(rawMax); }
        public OptionalDouble average() { return count == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) sum / count); }
        @Override public String toString() {
            return "Stats[count=%d, sum=%d, min=%s, max=%s]".formatted(count, sum,
                min().isPresent() ? min().getAsLong() : "-", max().isPresent() ? max().getAsLong() : "-");
        }
    }

    private static final class Acc {
        long count, sum, min = Long.MAX_VALUE, max = Long.MIN_VALUE;

        void add(long v) {
            count++;
            sum = Math.addExact(sum, v);
            if (v < min) min = v;
            if (v > max) max = v;
        }

        Acc merge(Acc o) {
            count += o.count;
            sum = Math.addExact(sum, o.sum);
            min = Math.min(min, o.min);
            max = Math.max(max, o.max);
            return this;
        }

        MinutesStats finish() { return new MinutesStats(count, sum, min, max); }
    }

    public static <T> Collector<T, ?, MinutesStats> statsOf(ToLongFunction<? super T> f) {
        return Collector.of(Acc::new, (a, t) -> a.add(f.applyAsLong(t)), Acc::merge, Acc::finish,
                            Collector.Characteristics.UNORDERED);
    }

    static <T, A, R> R viaSplit(Collector<T, A, R> c, List<T> items, int cut) {
        A left = c.supplier().get(), right = c.supplier().get();
        items.subList(0, cut).forEach(t -> c.accumulator().accept(left, t));
        items.subList(cut, items.size()).forEach(t -> c.accumulator().accept(right, t));
        return c.finisher().apply(c.combiner().apply(left, right));
    }

    static <T, A, R> R viaThreeWay(Collector<T, A, R> c, List<T> items, int i, int j) {
        A a = c.supplier().get(), b = c.supplier().get(), d = c.supplier().get();
        items.subList(0, i).forEach(t -> c.accumulator().accept(a, t));
        items.subList(i, j).forEach(t -> c.accumulator().accept(b, t));
        items.subList(j, items.size()).forEach(t -> c.accumulator().accept(d, t));
        return c.finisher().apply(c.combiner().apply(c.combiner().apply(a, b), d));
    }

    public static void main(String[] args) {
        List<Course> courses = Catalog.sample();
        Collector<Course, ?, MinutesStats> collector = statsOf(Course::minutes);

        System.out.println("== 1. Результат и сравнение с summarizingLong ==");
        MinutesStats mine = courses.stream().collect(collector);
        LongSummaryStatistics std = courses.stream().collect(Collectors.summarizingLong(Course::minutes));
        System.out.println("  custom : " + mine + ", avg=" + mine.average().getAsDouble());
        System.out.println("  builtin: " + std);
        boolean same = mine.count() == std.getCount() && mine.sum() == std.getSum()
            && mine.min().getAsLong() == std.getMin() && mine.max().getAsLong() == std.getMax()
            && mine.average().getAsDouble() == std.getAverage();
        System.out.println("  значения совпадают: " + same);
        if (!same) throw new AssertionError();

        System.out.println("\n== 2. Корректность combiner для ЛЮБОГО разбиения ==");
        for (int cut = 0; cut <= courses.size(); cut++) {
            if (!viaSplit(collector, courses, cut).equals(mine)) throw new AssertionError("cut " + cut);
        }
        System.out.printf("  двухчастные разбиения: %d из %d совпали%n", courses.size() + 1, courses.size() + 1);
        int three = 0;
        for (int i = 0; i <= courses.size(); i++)
            for (int j = i; j <= courses.size(); j++) {
                if (!viaThreeWay(collector, courses, i, j).equals(mine)) throw new AssertionError();
                three++;
            }
        System.out.printf("  трёхчастные разбиения: %d из %d совпали%n", three, three);

        List<Course> shuffled = new ArrayList<>(courses);
        for (int seed = 0; seed < 200; seed++) {
            Collections.shuffle(shuffled, new Random(seed));
            if (!shuffled.parallelStream().collect(collector).equals(mine)) throw new AssertionError();
        }
        System.out.println("  200 перемешиваний + parallelStream: результат всегда одинаков");

        System.out.println("\n== 3. Пустой поток ==");
        MinutesStats empty = Collections.<Course>emptyList().stream().collect(collector);
        LongSummaryStatistics emptyStd = LongStream.empty().summaryStatistics();
        System.out.println("  custom : " + empty + ", average=" + empty.average());
        System.out.println("  builtin: " + emptyStd + "  ← min/max = sentinel-ы Long.MAX/MIN_VALUE, average=0.0");

        System.out.println("\n== 4. Переполнение ==");
        long[] huge = {Long.MAX_VALUE, 1};
        System.out.println("  builtin sum: " + Arrays.stream(huge).boxed().collect(Collectors.summarizingLong(Long::longValue)).getSum()
            + "  ← тихо обернулось в отрицательное");
        try { Arrays.stream(huge).boxed().collect(statsOf(Long::longValue)); }
        catch (ArithmeticException e) { System.out.println("  custom: ArithmeticException: " + e.getMessage()); }

        System.out.println("\n== 5. Композиция с groupingBy и свойства collector-а ==");
        Map<Level, MinutesStats> byLevel = courses.stream()
            .collect(Collectors.groupingBy(Course::level, () -> new EnumMap<>(Level.class), collector));
        byLevel.forEach((l, s) -> System.out.printf("  %-12s %s%n", l, s));
        System.out.println("  characteristics: " + collector.characteristics()
            + " (IDENTITY_FINISH нет: finisher создаёт неизменяемый record)");
        System.out.println("  статических изменяемых полей в классе нет; Acc создаётся supplier-ом на каждый кусок");
        System.out.println("OK");
    }
}
