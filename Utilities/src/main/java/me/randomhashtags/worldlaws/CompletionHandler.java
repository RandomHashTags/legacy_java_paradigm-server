package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

public interface CompletionHandler {
    default void handleString(String string) { }

    default String loadJSONObjectString() { return null; }
    default JSONObject loadJSONObject() { return null; }
    default String loadJSONArrayString() { return null; }
    default JSONArray loadJSONArray() { return null; }
}
