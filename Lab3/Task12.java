package javalabs.Lab3;

public class Task12 {

    sealed interface EnrollmentResult permits Accepted, Rejected, WaitListed {}

    record Accepted(long id) implements EnrollmentResult {}
    record Rejected(String reason) implements EnrollmentResult {}
    record WaitListed(int position) implements EnrollmentResult {}

    static String describe(EnrollmentResult r) {
        return switch (r) {
            case Accepted a -> "Accepted " + a.id();
            case Rejected rj -> "Rejected: " + rj.reason();
            case WaitListed w -> "Wait #" + w.position();
        };
    }

    public static void main(String[] args) {
        System.out.println(describe(new Accepted(1)));
        System.out.println(describe(new Rejected("full")));
        System.out.println(describe(new WaitListed(3)));
    }
}