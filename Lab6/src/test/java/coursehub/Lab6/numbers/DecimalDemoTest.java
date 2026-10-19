package coursehub.Lab6.numbers;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class DecimalDemoTest {

    @Test
    public void testDoubleProblem() {
        double result = 0.1 + 0.2;
        assertNotEquals(0.3, result, "double не может точно сложить 0.1 и 0.2");
    }

    @Test
    public void testBigDecimalSolution() {

        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        BigDecimal result = a.add(b);
        
        assertEquals(0, result.compareTo(new BigDecimal("0.3")), "BigDecimal должен сложить точно");
    }
}