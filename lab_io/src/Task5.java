import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task5 {

    static final int MAGIC = 0x43525345; 
    static final short VERSION = 1;

    static void write(Path file, List<Course> courses) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeInt(MAGIC);
            out.writeShort(VERSION);
            out.writeInt(courses.size());
            for (Course c : courses) {
                out.writeUTF(c.code());
                out.writeUTF(c.title());
                out.writeInt(c.price());
            }
        }
    }

    static List<Course> read(Path file) throws IOException, CatalogException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            int magic = in.readInt();
            if (magic != MAGIC) {
                throw new CatalogException("Это не наш файл: неверный magic " + Integer.toHexString(magic));
            }
            short version = in.readShort();
            if (version != VERSION) {
                throw new CatalogException("Неизвестная версия формата: " + version);
            }
            int count = in.readInt();
            if (count < 0 || count > 1_000_000) {
                throw new CatalogException("Неправдоподобное количество записей: " + count);
            }
            List<Course> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(new Course(in.readUTF(), in.readUTF(), in.readInt()));
            }
            if (in.read() != -1) {
                throw new CatalogException("После последней записи в файле лишние байты");
            }
            return result;
        } catch (EOFException e) {
            throw new CatalogException("Файл обрезан: данные закончились раньше времени", e);
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Demo.dir("task5");
        List<Course> courses = List.of(
                new Course("JAVA-101", "Основы Java", 5000),
                new Course("CS-007", "Алгоритмы", 7500));

        System.out.println("1) Нормальный файл");
        Path good = dir.resolve("good.bin");
        write(good, courses);
        List<Course> back = read(good);
        System.out.println("  прочитано: " + back);
        Demo.check(back.equals(courses), "прочитали ровно то, что записали");

        byte[] bytes = Files.readAllBytes(good);

        System.out.println("2) Неизвестная версия");
        Path v99 = dir.resolve("v99.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(v99))) {
            out.writeInt(MAGIC);
            out.writeShort(99);
            out.writeInt(0);
        }
        Demo.expectFailure("версия 99", () -> read(v99));

        System.out.println("3) Обрезанный файл");
        Path cut = dir.resolve("cut.bin");
        Files.write(cut, Arrays.copyOf(bytes, bytes.length - 5));
        Demo.expectFailure("обрезали последние 5 байт", () -> read(cut));

        System.out.println("4) Чужой файл и лишние байты");
        byte[] alien = bytes.clone();
        alien[0] = 0;
        Files.write(dir.resolve("alien.bin"), alien);
        Demo.expectFailure("испорченный magic", () -> read(dir.resolve("alien.bin")));
        byte[] extra = Arrays.copyOf(bytes, bytes.length + 3);
        Files.write(dir.resolve("extra.bin"), extra);
        Demo.expectFailure("лишние байты в конце", () -> read(dir.resolve("extra.bin")));
    }
}
