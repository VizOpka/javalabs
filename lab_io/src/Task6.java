import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Task6 {

    static long tmpFilesIn(Path dir) throws IOException {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(p -> p.getFileName().toString().endsWith(".tmp")).count();
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Demo.dir("task6");
        Path target = dir.resolve("catalog.txt");
        Files.deleteIfExists(target);

        List<Course> oldCourses = List.of(new Course("OLD-001", "Старый курс", 1000));
        List<Course> newCourses = List.of(
                new Course("JAVA-101", "Основы Java", 5000),
                new Course("CS-007", "Алгоритмы", 7500));

        System.out.println("1) Первая запись и замена каталога");
        AtomicFile.replace(target, oldCourses);
        AtomicFile.Mode mode = AtomicFile.replace(target, newCourses);
        System.out.println("  режим: " + mode);
        String text = Files.readString(target, StandardCharsets.UTF_8);
        System.out.print(text);
        Demo.check(mode == AtomicFile.Mode.ATOMIC, "замена выполнена атомарно");
        Demo.check(text.contains("JAVA-101") && !text.contains("OLD-001"), "в целевом файле новый каталог");
        Demo.check(tmpFilesIn(dir) == 0, "временных файлов не осталось");

        System.out.println("2) Ошибка посреди записи (в списке null)");
        String before = Files.readString(target, StandardCharsets.UTF_8);
        List<Course> broken = Arrays.asList(newCourses.get(0), null, newCourses.get(1));
        Demo.expectFailure("null вместо курса", () -> AtomicFile.replace(target, broken));
        Demo.check(Files.readString(target, StandardCharsets.UTF_8).equals(before), "старый файл остался целым");
        Demo.check(tmpFilesIn(dir) == 0, "временный файл удалён после ошибки");

        System.out.println("3) Файловая система без atomic move (имитация)");
        AtomicFile.Mode mode2 = AtomicFile.replace(target, oldCourses, true);
        System.out.println("  режим: " + mode2);
        Demo.check(mode2 == AtomicFile.Mode.FALLBACK, "сработал запасной вариант, и мы об этом знаем");
        Demo.check(Files.readString(target, StandardCharsets.UTF_8).contains("OLD-001"), "файл заменён");
        Demo.check(tmpFilesIn(dir) == 0, "временных файлов не осталось");
    }
}
