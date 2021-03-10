package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.ServerObject;
import org.json.JSONObject;

public final class CountryAvailability implements ServerObject {
    public String country;
    private final String title, category;
    private final boolean value;

    public CountryAvailability(String title, boolean value, CountryAvailabilityCategory category) {
        this.title = LocalServer.fixEscapeValues(title);
        this.value = value;
        this.category = category.name();
    }
    public CountryAvailability(JSONObject json) {
        country = json.getString("country");
        title = null;
        value = true;
        category = null;
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                "\"value\":" + value + "," +
                "\"category\":\"" + category + "\"" +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"country\":\"" + country + "\"" +
                "}";
    }
}
