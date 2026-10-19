package coursehub.Lab6.strings;

public class StringIdentityDemo {
    public static void run() {

        String s1 = "Java";
        String s2 = "Java";
        System.out.println("s1 == s2: " + (s1 == s2)); 

        String s3 = new String("Java");
        System.out.println("s1 == s3: " + (s1 == s3)); 
        System.out.println("s1.equals(s3): " + s1.equals(s3)); 
  
        String s4 = "Ja" + "va"; 
        System.out.println("s1 == s4: " + (s1 == s4)); 

        String part1 = "Ja";
        String s5 = part1 + "va"; 
        System.out.println("s1 == s5: " + (s1 == s5)); 

        String s6 = s5.intern();
        System.out.println("s1 == s6: " + (s1 == s6)); 
        
    }

   public static void main(String[] args) {run();}

}