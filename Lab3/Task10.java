package javalabs.Lab3;

public class Task10 {

    interface A { default String greet() { return "A"; } }
    interface B { default String greet() { return "B"; } }


    static class C implements A, B { 
        @Override public String greet() { return A.super.greet() + "+" + B.super.greet(); }
    }

    public static void main(String[] args) {
        System.out.println(new C().greet());   
    }
}