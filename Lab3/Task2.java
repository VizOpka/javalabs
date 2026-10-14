package javalabs.Lab3;

public class Task2 {

    static class NotificationSender {
        void send(String m) { System.out.println("Sender: " + m); }
        void send(String m, boolean urgent) { System.out.println("Sender urgent=" + urgent + ": " + m); }
    }

    static class EmailSender extends NotificationSender {
        @Override void send(String m) { System.out.println("Email: " + m); }
        void send(String m, int prio) { System.out.println("Email prio=" + prio + ": " + m); }
    }

    static class ConsoleSender extends NotificationSender {
        @Override void send(String m) { System.out.println("Console: " + m); }
    }

    public static void main(String[] args) {
        NotificationSender s1 = new EmailSender();
        s1.send("hi");

        NotificationSender s2 = new ConsoleSender();
        s2.send("hi");

        NotificationSender s3 = new EmailSender();
        s3.send("hi", true);
    }
}