package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class CountryInfoValue implements Jsoupable {

    public static CountryInfoValue parse(JSONObject json) {
        return new CountryInfoValue(json);
    }

    private final String title, value, description;

    public CountryInfoValue(String title, String value, String description) {
        this.title = LocalServer.fixEscapeValues(title);
        this.value = LocalServer.fixEscapeValues(LocalServer.removeWikipediaReferences(value));
        this.description = LocalServer.fixEscapeValues(LocalServer.removeWikipediaReferences(description));
    }
    private CountryInfoValue(JSONObject json) {
        title = json.getString("title");
        value = json.getString("value");
        description = json.optString("description", null);
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title", "value");
        json.put("title", title);
        if(description != null) {
            json.put("description", description);
            json.addTranslatedKey("description");
        }
        json.put("value", value);
        return json;
    }
}