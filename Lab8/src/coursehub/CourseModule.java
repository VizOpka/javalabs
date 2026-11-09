package coursehub;

import java.util.List;
import java.util.Objects;

public record CourseModule(String name, List<String> topics) {
    public CourseModule {
        Objects.requireNonNull(name, "name");
        topics = List.copyOf(topics);
    }
}
