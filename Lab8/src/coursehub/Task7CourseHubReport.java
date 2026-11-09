package coursehub;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Task7CourseHubReport {
    enum EventType { ENROLLED, PROGRESS, COMPLETED, DROPPED }

    record EnrollmentEvent(long eventId, String studentId, String courseCode, EventType type) {}

    enum AnomalyType { UNKNOWN_COURSE, BLANK_STUDENT, COMPLETED_WITHOUT_ENROLLMENT, COMPLETED_AND_DROPPED, DUPLICATE_ENROLLMENT }

    record Anomaly(AnomalyType type, String key) {}

    record CourseRate(String courseCode, long enrolled, long completed) {
        double rate() { return enrolled == 0 ? 0 : (double) completed / enrolled; }
    }

    record Report(long events, long validEvents, long uniqueStudents,
                  List<CourseRate> courseRates, List<CourseRate> top3,
                  Map<Level, Long> enrollmentsByLevel,
                  Map<AnomalyType, Long> anomalyCounts, List<Anomaly> anomalySamples) {}

    static final int SAMPLES_PER_TYPE = 3;
    static final int E = 1, C = 2, D = 4;

    static final class PairState {
        int mask, enrollCount;
        void merge(PairState o) { mask |= o.mask; enrollCount += o.enrollCount; }
    }

    private static void addBounded(TreeSet<String> set, String key) {
        set.add(key);
        while (set.size() > SAMPLES_PER_TYPE) set.pollLast();
    }

    static final class Acc {
        private final Map<String, Level> catalog;
        long events, valid;
        final Map<String, String> students = new HashMap<>();
        final Map<String, Map<String, PairState>> pairs = new HashMap<>();
        final EnumMap<AnomalyType, Long> invalidCounts = new EnumMap<>(AnomalyType.class);
        final EnumMap<AnomalyType, TreeSet<String>> invalidSamples = new EnumMap<>(AnomalyType.class);

        Acc(Map<String, Level> catalog) { this.catalog = catalog; }

        void accept(EnrollmentEvent e) {
            events++;
            if (!catalog.containsKey(e.courseCode())) { invalid(AnomalyType.UNKNOWN_COURSE, e); return; }
            if (e.studentId() == null || e.studentId().isBlank()) { invalid(AnomalyType.BLANK_STUDENT, e); return; }
            valid++;
            String sid = students.computeIfAbsent(e.studentId(), k -> k);
            if (e.type() == EventType.PROGRESS) return;
            PairState ps = pairs.computeIfAbsent(e.courseCode(), k -> new HashMap<>())
                                .computeIfAbsent(sid, k -> new PairState());
            switch (e.type()) {
                case ENROLLED  -> { ps.mask |= E; ps.enrollCount++; }
                case COMPLETED -> ps.mask |= C;
                case DROPPED   -> ps.mask |= D;
                default -> { }
            }
        }

        private void invalid(AnomalyType t, EnrollmentEvent e) {
            invalidCounts.merge(t, 1L, Long::sum);
            addBounded(invalidSamples.computeIfAbsent(t, k -> new TreeSet<>()), "event#%012d".formatted(e.eventId()));
        }

        Acc merge(Acc o) {
            events += o.events;
            valid += o.valid;
            o.students.keySet().forEach(k -> students.putIfAbsent(k, k));
            o.pairs.forEach((course, m) -> {
                Map<String, PairState> mine = pairs.computeIfAbsent(course, k -> new HashMap<>());
                m.forEach((sid, ps) -> mine.computeIfAbsent(students.get(sid), k -> new PairState()).merge(ps));
            });
            o.invalidCounts.forEach((t, n) -> invalidCounts.merge(t, n, Long::sum));
            o.invalidSamples.forEach((t, s) -> {
                TreeSet<String> mine = invalidSamples.computeIfAbsent(t, k -> new TreeSet<>());
                s.forEach(x -> addBounded(mine, x));
            });
            return this;
        }

        Report finish() {
            EnumMap<AnomalyType, Long> counts = new EnumMap<>(AnomalyType.class);
            EnumMap<AnomalyType, TreeSet<String>> samples = new EnumMap<>(AnomalyType.class);
            invalidCounts.forEach(counts::put);
            invalidSamples.forEach((t, s) -> samples.put(t, new TreeSet<>(s)));
            EnumMap<Level, Long> byLevel = new EnumMap<>(Level.class);
            List<CourseRate> rates = new ArrayList<>();

            for (String code : new TreeSet<>(catalog.keySet())) {
                long enrolled = 0, completed = 0;
                for (var e : pairs.getOrDefault(code, Map.of()).entrySet()) {
                    int m = e.getValue().mask;
                    String key = code + "/" + e.getKey();
                    boolean enr = (m & E) != 0, com = (m & C) != 0, drop = (m & D) != 0;
                    if (enr) enrolled++;
                    if (enr && com && !drop) completed++;
                    if (com && !enr)  anomaly(counts, samples, AnomalyType.COMPLETED_WITHOUT_ENROLLMENT, key);
                    if (com && drop)  anomaly(counts, samples, AnomalyType.COMPLETED_AND_DROPPED, key);
                    if (e.getValue().enrollCount > 1) anomaly(counts, samples, AnomalyType.DUPLICATE_ENROLLMENT, key);
                }
                if (enrolled > 0) byLevel.merge(catalog.get(code), enrolled, Long::sum);
                rates.add(new CourseRate(code, enrolled, completed));
            }

            Comparator<CourseRate> topOrder = Comparator
                .comparing((CourseRate r) -> r, (a, b) -> Long.compare(b.completed() * a.enrolled(), a.completed() * b.enrolled()))
                .thenComparing(Comparator.comparingLong(CourseRate::enrolled).reversed())
                .thenComparing(CourseRate::courseCode);
            List<CourseRate> top3 = rates.stream().filter(r -> r.enrolled() > 0).sorted(topOrder).limit(3).toList();

            List<Anomaly> sampleList = new ArrayList<>();
            samples.forEach((t, set) -> set.forEach(k -> sampleList.add(new Anomaly(t, k))));
            return new Report(events, valid, students.size(), List.copyOf(rates), top3,
                Collections.unmodifiableMap(byLevel), Collections.unmodifiableMap(counts), List.copyOf(sampleList));
        }

        private static void anomaly(EnumMap<AnomalyType, Long> counts, EnumMap<AnomalyType, TreeSet<String>> samples,
                                    AnomalyType t, String key) {
            counts.merge(t, 1L, Long::sum);
            addBounded(samples.computeIfAbsent(t, k -> new TreeSet<>()), key);
        }
    }

    static Collector<EnrollmentEvent, ?, Report> reportCollector(Map<String, Level> catalog) {
        return Collector.of(() -> new Acc(catalog), Acc::accept, Acc::merge, Acc::finish, Collector.Characteristics.UNORDERED);
    }

    static Report reportWithLoop(Iterator<EnrollmentEvent> events, Map<String, Level> catalog) {
        Acc acc = new Acc(catalog);
        while (events.hasNext()) acc.accept(events.next());
        return acc.finish();
    }

    static final class EventSource implements Iterator<EnrollmentEvent> {
        private final Random rnd; private final int students; private final List<String> codes;
        private int next; private long id = 1;
        private final ArrayDeque<EnrollmentEvent> buf = new ArrayDeque<>();
        final AtomicLong produced = new AtomicLong();

        EventSource(long seed, int students, Collection<String> codes) {
            this.rnd = new Random(seed); this.students = students; this.codes = new ArrayList<>(new TreeSet<>(codes));
        }

        @Override public boolean hasNext() {
            while (buf.isEmpty() && next < students) fill();
            return !buf.isEmpty();
        }

        @Override public EnrollmentEvent next() {
            if (!hasNext()) throw new NoSuchElementException();
            produced.incrementAndGet();
            return buf.poll();
        }

        private void put(String s, String c, EventType t) { buf.add(new EnrollmentEvent(id++, s, c, t)); }

        private void fill() {
            String s = "S%06d".formatted(next++);
            int n = 1 + rnd.nextInt(3);
            Set<String> chosen = new TreeSet<>();
            while (chosen.size() < n) chosen.add(codes.get(rnd.nextInt(codes.size())));
            for (String c : chosen) {
                int roll = rnd.nextInt(1000);
                if (roll >= 5) put(s, c, EventType.ENROLLED);
                if (roll >= 990) put(s, c, EventType.ENROLLED);
                int progress = 5 + rnd.nextInt(16);
                for (int i = 0; i < progress; i++) put(s, c, EventType.PROGRESS);
                int fin = rnd.nextInt(100);
                if (fin < 60) put(s, c, EventType.COMPLETED);
                else if (fin < 85) put(s, c, EventType.DROPPED);
                if (roll >= 5 && roll < 8) { put(s, c, EventType.COMPLETED); put(s, c, EventType.DROPPED); }
                if (roll >= 8 && roll < 10) put(s, "ZZ999", EventType.PROGRESS);
                if (roll >= 10 && roll < 11) put("  ", c, EventType.PROGRESS);
            }
        }
    }

    static Stream<EnrollmentEvent> stream(EventSource src) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(src, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    static void print(Report r) {
        System.out.printf("  событий всего=%d, валидных=%d, уникальных студентов=%d%n", r.events(), r.validEvents(), r.uniqueStudents());
        System.out.println("  completion rate по курсам (completed / enrolled):");
        r.courseRates().forEach(c -> System.out.printf("    %-5s %6d / %-6d = %5.1f%%%n", c.courseCode(), c.completed(), c.enrolled(), 100 * c.rate()));
        System.out.println("  TOP-3:");
        int i = 1;
        for (CourseRate c : r.top3()) System.out.printf("    #%d %-5s %5.1f%% (enrolled=%d)%n", i++, c.courseCode(), 100 * c.rate(), c.enrolled());
        System.out.println("  записей по уровням: " + r.enrollmentsByLevel());
        System.out.println("  аномалии (кол-во): " + r.anomalyCounts());
        System.out.println("  аномалии (первые " + SAMPLES_PER_TYPE + " на тип, отсортированы):");
        r.anomalySamples().forEach(a -> System.out.println("    " + a.type() + "  " + a.key()));
    }

    public static void main(String[] args) {
        Map<String, Level> catalog = new TreeMap<>();
        Catalog.sample().forEach(c -> catalog.put(c.code(), c.level()));
        final int STUDENTS = 100_000;
        final long SEED = 42;

        System.out.printf("Heap max: %d MB (запуск с -Xmx64m)%n%n", Runtime.getRuntime().maxMemory() / (1024 * 1024));

        System.out.println("== 1. Один проход по большому потоку (Stream + Collector) ==");
        EventSource src = new EventSource(SEED, STUDENTS, catalog.keySet());
        Stream<EnrollmentEvent> once = stream(src);
        Report streamed = once.collect(reportCollector(catalog));
        print(streamed);
        System.out.printf("  сгенерировано=%d, обработано=%d → каждое событие прочитано ровно один раз%n",
                          src.produced.get(), streamed.events());
        long pairs = streamed.courseRates().stream().mapToLong(CourseRate::enrolled).sum();
        System.out.printf("  в памяти держалось ~%d состояний пар и %d студентов вместо %d событий%n",
                          pairs, streamed.uniqueStudents(), streamed.events());
        try { once.collect(reportCollector(catalog)); throw new AssertionError(); }
        catch (IllegalStateException e) { System.out.println("  повторный проход невозможен: IllegalStateException: " + e.getMessage()); }

        System.out.println("\n== 2. Детерминизм ==");
        Report again = stream(new EventSource(SEED, STUDENTS, catalog.keySet())).collect(reportCollector(catalog));
        System.out.println("  тот же вход, второй запуск (stream):  отчёты равны = " + again.equals(streamed));
        Report loop = reportWithLoop(new EventSource(SEED, STUDENTS, catalog.keySet()), catalog);
        System.out.println("  тот же вход, обычный цикл:            отчёты равны = " + loop.equals(streamed));

        List<EnrollmentEvent> small = stream(new EventSource(7, 3_000, catalog.keySet())).toList();
        Report base = small.stream().collect(reportCollector(catalog));
        int ok = 0;
        for (int seed = 0; seed < 20; seed++) {
            List<EnrollmentEvent> shuffled = new ArrayList<>(small);
            Collections.shuffle(shuffled, new Random(seed));
            if (shuffled.parallelStream().collect(reportCollector(catalog)).equals(base)) ok++;
        }
        System.out.printf("  %d событий, 20 перемешиваний + parallelStream: %d/20 отчётов идентичны (порядок и разбиение не влияют)%n",
                          small.size(), ok);
        if (!again.equals(streamed) || !loop.equals(streamed) || ok != 20) throw new AssertionError();

        System.out.println("\n== 3. Что было бы при материализации потока ==");
        if (Runtime.getRuntime().maxMemory() <= 256L * 1024 * 1024) {
            try {
                List<EnrollmentEvent> all = stream(new EventSource(SEED, STUDENTS, catalog.keySet())).toList();
                System.out.println("  материализовано " + all.size() + " событий (куча больше ожидаемой)");
            } catch (OutOfMemoryError oom) {
                System.out.println("  toList() → OutOfMemoryError при -Xmx64m: O(событий) не подходит, O(пар) — подходит");
            }
        } else {
            System.out.println("  пропущено: запустите с -Xmx64m, чтобы увидеть OOM у toList()");
        }
        System.out.println("OK");
    }
}
