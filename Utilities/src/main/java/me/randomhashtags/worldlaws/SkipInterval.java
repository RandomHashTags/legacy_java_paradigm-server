package me.randomhashtags.worldlaws;

public enum SkipInterval {
    NONE(-2),
    ONLY_FIRST(-1);

    private int value;

    SkipInterval(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
