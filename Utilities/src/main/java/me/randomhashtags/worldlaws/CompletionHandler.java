package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public interface CompletionHandler {
    default void handleFail() { }
    default void handleObject(Object object) { }
    default void handleString(String string) { }
    default void handleStringValue(String key, String value) { }
    default void handleHashSetString(HashSet<String> hashset) { }
    default void handleBoolean(boolean success) { }
    default void handleJSONArray(JSONArray array) { }
    default void handleJSONObject(JSONObject json) { }
    default void handleClient(@NotNull WLClient client) { }
    default void handleServiceResponse(SovereignStateInformationType type, String string) { }

    default void load(CompletionHandler handler) { handler.handleString(null); }
}
