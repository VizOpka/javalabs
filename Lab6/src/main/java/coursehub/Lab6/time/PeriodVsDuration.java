package coursehub.Lab6.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PeriodVsDuration {
    public static void run() {

        Period oneDayPeriod = Period.ofDays(1);
        
        Duration twentyFourHours = Duration.ofHours(24);

        ZonedDateTime beforeDst = ZonedDateTime.of(LocalDateTime.of(2024, 3, 10, 0, 0), ZoneId.of("America/New_York"));
        
        ZonedDateTime plusPeriod = beforeDst.plus(oneDayPeriod);
        
        ZonedDateTime plusDuration = beforeDst.plus(twentyFourHours);

        System.out.println("исходное время: " + beforeDst);
        System.out.println("плюс 1 календарный день (Period): " + plusPeriod);
        System.out.println("плюс 24 часа (Duration): " + plusDuration);

        System.out.println("\nвывод: для срока подписки (дней/месяцев) используем Period. для таймаута Duration.");
    }

    public static void main(String[] args) {run();}
}