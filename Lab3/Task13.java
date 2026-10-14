package javalabs.Lab3;

public class Task13 {

    interface Notification { void send(String m); }

    static class EmailNotification implements Notification {
        public void send(String m) { System.out.println("Email: " + m); }
    }
    static class SmsNotification implements Notification {
        public void send(String m) { System.out.println("SMS: " + m); }
    }
    static class PushNotification implements Notification {
        public void send(String m) { System.out.println("Push: " + m); }
    }

    public static void main(String[] args) {
        Notification n = new SmsNotification();
        n.send("hi");
    }
}