package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class CountryInfoValue implements Jsoupable {
    private final String title, value, description;

    public CountryInfoValue(String title, String value, String description) {
        this.title = LocalServer.fixEscapeValues(title);
        this.value = LocalServer.fixEscapeValues(removeReferences(value));
        this.description = LocalServer.fixEscapeValues(description);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                "\"value\":\"" + value + "\"" +
                "}";
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        if(description != null) {
            json.put("description", description);
        }
        json.put("value", value);
        return json;
    }
}