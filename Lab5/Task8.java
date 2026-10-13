package javalabs.Lab5;

import java.util.*;

public class Task8 {
    static <T extends Comparable<? super T>> T max(List<? extends T> list) {
        if (list.isEmpty()) throw new NoSuchElementException();
        T best = list.get(0);
        for (T x : list) {
            if (x.compareTo(best) > 0) best = x;
        }
        return best;
    }

    static class Grade implements Comparable<Grade> {
        int value;
        Grade(int value) { this.value = value; }
        @Override public int compareTo(Grade o) { return Integer.compare(value, o.value); }
        @Override public String toString() { return "Grade(" + value + ")"; }
    }

    public static void main(String[] args) {
        System.out.println(max(Arrays.asList(3, 1, 4, 1, 5)));       
        System.out.println(max(Arrays.asList("b", "a", "c")));       
        System.out.println(max(Arrays.asList(new Grade(70), new Grade(95)))); 
    }
}