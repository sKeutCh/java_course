public enum Denomination {
    thousand(1000),
    five_hundred(500),
    hundred(100),
    fifty(50),
    ten(10),
    one(1);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
