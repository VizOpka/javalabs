package javalabs.Lab3;

public class Task4 {

    static abstract class AbstractNotificationSender {
        public final void send(String to, String msg) {
            if (to == null || to.isBlank()) throw new IllegalArgumentException("error");
            doSend(to, msg);
        }
        protected abstract void doSend(String to, String msg);
    }

    static class EmailSender extends AbstractNotificationSender {
        protected void doSend(String to, String msg) { System.out.println("Email to " + to + ": " + msg); }
    }

    static class ConsoleSender extends AbstractNotificationSender {
        protected void doSend(String to, String msg) { System.out.println("Console " + to + ": " + msg); }
    }

    public static void main(String[] args) {
        AbstractNotificationSender s = new EmailSender();
        s.send("a@b.c", "hi");

        s = new ConsoleSender();
        s.send("out", "hi");

        try { s.send("", "hi"); }
        catch (Exception e) { System.out.println(e.getMessage()); }
    }
}