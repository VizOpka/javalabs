package coursehub.Lab6.numbers;

import java.util.IntSummaryStatistics;
import java.util.Arrays;

public class SampleStats {
 
    public static String calculate(int[] samples) {
        IntSummaryStatistics stats = Arrays.stream(samples).summaryStatistics();
        
        return String.format(
            "кол-во: %d | min: %d | max: %d | avg: %.2f",
            stats.getCount(),
            stats.getMin(),
            stats.getMax(),
            stats.getAverage()
        );
    }
}