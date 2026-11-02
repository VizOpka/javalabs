import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public class Task9 {

    static final int MAGIC = 0x43525345;
    static final int CURRENT_VERSION = 2;
    static final int MAX_PAYLOAD = 10_000_000; 

    record CourseV1(String code, String title, int priceRub) { }

    record CourseV2(String code, String title, long priceKopecks, String currency) { }

    static CourseV2 migrate(CourseV1 c) {
        return new CourseV2(c.code(), c.title(), c.priceRub() * 100L, "RUB");
    }


    static byte[] payloadV1(List<CourseV1> list) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(list.size());
            for (CourseV1 c : list) {
                out.writeUTF(c.code());
                out.writeUTF(c.title());
                out.writeInt(c.priceRub());
            }
        }
        return bytes.toByteArray();
    }

    static byte[] payloadV2(List<CourseV2> list) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(list.size());
            for (CourseV2 c : list) {
                out.writeUTF(c.code());
                out.writeUTF(c.title());
                out.writeLong(c.priceKopecks());
                out.writeUTF(c.currency());
            }
        }
        return bytes.toByteArray();
    }

    static void writeFile(Path file, int version, byte[] payload) throws IOException {
        CRC32 crc = new CRC32();
        crc.update(payload);
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeInt(MAGIC);
            out.writeShort(version);
            out.writeInt(payload.length);
            out.writeInt((int) crc.getValue());
            out.write(payload);
        }
    }


    static List<CourseV2> read(Path file) throws IOException, CatalogException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            if (in.readInt() != MAGIC) {
                throw new CatalogException("Неверный magic: это не файл каталога");
            }
            int version = in.readUnsignedShort();
            if (version > CURRENT_VERSION) {
                throw new CatalogException("Версия " + version + " новее поддерживаемой ("
                        + CURRENT_VERSION + "): обновите программу");
            }
            if (version < 1) {
                throw new CatalogException("Неизвестная версия " + version);
            }
            int length = in.readInt();
            if (length < 0 || length > MAX_PAYLOAD) {
                throw new CatalogException("Неправдоподобная длина данных: " + length);
            }
            int expectedCrc = in.readInt();

            byte[] payload = new byte[length];
            in.readFully(payload);                  
            if (in.read() != -1) {
                throw new CatalogException("После данных в файле лишние байты");
            }

            CRC32 crc = new CRC32();
            crc.update(payload);
            if ((int) crc.getValue() != expectedCrc) {
                throw new CatalogException("Контрольная сумма не сошлась: файл повреждён");
            }

            if (version == 1) {
                List<CourseV2> migrated = new ArrayList<>();
                for (CourseV1 old : parseV1(payload)) {
                    migrated.add(migrate(old));
                }
                return migrated;
            }
            return parseV2(payload);
        } catch (EOFException e) {
            throw new CatalogException("Файл обрезан или внутри данных не хватает байт", e);
        }
    }

    static List<CourseV1> parseV1(byte[] payload) throws IOException, CatalogException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            int count = in.readInt();
            if (count < 0) throw new CatalogException("Отрицательное количество записей");
            List<CourseV1> list = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                list.add(new CourseV1(in.readUTF(), in.readUTF(), in.readInt()));
            }
            return list;
        }
    }

    static List<CourseV2> parseV2(byte[] payload) throws IOException, CatalogException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            int count = in.readInt();
            if (count < 0) throw new CatalogException("Отрицательное количество записей");
            List<CourseV2> list = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                list.add(new CourseV2(in.readUTF(), in.readUTF(), in.readLong(), in.readUTF()));
            }
            return list;
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Demo.dir("task9");
        List<CourseV1> oldData = List.of(
                new CourseV1("JAVA-101", "Основы Java", 5000),
                new CourseV1("CS-007", "Алгоритмы", 7500));
        List<CourseV2> newData = List.of(
                new CourseV2("JAVA-101", "Основы Java", 500_000, "RUB"),
                new CourseV2("CS-007", "Алгоритмы", 750_000, "RUB"));

        System.out.println("1) Старый файл версии 1 читается через миграцию");
        Path v1 = dir.resolve("v1.bin");
        writeFile(v1, 1, payloadV1(oldData));
        List<CourseV2> fromV1 = read(v1);
        System.out.println("  " + fromV1);
        Demo.check(fromV1.equals(newData), "5000 руб превратились в 500000 копеек + RUB");

        System.out.println("2) Файл версии 2 читается как есть");
        Path v2 = dir.resolve("v2.bin");
        writeFile(v2, 2, payloadV2(newData));
        Demo.check(read(v2).equals(newData), "v2 прочитан без изменений");

        System.out.println("3) Будущая версия 3");
        Path v3 = dir.resolve("v3.bin");
        writeFile(v3, 3, payloadV2(newData));
        Demo.expectFailure("версия 3 не читается как старая", () -> read(v3));

        System.out.println("4) Повреждение одного байта данных");
        byte[] bytes = Files.readAllBytes(v2);
        bytes[20] ^= 0x55;                                  
        Path broken = dir.resolve("broken.bin");
        Files.write(broken, bytes);
        Demo.expectFailure("контрольная сумма", () -> read(broken));

        System.out.println("5) Обрезанный файл");
        Path cut = dir.resolve("cut.bin");
        Files.write(cut, Arrays.copyOf(bytes, bytes.length - 7));
        Demo.expectFailure("обрезан", () -> read(cut));
    }
}
