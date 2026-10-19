package coursehub.Lab6.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WebinarSchedule {
    public static void run() {
        Instant webinarMoment = Instant.parse("2024-05-20T10:00:00Z");
        
        ZonedDateTime moscow = webinarMoment.atZone(ZoneId.of("Europe/Moscow"));
        ZonedDateTime london = webinarMoment.atZone(ZoneId.of("Europe/London"));
        ZonedDateTime tokyo = webinarMoment.atZone(ZoneId.of("Asia/Tokyo"));

        System.out.println("москва: " + moscow);
        System.out.println("лондон: " + london);
        System.out.println("токио: " + tokyo);
        
        System.out.println("\nсерверная зона по умолчанию (только для логов): " + ZoneId.systemDefault());
    }

    public static void main(String[] args) { run(); }
}