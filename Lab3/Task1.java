package javalabs.Lab3;

public class Task1 {

    static class Course {
        long id; String title;
        Course(long id, String title) { this.id = id; this.title = title; }
    }

    interface CourseFormatter { String format(Course c); }

    static class CompactCourseFormatter implements CourseFormatter {
        public String format(Course c) { return c.title + "(" + c.id + ")"; }
    }

    static class DetailedCourseFormatter implements CourseFormatter {
        public String format(Course c) { return "Course " + c.id + ": " + c.title; }
    }

    public static void main(String[] args) {
        Course c = new Course(1, "Java");
        CourseFormatter f = new CompactCourseFormatter();
        System.out.println(f.format(c));
        f = new DetailedCourseFormatter();
        System.out.println(f.format(c));
    }
}