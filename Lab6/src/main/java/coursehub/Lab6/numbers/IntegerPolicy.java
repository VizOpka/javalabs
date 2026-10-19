package coursehub.Lab6.numbers;

public class IntegerPolicy {
    public static void run() {
        Integer a = 127;
        Integer b = 127;
        System.out.println("127 == 127: " + (a == b)); 

        Integer c = 128;
        Integer d = 128;
        System.out.println("128 == 128: " + (c == d)); 
        System.out.println("128 equals 128: " + c.equals(d)); 

        Integer nullValue = null;
        try {
            int unboxed = nullValue; 
            System.out.println(unboxed);
        } catch (NullPointerException e) {
            System.out.println("поймали NPE");
        }
    }
    public static void main(String[] args) {run();}
}