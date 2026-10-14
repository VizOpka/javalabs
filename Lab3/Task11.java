package javalabs.Lab3;

public class Task11 {

    static class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        public boolean equals(Object o) {
            if (!(o instanceof Point p)) return false;
            return p.x == x && p.y == y;
        }
        public int hashCode() { return 31 * x + y; }
    }

    static class ColoredPoint extends Point {
        final String color;
        ColoredPoint(int x, int y, String c) { super(x, y); this.color = c; }
        public boolean equals(Object o) {
            if (!(o instanceof ColoredPoint cp)) return false;
            return super.equals(cp) && cp.color.equals(color);
        }
    }

    public static void main(String[] args) {
        Point p = new Point(1, 2);
        ColoredPoint cp = new ColoredPoint(1, 2, "red");

        System.out.println(p.equals(cp));   
        System.out.println(cp.equals(p));   
    }
}