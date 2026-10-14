package javalabs.Lab3;

import java.util.*;

public class Task9 {

    static class Student {
        int age; Set<String> done;
        Student(int a, Set<String> d) { age = a; done = d; }
    }

    static class Course {
        int cap, enrolled; Set<String> req;
        Course(int c, int e, Set<String> r) { cap = c; enrolled = e; req = r; }
    }

    interface EnrollmentPolicy { boolean allow(Student s, Course c); }

    static class AgePolicy implements EnrollmentPolicy {
        private final int min;
        AgePolicy(int min) { this.min = min; }
        public boolean allow(Student s, Course c) { return s.age >= min; }
    }

    static class PrerequisitesPolicy implements EnrollmentPolicy {
        public boolean allow(Student s, Course c) { return s.done.containsAll(c.req); }
    }

    static class CapacityPolicy implements EnrollmentPolicy {
        public boolean allow(Student s, Course c) { return c.enrolled < c.cap; }
    }

    static class EnrollmentService {
        private final EnrollmentPolicy policy;
        EnrollmentService(EnrollmentPolicy p) { this.policy = p; }
        boolean enroll(Student s, Course c) { return policy.allow(s, c); }
    }

    public static void main(String[] args) {
        Student s = new Student(20, Set.of("Math"));
        Course c = new Course(30, 10, Set.of("Math"));

        EnrollmentService service = new EnrollmentService(new PrerequisitesPolicy());
        System.out.println(service.enroll(s, c));
    }
}