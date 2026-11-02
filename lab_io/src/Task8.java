import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Task8 {

    record StreamStats(long lines, long valid, long invalid, long totalPrice,
                       List<ImportResult.LineError> firstErrors, boolean cancelled) { }

    static StreamStats scan(Path file, int maxStoredErrors, long cancelAfterErrors) throws IOException {
        long lines = 0, valid = 0, invalid = 0, totalPrice = 0;
        List<ImportResult.LineError> firstErrors = new ArrayList<>(); 
        boolean cancelled = false;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Course c = CourseParser.parseLine(line);
                    valid++;
                    totalPrice += c.price();
                } catch (IllegalArgumentException e) {
                    invalid++;                                  
                    if (firstErrors.size() < maxStoredErrors) { 
                        firstErrors.add(new ImportResult.LineError((int) lines, line, e.getMessage()));
                    }
                    if (invalid >= cancelAfterErrors) {        
                        cancelled = true;
                        break;
                    }
                }
            }
        }
        return new StreamStats(lines, valid, invalid, totalPrice, firstErrors, cancelled);
    }

    static void generate(Path file, int lineCount) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 1; i <= lineCount; i++) {
                if (i % 1000 == 0) {
                    writer.write("ЭТО-НЕ-КУРС");
                } else {
                    writer.write("JAVA-" + String.format("%03d", i % 1000) + ";Курс номер " + i + ";" + (1000 + i % 50 * 100));
                }
                writer.newLine();
            }
        }
    }

    static void print(StreamStats s) {
        System.out.println("  прочитано строк: " + s.lines() + ", верных: " + s.valid()
                + ", ошибочных: " + s.invalid() + ", сумма цен: " + s.totalPrice());
        System.out.println("  отменён: " + s.cancelled() + ", сохранено ошибок: " + s.firstErrors().size());
        for (ImportResult.LineError e : s.firstErrors()) {
            System.out.println("    строка " + e.lineNumber() + ": " + e.message());
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Demo.dir("task8");
        Path big = dir.resolve("big.csv");
        int total = 1_000_000;

        System.out.println("Максимум памяти JVM: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " МБ");
        generate(big, total);
        System.out.println("Размер файла: " + Files.size(big) / 1024 / 1024 + " МБ");

        System.out.println("1) Весь файл, хранить первые 5 ошибок, порог отмены не достигается");
        StreamStats full = scan(big, 5, Long.MAX_VALUE);
        print(full);
        Demo.check(full.lines() == total, "прочитан весь файл");
        Demo.check(full.invalid() == 1000 && full.firstErrors().size() == 5, "ошибок 1000, а хранится только 5");

        System.out.println("2) Отмена после 50 ошибок");
        StreamStats stopped = scan(big, 5, 50);
        print(stopped);
        Demo.check(stopped.cancelled() && stopped.invalid() == 50, "остановились ровно на пороге");
        Demo.check(stopped.lines() < total, "файл дочитан не до конца");

        Files.delete(big); 
    }
}
