package coursehub.Lab6.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseCode {

    private static final Pattern CODE_PATTERN = Pattern.compile("^(\\p{Lu}+)-(\\d{3})$");

    public static void run() {
        String[] tests = {"JAVA-101", "PYTHON-202", "java-101", "JAVA-101 ", "JAVA-10", "JAVA-101B"};
        
        for (String test : tests) {
            Matcher matcher = CODE_PATTERN.matcher(test);
            if (matcher.matches()) { 
                System.out.println("valid: " + test + "  prefix: " + matcher.group(1) + ", number: " + matcher.group(2));
            } else {
                System.out.println("invalid: [" + test + "]");
            }
        }
    }
    public static void main(String[] args) {run();}
}