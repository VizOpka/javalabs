package coursehub.Lab6.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

    @Test
    public void testMoneyAllocation() {
        Money money = new Money(new BigDecimal("100.00"), "USD");
        
        Money[] parts = money.allocate(3);
        
        assertEquals(new BigDecimal("33.34"), parts[0].getAmount());
        assertEquals(new BigDecimal("33.33"), parts[1].getAmount());
        assertEquals(new BigDecimal("33.33"), parts[2].getAmount());

        BigDecimal sum = parts[0].getAmount()
                .add(parts[1].getAmount())
                .add(parts[2].getAmount());
        assertEquals(0, sum.compareTo(new BigDecimal("100.00")), "сумма частей должна равняться исходной сумме");
    }

    @Test
    public void testCurrencyMismatch() {
        Money usd = new Money(new BigDecimal("100.00"), "USD");
        Money eur = new Money(new BigDecimal("50.00"), "EUR");
        
        assertThrows(IllegalArgumentException.class, () -> {
            usd.add(eur);
        }, "нельзя складывать разные валюты");
    }
}