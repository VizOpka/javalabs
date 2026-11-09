package coursehub;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task5Optional {
    static final Comparator<Course> LONGEST_FIRST =
        Comparator.comparing(Course::duration, Comparator.reverseOrder()).thenComparing(Course::code);

    static Optional<Course> longest(Collection<Course> courses) {
        return courses.stream().min(LONGEST_FIRST);
    }

    static final class CourseProfile {
        private final String code;
        private final String mentor;

        CourseProfile(String code, String mentor) { this.code = Objects.requireNonNull(code); this.mentor = mentor; }

        String code() { return code; }
        Optional<String> mentor() { return Optional.ofNullable(mentor); }
    }

    static final class Repo {
        private final Map<String, CourseProfile> byCode = new HashMap<>();
        Repo(Collection<CourseProfile> ps) { ps.forEach(p -> byCode.put(p.code(), p)); }
        Optional<CourseProfile> findByCode(String code) { return Optional.ofNullable(byCode.get(code)); }
    }

    static int expensiveCalls = 0;
    static String expensiveFallback() { expensiveCalls++; return "fallback"; }

    public static void main(String[] args) {
        List<Course> courses = Catalog.sample();

        System.out.println("== 1. Самый длинный курс → преобразование → fallback ==");
        String label = longest(courses)
            .map(c -> "%s (%s), %d ч".formatted(c.title(), c.code(), c.duration().toHours()))
            .orElse("курсов нет");
        System.out.println("  " + label);

        System.out.println("  Пустой каталог: " + longest(List.of())
            .map(c -> c.title().toUpperCase(Locale.ROOT)).orElse("курсов нет"));

        List<Course> tie = courses.stream().filter(c -> !c.code().equals("D301")).toList();
        System.out.println("  Ничья 900 мин (W201/J301/K301) → побеждает меньший code: "
            + longest(tie).map(Course::code).orElse("?"));

        System.out.println("\n== 2. filter + ifPresentOrElse ==");
        longest(courses).filter(c -> c.level() == Level.BEGINNER)
            .ifPresentOrElse(c -> System.out.println("  начальный: " + c.code()),
                             () -> System.out.println("  самый длинный курс — не начального уровня"));

        System.out.println("\n== 3. flatMap по цепочке Optional-ов ==");
        Repo repo = new Repo(List.of(new CourseProfile("J101", "Anna"), new CourseProfile("P101", null)));
        for (String code : List.of("J101", "P101", "NOPE")) {
            String mentor = repo.findByCode(code).flatMap(CourseProfile::mentor).map(String::toUpperCase).orElse("не назначен");
            System.out.printf("  %-5s → %s%n", code, mentor);
        }

        System.out.println("\n== 4. orElse (eager) vs orElseGet (lazy) ==");
        Optional<Course> present = longest(courses);
        expensiveCalls = 0;
        present.map(Course::code).orElse(expensiveFallback());
        int afterOrElse = expensiveCalls;
        present.map(Course::code).orElseGet(Task5Optional::expensiveFallback);
        System.out.printf("  при НАЛИЧИИ значения: orElse вызвал fallback %d раз, orElseGet — %d раз%n",
                          afterOrElse, expensiveCalls - afterOrElse);

        System.out.println("\n== 5. orElseThrow, когда отсутствие — нарушение инварианта ==");
        try {
            longest(List.of()).orElseThrow(() -> new NoSuchElementException("каталог не должен быть пустым"));
        } catch (NoSuchElementException e) { System.out.println("  поймано: " + e.getMessage()); }

        System.out.println("\n== 6. Примитивные Optional ==");
        System.out.println("  max minutes = " + courses.stream().mapToLong(Course::minutes).max().orElse(-1));

        System.out.println("\n== 7. Optional не хранится в полях ==");
        for (Class<?> k : List.of(Course.class, CourseModule.class, CourseProfile.class)) {
            boolean bad = Arrays.stream(k.getDeclaredFields()).map(Field::getType).anyMatch(t -> t == Optional.class);
            System.out.printf("  %-14s Optional-полей: %s%n", k.getSimpleName(), bad ? "ЕСТЬ" : "нет");
            if (bad) throw new AssertionError();
        }
        System.out.println("  Optional.get()/isPresent()+get() в коде задания не используются.");
        System.out.println("OK");
    }
}
