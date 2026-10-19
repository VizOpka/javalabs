package coursehub.Lab6.domain;

import java.math.BigDecimal;
import java.time.*;

public class PricingService {
    public static void run() {

        ZoneId organizerZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime startMoscow = ZonedDateTime.of(2024, 2, 29, 10, 0, 0, 0, organizerZone); 
        Instant startInstant = startMoscow.toInstant();

        LocalDate deadlineDate = startMoscow.toLocalDate().minus(Period.ofDays(7));
        ZonedDateTime deadlineMoscow = deadlineDate.atStartOfDay(organizerZone);
        Instant deadlineInstant = deadlineMoscow.toInstant();

        ZoneId userZone = ZoneId.of("America/New_York");
        Instant purchaseTime = Instant.parse("2024-02-20T12:00:00Z"); 

        BigDecimal basePriceAmount = new BigDecimal("100.00");
        Money basePrice = new Money(basePriceAmount, "USD");
        Money finalPrice;

        if (purchaseTime.isBefore(deadlineInstant)) {
            BigDecimal discountAmount = basePriceAmount.multiply(new BigDecimal("0.10"));
            finalPrice = basePrice.subtract(new Money(discountAmount, "USD"));
            System.out.println("скидка применена");
        } else {
            finalPrice = basePrice;
            System.out.println("скидка не применена");
        }

        System.out.println("цена для пользователя: " + finalPrice);

        int totalLessons = 10;
        int passedLessons = 4;
        int remainingLessons = totalLessons - passedLessons;
        
        Money[] refundParts = finalPrice.allocate(totalLessons);
        Money refund = new Money(new BigDecimal("0.00"), "USD");
        
        for (int i = passedLessons; i < totalLessons; i++) {
            refund = refund.add(refundParts[i]);
        }

        System.out.println("возврат за " + remainingLessons + " занятий: " + refund);
        
        System.out.println("Старт вебинара для пользователя (Нью-Йорк): " + startInstant.atZone(userZone));
    }

    public static void main(String[] args) { run(); }
}