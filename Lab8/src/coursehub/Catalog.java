package coursehub;

import java.time.Duration;
import java.util.List;

public final class Catalog {
    private Catalog() {}

    static CourseModule m(String name, String... topics) {
        return new CourseModule(name, List.of(topics));
    }

    static Course course(String code, String title, Level level, long minutes, CourseModule... modules) {
        return new Course(code, title, level, Duration.ofMinutes(minutes), List.of(modules));
    }

    public static List<Course> sample() {
        return List.of(
            course("J101", "Java Basics", Level.BEGINNER, 600,
                m("Syntax", " Java ", "Variables"), m("OOP", "Classes", "JAVA")),
            course("P101", "Python Basics", Level.BEGINNER, 600,
                m("Syntax", "Python", "Variables"), m("Scripts", "  Command   Line ", "python")),
            course("G100", "Git Essentials", Level.BEGINNER, 240,
                m("Branching", "Git", "Merge"), m("Remote", "git", "Collaboration")),
            course("S201", "SQL Fundamentals", Level.INTERMEDIATE, 480,
                m("Queries", "SQL ", " Joins"), m("Design", "Normalization", "sql")),
            course("J201", "Java Streams", Level.INTERMEDIATE, 480,
                m("Lambdas", "Java", "Lambdas"),
                m("Streams", "Streams  API", "streams api", "\uFF2A\uFF21\uFF36\uFF21", "   ")),
            course("W201", "Web Development", Level.INTERMEDIATE, 900,
                m("HTTP", "HTTP", "REST"), m("Frontend", "HTML", "css")),
            course("D301", "Distributed Systems", Level.ADVANCED, 1200,
                m("Consensus", "Raft", "Paxos"), m("Messaging", "Kafka", "Streams API")),
            course("J301", "JVM Internals", Level.ADVANCED, 900,
                m("GC", "JVM", "Garbage Collection"), m("JIT", "jvm", "JIT")),
            course("K301", "Kafka In Depth", Level.ADVANCED, 900,
                m("Brokers", "Kafka", "Replication"), m("Streams", "kafka streams", "Streams API")),
            course("E000", "Empty Placeholder", Level.BEGINNER, 60)
        );
    }
}
