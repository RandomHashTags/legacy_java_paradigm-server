package me.randomhashtags.worldlaws;

public interface CompletionHandler {
    default void handle(Object object) { }
    default void handleClient(@NotNull WLClient client) { }
}
