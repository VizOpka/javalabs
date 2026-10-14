package javalabs.Lab3;

public class Task14 {

    interface CourseFormat { String describe(); }
    static class OnlineFormat implements CourseFormat { public String describe() { return "Online"; } }
    static class ClassroomFormat implements CourseFormat { public String describe() { return "Classroom"; } }
    static class HybridFormat implements CourseFormat { public String describe() { return "Hybrid"; } }

    interface PaymentPolicy { String describe(); }
    static class FreePayment implements PaymentPolicy { public String describe() { return "Free"; } }
    static class FixedPayment implements PaymentPolicy { public String describe() { return "Fixed"; } }
    static class SubscriptionPayment implements PaymentPolicy { public String describe() { return "Subscription"; } }

    static class CourseOffering {
        private final CourseFormat format;
        private final PaymentPolicy payment;
        CourseOffering(CourseFormat f, PaymentPolicy p) { format = f; payment = p; }
        String describe() { return format.describe() + " / " + payment.describe(); }
    }

    public static void main(String[] args) {
        CourseOffering o = new CourseOffering(new HybridFormat(), new SubscriptionPayment());
        System.out.println(o.describe());
    }
}