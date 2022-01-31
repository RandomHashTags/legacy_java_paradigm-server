package me.randomhashtags.worldlaws.service;

public enum SkipInterval {
    NONE(-2),
    ONLY_FIRST(-1);

    private final int value;

    SkipInterval(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
