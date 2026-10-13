package javalabs.Lab5;

import java.util.*;

public class Task1 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("A"); list.add("B"); list.add("C");
        list.add(1, "X");       
        list.remove("B");        
        System.out.println(list); 

        List<Integer> al = new ArrayList<>();
        List<Integer> ll = new LinkedList<>();
        for (int i = 0; i < 10000; i++) { al.add(i); ll.add(i); }

        long t = System.nanoTime();
        for (int i = 0; i < 10000; i++) al.get(5000);
        System.out.println("ArrayList: " + (System.nanoTime()-t)/1_000_000 + " ms");

        t = System.nanoTime();
        for (int i = 0; i < 10000; i++) ll.get(5000);
        System.out.println("LinkedList: " + (System.nanoTime()-t)/1_000_000 + " ms");
    }
}