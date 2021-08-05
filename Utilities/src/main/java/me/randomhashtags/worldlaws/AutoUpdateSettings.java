package me.randomhashtags.worldlaws;

public final class AutoUpdateSettings {
    public final long interval;
    public final CompletionHandler handler;

    public AutoUpdateSettings(long interval, CompletionHandler handler) {
        this.interval = interval;
        this.handler = handler;
    }
}
