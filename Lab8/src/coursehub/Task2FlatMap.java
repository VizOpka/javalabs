package coursehub;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Task2FlatMap {
    private static final Pattern WS = Pattern.compile("\\s+");

    static String normalize(String raw) {
        String n = Normalizer.normalize(raw, Normalizer.Form.NFKC).strip();
        return WS.matcher(n).replaceAll(" ").toLowerCase(Locale.ROOT);
    }

    record TopicCourse(String topic, String courseCode) {}

    static java.util.stream.Stream<TopicCourse> topicPairs(Course c) {
        return c.modules().stream()
                .flatMap(m -> m.topics().stream())
                .map(Task2FlatMap::normalize)
                .filter(t -> !t.isEmpty())
                .map(t -> new TopicCourse(t, c.code()));
    }

    static SortedSet<String> uniqueTopics(List<Course> courses) {
        return courses.stream()
            .flatMap(Task2FlatMap::topicPairs)
            .map(TopicCourse::topic)
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(TreeSet::new),
                Collections::unmodifiableSortedSet));
    }

    static SortedMap<String, SortedSet<String>> reverseIndex(List<Course> courses) {
        return courses.stream()
            .flatMap(Task2FlatMap::topicPairs)
            .collect(Collectors.collectingAndThen(
                Collectors.groupingBy(
                    TopicCourse::topic,
                    TreeMap::new,
                    Collectors.mapping(TopicCourse::courseCode,
                        Collectors.collectingAndThen(
                            Collectors.toCollection(TreeSet::new),
                            Collections::unmodifiableSortedSet))),
                Collections::unmodifiableSortedMap));
    }

    public static void main(String[] args) {
        List<Course> courses = Catalog.sample();

        long rawCount = courses.stream().flatMap(c -> c.modules().stream())
                               .flatMap(m -> m.topics().stream()).count();
        SortedSet<String> topics = uniqueTopics(courses);
        System.out.printf("Сырых тем (с повторами/мусором): %d, уникальных нормализованных: %d%n",
                          rawCount, topics.size());
        System.out.println("Темы: " + topics);

        SortedMap<String, SortedSet<String>> index = reverseIndex(courses);
        System.out.println("\nОбратный индекс topic → courses:");
        index.forEach((t, cs) -> System.out.printf("  %-20s -> %s%n", "'" + t + "'", cs));

        if (!index.keySet().equals(topics)) throw new AssertionError("index keys != unique topics");
        Set<String> coded = index.values().stream().flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));
        Set<String> withTopics = courses.stream().filter(c -> topicPairs(c).findAny().isPresent())
                                        .map(Course::code).collect(Collectors.toCollection(TreeSet::new));
        if (!coded.equals(withTopics)) throw new AssertionError("coverage mismatch");
        System.out.println("\nКурсы без тем в индексе отсутствуют: "
            + courses.stream().map(Course::code).filter(c -> !coded.contains(c)).toList());

        System.out.println("Курсы по теме 'java': " + index.get("java"));

        try { index.get("java").add("X"); throw new AssertionError("must be immutable"); }
        catch (UnsupportedOperationException e) { System.out.println("Результат неизменяем: UnsupportedOperationException"); }
        System.out.println("OK");
    }
}
