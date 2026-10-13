package javalabs.Lab5;

import java.util.*;

public class Task3 {
    public static void main(String[] args) {
        Map<String,String> index = new HashMap<>();
        String[][] data = {{"CS101","Intro"},{"MA101","Math"},{"CS101","Dup"}};

        for (String[] row : data) {
            if (index.containsKey(row[0])) {
                System.out.println("Дубликат ID: " + row[0]);
            } else {
                index.put(row[0], row[1]);
            }
        }
        System.out.println(index);
        System.out.println(index.get("CS101")); }
}