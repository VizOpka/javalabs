import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class Task3 {

    static final List<String> events = new ArrayList<>();

    static class LoggedReader extends BufferedReader {
        LoggedReader(Reader r) { super(r); }

        @Override
        public void close() throws IOException {
            events.add("close reader");
            super.close();
        }
    }

    static class LoggedWriter extends BufferedWriter {
        LoggedWriter(Writer w) { super(w); }

        @Override
        public void close() throws IOException {
            events.add("close writer");
            super.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Demo.dir("task3");
        Path in = dir.resolve("in.txt");
        Path out = dir.resolve("out.txt");
        Files.writeString(in, "строка 1\nстрока 2\nстрока 3\nстрока 4\n", StandardCharsets.UTF_8);

        LoggedReader reader = new LoggedReader(new InputStreamReader(Files.newInputStream(in), StandardCharsets.UTF_8));
        events.add("open reader");
        LoggedWriter writer = new LoggedWriter(new OutputStreamWriter(Files.newOutputStream(out), StandardCharsets.UTF_8));
        events.add("open writer");

        try (reader; writer) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (number == 3) {
                    events.add("ИСКЛЮЧЕНИЕ на строке 3");
                    throw new IllegalStateException("Сбой в середине pipeline (строка 3)");
                }
                writer.write(line.toUpperCase());
                writer.newLine();
                events.add("обработана строка " + number);
            }
        } catch (IllegalStateException e) {
            System.out.println("Поймали: " + e.getMessage());
        }

        System.out.println("Журнал событий:");
        for (String e : events) {
            System.out.println("  " + e);
        }

        List<String> expected = List.of("open reader", "open writer",
                "обработана строка 1", "обработана строка 2",
                "ИСКЛЮЧЕНИЕ на строке 3", "close writer", "close reader");
        Demo.check(events.equals(expected), "close вызван у обоих, порядок: writer, затем reader");

        try {
            reader.read();
            throw new AssertionError("reader должен быть закрыт");
        } catch (IOException e) {
            System.out.println("  reader закрыт: " + e.getMessage());
        }
        try {
            writer.write("x");
            throw new AssertionError("writer должен быть закрыт");
        } catch (IOException e) {
            System.out.println("  writer закрыт: " + e.getMessage());
        }

        String result = Files.readString(out, StandardCharsets.UTF_8);
        System.out.println("Файл out.txt после сбоя:\n" + result);
        Demo.check(result.contains("СТРОКА 1") && result.contains("СТРОКА 2"), "две успевшие строки сохранены");
    }
}
