import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class Task1 {

    static void saveCatalog(Path file, List<Course> courses) throws CatalogException {
        StringBuilder sb = new StringBuilder();
        for (Course c : courses) {
            sb.append(c.toLine()).append("\n");
        }
        try {
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CatalogException("Не удалось записать каталог: " + file, e);
        }
    }

    static String readCatalog(Path file) throws CatalogException {
        if (Files.isDirectory(file)) {
            throw new CatalogException("Ожидался файл, но по этому пути каталог: " + file);
        }
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new CatalogException("Файл каталога не найден: " + file, e);
        } catch (MalformedInputException e) {
            throw new CatalogException("Файл не является корректным UTF-8: " + file, e);
        } catch (IOException e) {
            throw new CatalogException("Не удалось прочитать каталог: " + file, e);
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Demo.dir("task1");
        Path file = dir.resolve("catalog.txt");
        List<Course> courses = List.of(
                new Course("JAVA-101", "Основы Java", 5000),
                new Course("CS-007", "Алгоритмы", 7500),
                new Course("SQL-201", "Базы данных", 4200));

        System.out.println("1) Нормальный сценарий: записали и прочитали");
        saveCatalog(file, courses);
        String text = readCatalog(file);
        System.out.print(text);
        Demo.check(text.contains("Основы Java"), "кириллица прочиталась без искажений");
        Demo.check(text.lines().count() == 3, "в файле 3 строки");

        System.out.println("2) Файла нет");
        Demo.expectFailure("чтение несуществующего файла",
                () -> readCatalog(dir.resolve("net-takogo.txt")));

        System.out.println("3) Вместо файла - каталог");
        Path folder = dir.resolve("catalog-dir");
        Files.createDirectories(folder);
        Demo.expectFailure("чтение каталога как файла", () -> readCatalog(folder));
        Demo.expectFailure("запись в каталог как в файл", () -> saveCatalog(folder, courses));

        System.out.println("4) Файл не в UTF-8");
        Files.write(dir.resolve("cp1251.txt"), "Привет".getBytes(Charset.forName("windows-1251")));
        Demo.expectFailure("файл в windows-1251", () -> readCatalog(dir.resolve("cp1251.txt")));
    }
}
