package coursehub.Lab6.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {
    public static void run() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        
        String[] inputs = {"15.05.2024", "31.02.2024", "15.05.2024 extra", "abc"};
        
        for (String input : inputs) {
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                System.out.println("успех: " + input + " -> " + date);
            } catch (DateTimeParseException e) {
                System.out.println("ошибка в поле 'дата'. исходное значение: '" + input + "'. причина: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {run();}
}