package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.ServerObject;
import org.json.JSONObject;

public final class CountryAvailability implements ServerObject {
    public String country;
    private final String title, imageURL;
    private final boolean value;

    public CountryAvailability(String title, String imageURL, boolean value) {
        this.title = LocalServer.fixEscapeValues(title);
        this.imageURL = imageURL;
        this.value = value;
    }
    public CountryAvailability(JSONObject json) {
        country = json.getString("country");
        title = null;
        imageURL = null;
        value = true;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"value\":" + value +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"country\":\"" + country + "\"" +
                "}";
    }
}
