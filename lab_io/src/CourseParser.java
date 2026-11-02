import java.util.regex.Pattern;

public class CourseParser {

    private static final Pattern CODE = Pattern.compile("[A-Z]{2,8}-[0-9]{3}");

    public static Course parseLine(String line) {
        String[] parts = line.split(";", -1); 
        if (parts.length != 3) {
            throw new IllegalArgumentException("нужно 3 поля через ';', а найдено " + parts.length);
        }
        String code = parts[0].strip();
        String title = parts[1].strip();
        String priceText = parts[2].strip();

        if (!CODE.matcher(code).matches()) {
            throw new IllegalArgumentException("неверный код курса: '" + code + "'");
        }
        if (title.isEmpty()) {
            throw new IllegalArgumentException("пустое название");
        }
        int price;
        try {
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("цена не число: '" + priceText + "'");
        }
        if (price < 0) {
            throw new IllegalArgumentException("цена отрицательная: " + price);
        }
        return new Course(code, title, price);
    }
}
