import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task4 {

    static Stream<String> badLines(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines;
        }
    }

    static long countNotBlank(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.filter(s -> !s.isBlank()).count();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    static List<String> firstLines(Path file, int n) throws IOException {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.limit(n).collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Demo.dir("task4");
        Path file = dir.resolve("lines.txt");
        Files.writeString(file, "первая\n\nвторая\nтретья\n", StandardCharsets.UTF_8);

        System.out.println("1) Ошибочный возврат stream наружу");
        Stream<String> broken = badLines(file);
        Demo.expectFailure("работа с уже закрытым stream", () -> broken.count());

        System.out.println("2) Правильно: ресурс внутри метода");
        System.out.println("  непустых строк: " + countNotBlank(file));
        System.out.println("  первые 2: " + firstLines(file, 2));
        Demo.check(countNotBlank(file) == 3, "непустых строк 3");

        System.out.println("3) Ошибка чтения прямо во время потока");
        Path bad = dir.resolve("bad-utf8.txt");
        Files.write(bad, new byte[]{(byte) 0xFF, (byte) 0xFE, 'a', '\n'});
        Demo.expectFailure("файл с битым UTF-8", () -> countNotBlank(bad));
    }
}
