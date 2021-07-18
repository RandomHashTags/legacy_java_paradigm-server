package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.location.CountryResource;
import me.randomhashtags.worldlaws.location.CustomCountry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public interface CompletionHandler {
    default void handleObject(Object object) { }
    default void handleString(String string) { }
    default void handleStringValue(String key, String value) { }
    default void handleCountryResources(HashSet<CountryResource> resources) { }
    default void handleBoolean(boolean success) { }
    default void handleJSONArray(JSONArray array) { }
    default void handleJSONObject(JSONObject json) { }
    default void handleClient(@NotNull WLClient client) { }
    default void handleCustomCountry(CustomCountry country) { }

    default void load(CompletionHandler handler) { handler.handleString(null); }
}
