package javalabs.Lab3;

public class Task5 {

    interface Identifiable { long id(); }
    interface CoursePolicy { boolean ok(); }
    interface Formatter<T> { String format(T t); }
    interface ClockProvider { long now(); }

    static abstract class BaseEntity implements Identifiable {
        private final long id;
        protected BaseEntity(long id) { this.id = id; }
        public final long id() { return id; }
    }

    public static void main(String[] args) {
        System.out.println("Identifiable/CoursePolicy/Formatter/ClockProvider -> interface");
        System.out.println("BaseEntity -> abstract class");
    }
}