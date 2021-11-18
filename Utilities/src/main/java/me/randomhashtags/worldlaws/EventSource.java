package me.randomhashtags.worldlaws;

import org.json.JSONObject;

public final class EventSource {
    private final String siteName, homepageURL;

    public EventSource(String siteName, String homepageURL) {
        this.siteName = LocalServer.fixEscapeValues(siteName);
        this.homepageURL = homepageURL;
    }
    public EventSource(JSONObject json) {
        siteName = LocalServer.fixEscapeValues(json.getString("siteName"));
        homepageURL = json.getString("homepageURL");
    }

    public String getSiteName() {
        return siteName;
    }
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("homepageURL", homepageURL);
        return json;
    }

    @Override
    public String toString() {
        return "\"" + siteName + "\":{" +
                "\"homepageURL\":\"" + homepageURL + "\"" +
                "}";
    }
}
