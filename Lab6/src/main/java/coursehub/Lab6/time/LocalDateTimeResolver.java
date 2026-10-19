package coursehub.Lab6.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeResolver {
    public static void run() {
        ZoneId nyZone = ZoneId.of("America/New_York");
        
        LocalDateTime missingTime = LocalDateTime.of(2024, 3, 10, 2, 30);

        ZonedDateTime resolved = ZonedDateTime.of(missingTime, nyZone);
        
        System.out.println("запрос локального времени: " + missingTime);
        System.out.println("java : " + resolved);

        System.out.println("\n + 1 час: " + resolved.plusHours(1));
        System.out.println(" + 1 день: " + resolved.plusDays(1));
    }

    public static void main(String[] args) { run(); }
}