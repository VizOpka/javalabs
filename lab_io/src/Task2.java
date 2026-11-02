import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Task2 {

    public static void main(String[] args) throws IOException {
        Path dir = Demo.dir("task2");
        Path in = dir.resolve("text.txt");
        Path out = dir.resolve("text-upper.txt");
        Files.writeString(in, "Привет, мир!\nJava - это просто.\n\nПоследняя строка\n", StandardCharsets.UTF_8);

        long lines = 0;
        long chars = 0;
        long bytesOfText = 0;
        try (BufferedReader reader = Files.newBufferedReader(in, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                chars += line.length();                                 
                bytesOfText += line.getBytes(StandardCharsets.UTF_8).length; 
                writer.write(line.toUpperCase());
                writer.newLine();
            }
        }

        System.out.println("Строк: " + lines);
        System.out.println("Символов (без переводов строк): " + chars);
        System.out.println("Байт тех же строк в UTF-8: " + bytesOfText);
        System.out.println("Размер файла на диске, байт: " + Files.size(in) + " (с переводами строк)");
        System.out.println("Результат:");
        System.out.print(Files.readString(out, StandardCharsets.UTF_8));

        byte[] raw = Files.readAllBytes(in);
        String right = new String(raw, StandardCharsets.UTF_8);
        String wrong = new String(raw, StandardCharsets.ISO_8859_1);
        System.out.println("UTF-8:      " + right.lines().findFirst().get());
        System.out.println("ISO-8859-1: " + wrong.lines().findFirst().get());
        Demo.check(bytesOfText > chars, "байт больше, чем символов (кириллица = 2 байта на символ)");
        Demo.check(lines == 4, "посчитано 4 строки (включая пустую)");
    }
}
