package javalabs.Lab5;

import java.util.*;

public class Task13 {
    static Map<String, Set<String>> index = new HashMap<>();

    static void addCourse(String courseId, String... tags) {
        for (String tag : tags) {
            Set<String> set = index.get(tag);
            if (set == null) {
                set = new HashSet<>();
                index.put(tag, set);
            }
            set.add(courseId);
        }
    }

    static void removeCourse(String courseId, String tag) {
        Set<String> set = index.get(tag);
        if (set == null) return;
        set.remove(courseId);
        if (set.isEmpty()) index.remove(tag);  
    }

    static Set<String> intersect(String... tags) {
        if (tags.length == 0) return Collections.emptySet();
        Set<String> result = new HashSet<>(
            index.containsKey(tags[0]) ? index.get(tags[0]) : Collections.<String>emptySet()
        );
        for (int i = 1; i < tags.length && !result.isEmpty(); i++) {
            Set<String> s = index.get(tags[i]);
            if (s == null) return Collections.emptySet();
            result.retainAll(s);
        }
        return result;
    }

    public static void main(String[] args) {
        addCourse("CS101", "java", "oop");
        addCourse("CS102", "java", "algo");
        System.out.println("java ∩ oop: " + intersect("java", "oop")); 
        System.out.println("java ∩ algo: " + intersect("java", "algo")); 

        removeCourse("CS101", "oop");
        System.out.println("Есть тег oop? " + index.containsKey("oop")); 
    }
}