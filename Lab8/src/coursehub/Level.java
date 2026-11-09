package coursehub;

public enum Level {
    BEGINNER(1), INTERMEDIATE(2), ADVANCED(3);

    private final int rank;

    Level(int rank) { this.rank = rank; }

    public int rank() { return rank; }
}
