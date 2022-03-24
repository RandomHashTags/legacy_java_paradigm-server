package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class CountryHistoryEra implements Jsoupable {
    private final int startingYear, endingYear;
    private final String title, description, imageURL, url;

    public CountryHistoryEra(String title, int startingYear, int endingYear, String description, String imageURL, String url) {
        this.title = LocalServer.fixEscapeValues(title);
        this.startingYear = startingYear;
        this.endingYear = endingYear;
        this.description = LocalServer.fixEscapeValues(LocalServer.removeWikipediaTranslations(LocalServer.removeWikipediaReferences(description)));
        this.imageURL = imageURL;
        this.url = url;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name", "description");
        json.put("name", title);
        json.put("startingYear", startingYear);
        json.put("endingYear", endingYear);
        json.put("description", description);
        json.put("imageURL", imageURL);
        json.put("url", url);
        return json;
    }
}
