package coursehub.Lab6.time;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.*;

public class DateParserTest {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    @Test
    public void testValidDate() {
        LocalDate date = LocalDate.parse("15.05.2024", formatter);
        assertEquals(2024, date.getYear());
        assertEquals(5, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());
    }

    @Test
    public void testInvalidDate() {
        assertThrows(DateTimeParseException.class, () -> {
            LocalDate.parse("31.02.2024", formatter);
        });
    }

    @Test
    public void testExtraText() {
        assertThrows(DateTimeParseException.class, () -> {
            LocalDate.parse("15.05.2024 extra", formatter);
        });
    }
}