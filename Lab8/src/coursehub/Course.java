package coursehub;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public record Course(String code, String title, Level level, Duration duration, List<CourseModule> modules) {
    public Course {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(duration, "duration");
        if (code.isBlank()) throw new IllegalArgumentException("code is blank");
        if (duration.isNegative()) throw new IllegalArgumentException("negative duration");
        modules = List.copyOf(modules);
    }

    public long minutes() { return duration.toMinutes(); }
}
