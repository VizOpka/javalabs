package coursehub;

import java.util.*;
import java.util.stream.Collectors;

public final class Task1Sorting {
    static final Comparator<Course> CANONICAL = Comparator
        .comparingInt((Course c) -> c.level().rank())
        .thenComparing(Course::duration)
        .thenComparing(Course::code)
        .thenComparing(Course::title);

    static final Comparator<Course> WEAK = Comparator
        .comparingInt((Course c) -> c.level().rank())
        .thenComparing(Course::duration);

    static final Comparator<Course> LONGEST_FIRST = Comparator
        .comparingInt((Course c) -> c.level().rank())
        .thenComparing(Course::duration, Comparator.reverseOrder())
        .thenComparing(Course::code)
        .thenComparing(Course::title);

    static String key(Course c) { return c.code() + "/" + c.title(); }

    static void print(String header, List<Course> courses) {
        System.out.println(header);
        for (Course c : courses) {
            System.out.printf("  %-9s %-5s %-24s %4d min%n", c.level(), c.code(), c.title(), c.minutes());
        }
    }

    public static void main(String[] args) {
        List<Course> base = new ArrayList<>(Catalog.sample());

        base.add(Catalog.course("J101", "Java Basics (legacy)", Level.BEGINNER, 600));

        List<Course> reference = base.stream().sorted(CANONICAL).toList();
        print("== CANONICAL (level↑, duration↑, code↑, title↑) ==", reference);
        print("== LONGEST_FIRST (level↑, duration↓, code↑, title↑) ==",
              base.stream().sorted(LONGEST_FIRST).toList());

        final int RUNS = 1000;
        int identical = 0;
        Set<List<String>> weakVariants = new LinkedHashSet<>();
        for (int seed = 0; seed < RUNS; seed++) {
            List<Course> shuffled = new ArrayList<>(base);
            Collections.shuffle(shuffled, new Random(seed));

            List<Course> sorted = new ArrayList<>(shuffled);
            sorted.sort(CANONICAL);
            if (sorted.equals(reference)) identical++;

            weakVariants.add(shuffled.stream().sorted(WEAK).map(Task1Sorting::key).toList());
        }
        System.out.println();
        System.out.printf("CANONICAL: %d/%d перестановок дали ИДЕНТИЧНЫЙ результат%n", identical, RUNS);
        System.out.printf("WEAK (без tie-breakers): %d РАЗНЫХ результатов из %d перестановок%n",
                          weakVariants.size(), RUNS);

        System.out.println("Два разных результата WEAK (BEGINNER, 600 min — ничья по ключам):");
        weakVariants.stream().limit(2).forEach(v ->
            System.out.println("  " + v.stream().filter(s -> s.startsWith("J101") || s.startsWith("P101"))
                                       .collect(Collectors.joining(" | "))));

        if (identical != RUNS) throw new AssertionError("canonical order is not deterministic");
        System.out.println("OK: детерминизм подтверждён.");
    }
}
