package javalabs.Lab3;

public class Task3 {

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

        CourseFormatter[] arr = {
            new CompactCourseFormatter(),
            new DetailedCourseFormatter(),
            x -> "last method: " + x.title
        };

        for (CourseFormatter f : arr) System.out.println(f.format(c));
    }
}