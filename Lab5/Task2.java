package javalabs.Lab5;

import java.util.*;

public class Task2 {
    public static void main(String[] args) {
        List<String> codes = Arrays.asList("CS101","MA101","CS101","PH101","MA101");
        Set<String> unique = new LinkedHashSet<>(codes);
        System.out.println(unique); 
    }
}