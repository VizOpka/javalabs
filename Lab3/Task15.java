package javalabs.Lab3;

public class Task15 {

    static class Student { int age; Student(int a) { age = a; } }
    static class Course { int cap, enrolled; Course(int c, int e) { cap = c; enrolled = e; } }

    sealed interface RuleResult permits Pass, Fail {}
    record Pass() implements RuleResult {}
    record Fail(String reason) implements RuleResult {}

    interface EnrollmentRule { RuleResult check(Student s, Course c); }

    static class AgeRule implements EnrollmentRule {
        private final int min;
        AgeRule(int min) { this.min = min; }
        public RuleResult check(Student s, Course c) {
            return s.age >= min ? new Pass() : new Fail("age < " + min);
        }
    }

    static class CapacityRule implements EnrollmentRule {
        public RuleResult check(Student s, Course c) {
            return c.enrolled < c.cap ? new Pass() : new Fail("full");
        }
    }

    static class AlwaysPassRule implements EnrollmentRule {
        public RuleResult check(Student s, Course c) { return new Pass(); }
    }

    static class AllOfRule implements EnrollmentRule {
        private final EnrollmentRule[] rules;
        AllOfRule(EnrollmentRule... rules) { this.rules = rules; }
        public RuleResult check(Student s, Course c) {
            for (EnrollmentRule r : rules) {
                RuleResult res = r.check(s, c);
                if (res instanceof Fail) return res;
            }
            return new Pass();
        }
    }

    static class AnyOfRule implements EnrollmentRule {
        private final EnrollmentRule[] rules;
        AnyOfRule(EnrollmentRule... rules) { this.rules = rules; }
        public RuleResult check(Student s, Course c) {
            for (EnrollmentRule r : rules)
                if (r.check(s, c) instanceof Pass) return new Pass();
            return new Fail("no alternative passed");
        }
    }

    static class EnrollmentService {
        private final EnrollmentRule rule;
        EnrollmentService(EnrollmentRule r) { rule = r; }
        String enroll(Student s, Course c) {
            RuleResult res = rule.check(s, c);
            return switch (res) {
                case Pass p -> "Accepted";
                case Fail f -> "Rejected: " + f.reason();
            };
        }
    }

    public static void main(String[] args) {
        EnrollmentRule rule = new AllOfRule(
                new AgeRule(18),
                new AnyOfRule(new CapacityRule(), new AlwaysPassRule())
        );
        EnrollmentService service = new EnrollmentService(rule);
        System.out.println(service.enroll(new Student(20), new Course(30, 10)));
    }
}