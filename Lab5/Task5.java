package javalabs.Lab5;

import java.util.*;

public class Task5 {
    public static void main(String[] args) {

        List<String> list1 = new ArrayList<>(Arrays.asList("A","B","C","D"));
        try {
            for (String s : list1) {
                if (s.equals("B")) list1.remove(s);
            }
            System.out.println(" CME не вылетел ");
        } catch (ConcurrentModificationException e) {
            System.out.println(" CME поймали, список: " + list1);
        }

        List<String> list2 = new ArrayList<>(Arrays.asList("A","B","C","D"));
        Iterator<String> it = list2.iterator();
        while (it.hasNext()) {
            if (it.next().equals("B")) it.remove();
        }
        System.out.println("2) После iterator.remove: " + list2); 

        List<String> list3 = new ArrayList<>(Arrays.asList("A","B","C","D"));
        list3.removeIf(s -> s.equals("B"));
        System.out.println("3) После removeIf: " + list3); 
    }
}