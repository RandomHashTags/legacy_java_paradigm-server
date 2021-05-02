package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

public interface CompletionHandler {
    default void handle(Object object) { }
    default void handleJSONArray(JSONArray array) { }
    default void handleJSONObject(JSONObject json) { }
    default void handleClient(@NotNull WLClient client) { }

    default void load(CompletionHandler handler) { handler.handle(null); }
}
