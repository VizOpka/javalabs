package coursehub.Lab6.numbers;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomSampleService {

    public static int[] generateProductionSample(int size, int bound) {
        RandomGenerator rnd = RandomGeneratorFactory.getDefault().create();
        return generate(rnd, size, bound);
    }

    public static int[] generateDeterministicSample(int size, int bound, long seed) {
        RandomGenerator rnd = RandomGeneratorFactory.getDefault().create(seed);
        return generate(rnd, size, bound);
    }

    private static int[] generate(RandomGenerator rnd, int size, int bound) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = rnd.nextInt(bound);
        }
        return result;
    }

    public static void run() {
        System.out.println(" PRODUCTION RANDOMNESS ");
        int[] prodSample = generateProductionSample(5, 100);
        System.out.println(SampleStats.calculate(prodSample));

        System.out.println("\n DETERMINISTIC TEST RANDOMNESS (seed = 42) ");
        int[] testSample1 = generateDeterministicSample(5, 100, 42L);
        System.out.println(SampleStats.calculate(testSample1));

        System.out.println("\n тот же seed еще раз:");
        int[] testSample2 = generateDeterministicSample(5, 100, 42L);
        System.out.println(SampleStats.calculate(testSample2));
        
        System.out.println("массивы идентичны? " + java.util.Arrays.equals(testSample1, testSample2));
 
    }
    public static void main(String[] args) {run();}
}