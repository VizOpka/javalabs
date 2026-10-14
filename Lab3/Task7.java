package javalabs.Lab3;

public class Task7 {

    interface NotificationSender { void send(String to, String msg); }

    static class EmailSender implements NotificationSender {
        public void send(String to, String msg) { System.out.println("Email -> " + to + ": " + msg); }
    }

    static class LoggingSender implements NotificationSender {
        private final NotificationSender next;
        LoggingSender(NotificationSender next) { this.next = next; }
        public void send(String to, String msg) {
            System.out.println("[LOG] before " + to);
            next.send(to, msg);
            System.out.println("[LOG] after " + to);
        }
    }

    static class RetrySender implements NotificationSender {
        private final NotificationSender next;
        private final int max;
        RetrySender(NotificationSender next, int max) { this.next = next; this.max = max; }
        public void send(String to, String msg) {
            for (int i = 1; i <= max; i++) {
                try { next.send(to, msg); return; }
                catch (RuntimeException e) {
                    System.out.println("[RETRY] " + i);
                    if (i == max) throw e;
                }
            }
        }
    }

    public static void main(String[] args) {
        NotificationSender a = new LoggingSender(new RetrySender(new EmailSender(), 2));
        a.send("a@b.c", "A");

        System.out.println("---");

        NotificationSender b = new RetrySender(new LoggingSender(new EmailSender()), 2);
        b.send("a@b.c", "B");
    }
}