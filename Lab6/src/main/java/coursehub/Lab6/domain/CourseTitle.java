package coursehub.Lab6.domain;

import java.util.Locale;

public class CourseTitle {
    private final String displayValue;
    private final String searchKey;

    public CourseTitle(String rawTitle) {

        this.displayValue = rawTitle.strip().replaceAll("\\s+", " ");
        
        this.searchKey = this.displayValue.toLowerCase(Locale.ROOT);
    }

    public static void run() {
        CourseTitle t1 = new CourseTitle("  Java   Basics  ");
        CourseTitle t2 = new CourseTitle("JAVA BASICS");
        
        System.out.println("строка 1 display: [" + t1.displayValue + "], search: [" + t1.searchKey + "]");
        System.out.println("строка 2 display: [" + t2.displayValue + "], search: [" + t2.searchKey + "]");
        
        System.out.println("поисковые ключи совпадают? " + t1.searchKey.equals(t2.searchKey));
    }

    public static void main(String[] args) { run(); }
}