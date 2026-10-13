package javalabs.Lab5;

public class Result<T> {
    private final T value;
    private final String error;

    private Result(T value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        if (value == null) throw new IllegalArgumentException("value == null");
        return new Result<T>(value, null);
    }

    public static <T> Result<T> failure(String error) {
        if (error == null) throw new IllegalArgumentException("error == null");
        return new Result<T>(null, error);
    }

    public boolean isOk() {
        return error == null;
    }

    public T getOrThrow() {
        if (!isOk()) throw new IllegalStateException(error);
        return value;
    }

    public String getError() {
        return error;
    }

    public static void main(String[] args) {
        Result<Integer> ok = Result.success(42);
        Result<String> bad = Result.failure("нет данных");

        System.out.println("ok.isOk() = " + ok.isOk());
        System.out.println("ok.getOrThrow() = " + ok.getOrThrow());

        System.out.println("bad.isOk() = " + bad.isOk());
        System.out.println("bad.getError() = " + bad.getError());

        try {
            bad.getOrThrow();
        } catch (IllegalStateException e) {
            System.out.println("Поймали: " + e.getMessage());
        }

    }
}