package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class WikipediaPicture {

    public static WikipediaPicture parse(JSONObject json) {
        return new WikipediaPicture(json);
    }

    private final String name, title, imageURL;

    public WikipediaPicture(String name, String title, String imageURL) {
        this.name = LocalServer.fixEscapeValues(name);
        this.title = LocalServer.fixEscapeValues(title);
        this.imageURL = imageURL;
    }
    private WikipediaPicture(JSONObject json) {
        name = json.getString("name");
        title = json.optString("title", null);
        imageURL = json.getString("imageURL");
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", name);
        if(title != null) {
            json.put("title", title);
            json.addTranslatedKey("title");
        }
        json.put("imageURL", imageURL);
        return json;
    }
}
