import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Task7 {
    static ImportResult parseFile(Path source) throws IOException {
        List<Course> courses = new ArrayList<>();
        List<ImportResult.LineError> errors = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Course c = CourseParser.parseLine(line);
                    if (!seenCodes.add(c.code())) {
                        errors.add(new ImportResult.LineError(number, line, "дубликат кода " + c.code()));
                    } else {
                        courses.add(c);
                    }
                } catch (IllegalArgumentException e) {
                    errors.add(new ImportResult.LineError(number, line, e.getMessage()));
                }
            }
        }
        if (errors.isEmpty()) {
            return new ImportResult.Imported(courses);
        }
        return new ImportResult.Rejected(errors); 
    }

    static ImportResult importCatalog(Path source, Path target) throws IOException {
        ImportResult result = parseFile(source);
        if (result instanceof ImportResult.Imported imported) {
            AtomicFile.replace(target, imported.courses());
        }
        return result;
    }

    static void print(ImportResult result) {
        if (result instanceof ImportResult.Imported ok) {
            System.out.println("  ИМПОРТИРОВАНО курсов: " + ok.courses().size());
        } else if (result instanceof ImportResult.Rejected bad) {
            System.out.println("  ОТКЛОНЕНО, ошибок: " + bad.errors().size());
            for (ImportResult.LineError e : bad.errors()) {
                System.out.println("    строка " + e.lineNumber() + ": " + e.message() + "   [" + e.line() + "]");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Demo.dir("task7");
        Path target = dir.resolve("published.txt");
        Files.writeString(target, "OLD-001;Старый курс;1000\n", StandardCharsets.UTF_8);

        System.out.println("1) Хороший файл");
        Path good = dir.resolve("good.csv");
        Files.writeString(good, "JAVA-101;Основы Java;5000\nCS-007;Алгоритмы;7500\n\nSQL-201;Базы данных;4200\n",
                StandardCharsets.UTF_8);
        ImportResult r1 = importCatalog(good, target);
        print(r1);
        Demo.check(r1 instanceof ImportResult.Imported, "результат Imported");
        Demo.check(Files.readString(target, StandardCharsets.UTF_8).contains("SQL-201"), "каталог опубликован");

        String published = Files.readString(target, StandardCharsets.UTF_8);

        System.out.println("2) Файл с ошибками");
        Path bad = dir.resolve("bad.csv");
        Files.writeString(bad, String.join("\n",
                "JAVA-101;Основы Java;5000",   
                "java-102;Маленькими;3000",   
                "CS-007;Алгоритмы;7500",      
                "SQL-201;Базы данных;много",  
                "CS-007;Дубль;100",            
                "только одно поле"             
        ) + "\n", StandardCharsets.UTF_8);
        ImportResult r2 = importCatalog(bad, target);
        print(r2);
        Demo.check(r2 instanceof ImportResult.Rejected rej && rej.errors().size() == 4, "собраны все 4 ошибки с номерами строк");
        Demo.check(Files.readString(target, StandardCharsets.UTF_8).equals(published), "опубликованный каталог не изменился");
    }
}
