import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Demo {

    public interface Action {
        void run() throws Exception;
    }

    public static Path dir(String name) throws IOException {
        Path d = Path.of("demo", name);
        Files.createDirectories(d);
        return d;
    }

    public static void expectFailure(String title, Action action) {
        try {
            action.run();
        } catch (Exception e) {
            System.out.println("  [ожидаемая ошибка] " + title + " -> "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return;
        }
        throw new AssertionError("Ожидали ошибку, но её не было: " + title);
    }

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("ПРОВЕРКА НЕ ПРОШЛА: " + message);
        }
        System.out.println("  [проверка ок] " + message);
    }
}
