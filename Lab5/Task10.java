package javalabs.Lab5;

import java.util.*;

public class Task10 {
    static class Course {
        String id;
        String title;   
        Course(String id, String title) { this.id = id; this.title = title; }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Course)) return false;
            Course c = (Course) o;
            return id.equals(c.id);
        }
        @Override public int hashCode() { return id.hashCode(); }
        @Override public String toString() { return id + ":" + title; }
    }

    public static void main(String[] args) {
        Set<Course> set = new HashSet<>();
        Course c = new Course("CS101", "Intro");
        set.add(c);
        System.out.println("До изменения: " + set.contains(c)); 

        c.title = "Advanced";   
        System.out.println("После смены title: " + set.contains(c)); 

        Set<Course> broken = new HashSet<>();
        Course b = new Course("MA101", "Math");
        broken.add(b);
        System.out.println("broken до: " + broken.contains(b)); 
        b.id = "HACKED";   
        System.out.println("broken после смены id: " + broken.contains(b)); 
    }
}