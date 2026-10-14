package javalabs.Lab3;

import java.util.*;

public class Task8 {

    static class Course {
        long id; String title;
        Course(long id, String t) { this.id = id; this.title = t; }
    }

    interface ReadableCourseRepository {
        Course findById(long id);
        List<Course> findAll();
    }

    interface WritableCourseRepository extends ReadableCourseRepository {
        void save(Course c);
        void delete(long id);
    }

    static class InMemoryRepo implements WritableCourseRepository {
        private final Map<Long, Course> data = new HashMap<>();
        public Course findById(long id) { return data.get(id); }
        public List<Course> findAll() { return new ArrayList<>(data.values()); }
        public void save(Course c) { data.put(c.id, c); }
        public void delete(long id) { data.remove(id); }
    }

    public static void main(String[] args) {
        WritableCourseRepository w = new InMemoryRepo();
        w.save(new Course(1, "Java"));

        ReadableCourseRepository r = new InMemoryRepo();
        System.out.println(r.findAll());

    }
}