package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class WikipediaPicture {
    public String country;
    private final String name, title, imageURL, url;

    public WikipediaPicture(String name, String title, String imageURL, String url) {
        this.name = LocalServer.fixEscapeValues(name);
        this.title = LocalServer.fixEscapeValues(title);
        this.imageURL = imageURL;
        this.url = url;
    }
    public WikipediaPicture(JSONObject json) {
        this.name = LocalServer.fixEscapeValues(json.getString("id"));
        this.title = json.has("title") ? LocalServer.fixEscapeValues(json.getString("title")) : null;
        this.imageURL = json.getString("imageURL");
        this.url = json.getString("url");
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + name + "\"," +
                (title != null ? "\"title\":\"" + title + "\"," : "") +
                "\"imageURL\":\"" + imageURL + "\"," +
                "\"url\":\"" + url + "\"" +
                "}";
    }
}
