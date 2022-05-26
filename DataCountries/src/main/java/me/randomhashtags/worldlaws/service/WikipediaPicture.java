package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class WikipediaPicture {

    public static WikipediaPicture parse(JSONObject json) {
        return new WikipediaPicture(json);
    }

    private final String name, title, imageURL;

    public WikipediaPicture(String name, String title, String imageURL) {
        this.name = name;
        this.title = title;
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
            json.put("title", title, true);
        }
        json.put("imageURL", imageURL);
        return json;
    }
}
