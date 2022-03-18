package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class WikipediaPicture {
    public String country;
    private final String name, title, imageURL;

    public WikipediaPicture(String name, String title, String imageURL) {
        this.name = LocalServer.fixEscapeValues(name);
        this.title = LocalServer.fixEscapeValues(title);
        this.imageURL = imageURL;
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
