package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;

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

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                "\"startingYear\":" + startingYear + "," +
                "\"endingYear\":" + endingYear + "," +
                "\"description\":\"" + description + "\"," +
                "\"imageURL\":\"" + imageURL + "\"," +
                "\"url\":\"" + url + "\"" +
                "}";
    }
}
