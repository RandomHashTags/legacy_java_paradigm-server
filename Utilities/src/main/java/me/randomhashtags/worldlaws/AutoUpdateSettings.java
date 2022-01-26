package me.randomhashtags.worldlaws;

public final class AutoUpdateSettings {
    public final long interval;
    public final Runnable runnable;

    public AutoUpdateSettings(long interval, Runnable runnable) {
        this.interval = interval;
        this.runnable = runnable;
    }
}
