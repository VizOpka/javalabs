package javalabs.Lab5;

import java.util.*;

public class Task4 {
    public static void main(String[] args) {
        Queue<String> q = new ArrayDeque<>();
        q.offer("Ann");
        q.offer("Bob");
        System.out.println(q.peek());   
        System.out.println(q.poll());   
        System.out.println(q.poll());   
        System.out.println(q.poll());   
    }
}