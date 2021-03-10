package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.people.Politician;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface CompletionHandler {
    default void handle(Object object) { }
    default void handleJSONArray(JSONArray array) { }
    default void handleJSONObject(JSONObject object) { }
    default void handleClient(@NotNull WLClient client) { }
    default void handlePolitician(Politician politician) { }
    default void handlePoliticianCosponsors(List<Politician> cosponsors) { }

    default void load(CompletionHandler handler) { handler.handle(null); }
}
