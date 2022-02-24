package me.randomhashtags.worldlaws;

import org.json.JSONObject;

public final class EventSource {
    private final String siteName, homepageURL;

    public EventSource(String siteName, String homepageURL) {
        this.siteName = parseSiteName(siteName);
        this.homepageURL = homepageURL;
    }

    private String parseSiteName(String input) {
        return LocalServer.fixEscapeValues(input)
                .replace("%2B", "+")
                .replace("%27", "'")
                .replace("%C3%AD", "í")
                .replace("%CA%BB", "ʻ")
                .replace("%E2%80%93", "–")
                .replace("%E2%80%98", "ʻ")
                ;
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
