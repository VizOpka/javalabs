package coursehub.Lab6.numbers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegerPolicyTest {

    @Test
    public void testIntegerCache() {
        Integer a = 127;
        Integer b = 127;
        assertTrue(a == b, "числа 127 должны ссылаться на один объект из кэша");

        Integer c = 128;
        Integer d = 128;
        assertFalse(c == d, "числа 128 должны быть разными объектами в памяти");
        
        assertTrue(c.equals(d), "числа 128 должны быть равны через equals");
    }
}