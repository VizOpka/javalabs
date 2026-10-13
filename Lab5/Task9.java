package javalabs.Lab5;

import java.util.*;

public class Task9 {

    static <T> void copy(List<? extends T> src, List<? super T> dst) {
        for (T x : src) dst.add(x);
    }

    public static void main(String[] args) {
        List<Integer> src = Arrays.asList(1, 2, 3);
        List<Number> dst = new ArrayList<>();
        copy(src, dst);
        System.out.println("Скопировали: " + dst);

        List<? extends Number> producer = src;
        Number n = producer.get(0);   

        List<? super Integer> consumer = new ArrayList<Number>();
        consumer.add(5);              
        Object o = consumer.get(0);   
    }
}