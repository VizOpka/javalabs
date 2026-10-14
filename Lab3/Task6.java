package javalabs.Lab3;

public class Task6 {

    static class Parent {
        Number value() { return 1; }
        void process(String s) { System.out.println("Parent: " + s); }
        void handle(Number n) { System.out.println("Parent.handle(Number)"); }
    }

    static class Child extends Parent {
        @Override Integer value() { return 2; }
        @Override void process(String s) { System.out.println("Child: " + s); }
        void handle(Integer n) { System.out.println("Child.handle(Integer)"); }
    }

    public static void main(String[] args) {
        Parent p = new Child();
        System.out.println(p.value());  
        p.process("x");                  
        p.handle(10);                    
    }
}