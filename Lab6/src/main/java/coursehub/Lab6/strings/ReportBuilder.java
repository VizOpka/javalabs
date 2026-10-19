package coursehub.Lab6.strings;

public class ReportBuilder {
    public static void run() {
        int iterations = 10000;
        
        long start = System.currentTimeMillis();
        String badResult = "";
        for (int i = 0; i < iterations; i++) {
            badResult += i; 
        }
        long badTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        StringBuilder goodBuilder = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            goodBuilder.append(i);
        }
        String goodResult = goodBuilder.toString();
        long goodTime = System.currentTimeMillis() - start;

        System.out.println("string '+' time: " + badTime + " ms");
        System.out.println("stringBuilder time: " + goodTime + " ms");
        System.out.println("results equal: " + badResult.equals(goodResult));
    }
    public static void main(String[] args) {run();}
}