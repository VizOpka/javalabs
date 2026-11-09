package coursehub;

import java.time.Duration;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Task3Reduce {
    record Workload(Duration total, int courses, int modules) {
        static final Workload EMPTY = new Workload(Duration.ZERO, 0, 0);

        static Workload of(Course c) { return new Workload(c.duration(), 1, c.modules().size()); }

        Workload merge(Workload o) {
            return new Workload(total.plus(o.total),
                                Math.addExact(courses, o.courses),
                                Math.addExact(modules, o.modules));
        }
    }

    static <T> T chunkedReduce(List<T> items, int chunks, T identity, BinaryOperator<T> op) {
        int n = items.size();
        List<T> partials = new ArrayList<>();
        for (int i = 0; i < chunks; i++) {
            int from = (int) ((long) n * i / chunks), to = (int) ((long) n * (i + 1) / chunks);
            partials.add(items.subList(from, to).stream().reduce(identity, op));
        }
        return partials.stream().reduce(op).orElse(identity);
    }

    public static void main(String[] args) throws Exception {
        List<Course> courses = Catalog.sample();

        System.out.println("== 1. Сумма длительностей: правильная identity ==");
        Duration sum = courses.stream().map(Course::duration).reduce(Duration.ZERO, Duration::plus);
        Duration sum3 = courses.stream().reduce(Duration.ZERO, (acc, c) -> acc.plus(c.duration()), Duration::plus);
        long sumPrim = courses.stream().mapToLong(Course::minutes).sum();
        System.out.printf("reduce(ZERO, plus)          = %s (%d min)%n", sum, sum.toMinutes());
        System.out.printf("reduce(ZERO, acc, combiner) = %s%n", sum3);
        System.out.printf("mapToLong(..).sum()         = %d min%n", sumPrim);
        if (!sum.equals(sum3) || sum.toMinutes() != sumPrim) throw new AssertionError();

        System.out.println("\n== 2. Value objects: Workload.merge ==");
        Workload w = courses.stream().map(Workload::of).reduce(Workload.EMPTY, Workload::merge);
        System.out.println(w);

        System.out.println("\n== 3. НЕПРАВИЛЬНАЯ identity нарушает результат ==");
        Duration badId = Duration.ofMinutes(30);
        List<Duration> durations = courses.stream().map(Course::duration).toList();
        System.out.println("Верный итог: " + sum.toMinutes() + " min. Identity = 30 min (не нейтральна: 30 + x != x)");
        for (int k : new int[]{1, 2, 5}) {
            Duration r = chunkedReduce(durations, k, badId, Duration::plus);
            System.out.printf("  %d кусков(а): %5d min  (ошибка +%d min = 30 * %d)%n",
                              k, r.toMinutes(), r.minus(sum).toMinutes(), k);
        }

        List<Duration> big = new ArrayList<>();
        for (int i = 0; i < 1000; i++) big.addAll(durations);
        Duration bigTrue = big.stream().reduce(Duration.ZERO, Duration::plus);
        Duration bigSeqBad = big.stream().reduce(badId, Duration::plus);
        Duration bigParBad = big.parallelStream().reduce(badId, Duration::plus);
        System.out.printf("1000x данных: верно=%d min | seq с bad identity=%d | parallel с bad identity=%d%n",
            bigTrue.toMinutes(), bigSeqBad.toMinutes(), bigParBad.toMinutes());
        System.out.println("  (sequential «случайно» ошибся ровно на 30; parallel — на 30 * число_листьев, зависит от машины)");

        Workload badWorkload = new Workload(Duration.ZERO, 1, 0);
        List<Workload> ws = courses.stream().map(Workload::of).toList();
        for (int k : new int[]{1, 2, 5}) {
            Workload r = chunkedReduce(ws, k, badWorkload, Workload::merge);
            System.out.printf("  Workload с bad identity, %d кусков: courses=%d (верно %d)%n", k, r.courses(), w.courses());
        }

        System.out.println("\n  Неверная identity для min/max:");
        List<Integer> pos = List.of(5, 3, 9), neg = List.of(-5, -3, -9);
        System.out.printf("  min, identity 0:           %d (верно 3)%n", pos.stream().reduce(0, Math::min));
        System.out.printf("  min, identity MAX_VALUE:   %d%n", pos.stream().reduce(Integer.MAX_VALUE, Math::min));
        System.out.printf("  max, identity 0 на {-5,-3,-9}:        %d (верно -3)%n", neg.stream().reduce(0, Math::max));
        System.out.printf("  max, identity MIN_VALUE на {-5,-3,-9}: %d%n", neg.stream().reduce(Integer.MIN_VALUE, Math::max));

        System.out.println("\n== 4. Ассоциативность accumulator ==");
        System.out.println("Требования к reduce(identity, op):");
        System.out.println("  assoc:    op(op(a,b),c) == op(a,op(b,c))");
        System.out.println("  identity: op(identity,a) == a == op(a,identity)");
        System.out.println("  3-арг:    combiner(u, accumulator(identity,t)) == accumulator(u,t); оба — без состояния");

        List<Integer> nums = IntStream.rangeClosed(1, 10).boxed().toList();
        int seq = nums.stream().reduce(0, (a, b) -> a - b);
        System.out.println("Вычитание (НЕ ассоциативно): sequential = " + seq);
        for (int k : new int[]{2, 5}) {
            System.out.printf("  %d кусков: %d%n", k, chunkedReduce(nums, k, 0, (a, b) -> a - b));
        }

        int triples = 0;
        for (Workload a : ws) for (Workload b : ws) for (Workload c : ws) {
            if (!a.merge(b).merge(c).equals(a.merge(b.merge(c)))) throw new AssertionError("not associative");
            triples++;
        }
        for (Workload a : ws) {
            if (!Workload.EMPTY.merge(a).equals(a) || !a.merge(Workload.EMPTY).equals(a)) throw new AssertionError("identity");
        }
        System.out.printf("Workload.merge: ассоциативность проверена на %d тройках, identity — на %d элементах%n",
                          triples, ws.size());

        for (int k = 1; k <= ws.size(); k++) {
            if (!chunkedReduce(ws, k, Workload.EMPTY, Workload::merge).equals(w)) throw new AssertionError();
        }
        System.out.println("Для всех разбиений на 1.." + ws.size() + " кусков результат совпал с sequential: " + w.total());
        System.out.println("OK");
    }
}
