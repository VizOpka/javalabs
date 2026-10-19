package coursehub.Lab6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountAllocator {
    public static void run() {
        BigDecimal totalDiscount = new BigDecimal("10.00");
        int itemsCount = 3;

        BigDecimal perItem = totalDiscount.divide(BigDecimal.valueOf(itemsCount), 2, RoundingMode.DOWN);
        BigDecimal distributed = perItem.multiply(BigDecimal.valueOf(itemsCount));
        
        BigDecimal remainder = totalDiscount.subtract(distributed);

        System.out.println("скидка на позицию: " + perItem);
        System.out.println("остаток: " + remainder);

        BigDecimal firstItemDiscount = perItem.add(remainder);
        System.out.println("скидка на 1 позицию: " + firstItemDiscount);
        System.out.println("скидка на 2 и 3 позицию: " + perItem);
        
        System.out.println("итоговая сумма скидки: " + firstItemDiscount.add(perItem).add(perItem));
    }
    public static void main(String[] args) {run();}
}