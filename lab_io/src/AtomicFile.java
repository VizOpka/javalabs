import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class AtomicFile {

    public enum Mode { ATOMIC, FALLBACK }

    public static Mode replace(Path target, List<Course> courses) throws IOException {
        return replace(target, courses, false);
    }

    static Mode replace(Path target, List<Course> courses, boolean pretendNoAtomic) throws IOException {
        Path dir = target.toAbsolutePath().getParent();
        Path tmp = Files.createTempFile(dir, "catalog-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                for (Course c : courses) {
                    writer.write(c.toLine()); 
                    writer.newLine();
                }
                writer.flush();               
            }                              
            try {
                if (pretendNoAtomic) {
                    throw new AtomicMoveNotSupportedException(tmp.toString(), target.toString(), "имитация");
                }
                Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE);
                return Mode.ATOMIC;
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
                return Mode.FALLBACK;
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
