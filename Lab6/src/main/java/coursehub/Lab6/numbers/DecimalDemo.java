package coursehub.Lab6.numbers;

import java.math.BigDecimal;

public class DecimalDemo {
    public static void run() {
        
        System.out.println("double 0.1 + 0.2 = " + (0.1 + 0.2)); 

        
        BigDecimal bad = new BigDecimal(0.1);
        System.out.println("new BigDecimal(0.1): " + bad); 

       
        BigDecimal good1 = new BigDecimal("0.1");
        BigDecimal good2 = BigDecimal.valueOf(0.1);
        System.out.println("BigDecimal(\"0.1\"): " + good1);
        System.out.println("BigDecimal.valueOf(0.1): " + good2);
        System.out.println("0.1 == valueof(0.1) "+ good1.equals(good2));
        
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal total = price.add(price).add(price); 
        System.out.println("за 3 курса: " + total);
    }
    public static void main(String[] args) {run();}
}