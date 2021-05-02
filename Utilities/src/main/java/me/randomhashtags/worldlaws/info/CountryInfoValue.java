package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public class CountryInfoValue implements Jsoupable {
    private final String title, value, description;

    public CountryInfoValue(String title, String value, String description) {
        this.title = LocalServer.fixEscapeValues(title);
        this.value = LocalServer.fixEscapeValues(removeReferences(value));
        this.description = LocalServer.fixEscapeValues(description);
    }
    public CountryInfoValue(JSONObject json) {
        title = LocalServer.fixEscapeValues(json.getString("title"));
        value = LocalServer.fixEscapeValues(json.getString("value"));
        description = LocalServer.fixEscapeValues(json.has("description") ? json.getString("description") : null);
    }

    @Override
    public String toString() {
        return "{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                "\"title\":\"" + title + "\"," +
                "\"value\":\"" + value + "\"" +
                "}";
    }
}