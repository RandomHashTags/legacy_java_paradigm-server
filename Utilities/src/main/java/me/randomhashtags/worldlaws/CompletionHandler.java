package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public interface CompletionHandler {
    default void handleObject(Object object) { }
    default void handleString(String string) { }
    default void handleStringValue(String key, String value) { }
    default void handleConcurrentHashMapHashSetString(ConcurrentHashMap<String, HashSet<String>> hashmap) {  }
    default void handleJSONValue(String key, JSONObject json) { }
    default void handleClient(@NotNull WLClient client) { }

    default String loadJSONObjectString() { return null; }
    default JSONObject loadJSONObject() { return null; }
    default String loadJSONArrayString() { return null; }
    default JSONArray loadJSONArray() { return null; }
    default void handleCompletionHandler(CompletionHandler handler) { handler.handleObject(null); }
}
