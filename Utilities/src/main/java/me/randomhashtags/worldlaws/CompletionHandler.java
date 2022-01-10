package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.stories.Story;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public interface CompletionHandler {
    default void handleFail() { }
    default void handleObject(Object object) { }
    default void handleString(String string) { }
    default void handleStringValue(String key, String value) { }
    default void handleConcurrentHashMapHashSetString(ConcurrentHashMap<String, HashSet<String>> hashmap) {  }
    default void handleBoolean(boolean success) { }
    default void handleJSONArray(JSONArray array) { }
    default void handleJSONObject(JSONObject json) { }
    default void handleJSONValue(String key, JSONObject json) { }
    default void handleClient(@NotNull WLClient client) { }
    default void handleServiceResponse(CountryService service, String string) { }
    default void handleStories(HashSet<Story> stories) { }

    default void load(CompletionHandler handler) { handler.handleString(null); }
    default void handleCompletionHandler(CompletionHandler handler) { handler.handleObject(null); }
}
