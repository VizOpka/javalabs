package coursehub.Lab6.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {

        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public Money add(Money other) {
        checkCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money percentage(BigDecimal percent) {
  
        return new Money(this.amount.multiply(percent), this.currency);
    }

    public Money[] allocate(int parts) {
        BigDecimal base = this.amount.divide(BigDecimal.valueOf(parts), 2, RoundingMode.DOWN);
        Money[] result = new Money[parts];
        BigDecimal totalDistributed = base.multiply(BigDecimal.valueOf(parts));
        BigDecimal remainder = this.amount.subtract(totalDistributed);

        for (int i = 0; i < parts; i++) {
            BigDecimal partAmount = (i == 0) ? base.add(remainder) : base;
            result[i] = new Money(partAmount, this.currency);
        }
        return result;
    }

    private void checkCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("запрещено смешивать валюты: " + currency + " и " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() { return Objects.hash(amount, currency); }

    @Override
    public String toString() { return amount + " " + currency; }

    public static void run() {
        Money price = new Money(new BigDecimal("100.00"), "USD");
        Money discount = new Money(new BigDecimal("10.00"), "USD");
        
        System.out.println("цена: " + price);
        System.out.println("цена со скидкой: " + price.subtract(discount));
        System.out.println("10% от цены: " + price.percentage(new BigDecimal("0.10")));
        
        Money[] parts = price.allocate(3);
        System.out.println("разделили 100 на 3 части: " + parts[0] + ", " + parts[1] + ", " + parts[2]);
        
        try {
            price.add(new Money(new BigDecimal("5.00"), "EUR"));
        } catch (IllegalArgumentException e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }

    public static void main(String[] args) { run(); }
}