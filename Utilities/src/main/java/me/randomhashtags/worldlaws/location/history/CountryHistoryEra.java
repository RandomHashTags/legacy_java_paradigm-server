package me.randomhashtags.worldlaws.location.history;

import me.randomhashtags.worldlaws.LocalServer;

public final class CountryHistoryEra {
    private final int startingYear, endingYear;
    private final String title, description, url;

    public CountryHistoryEra(String title, int startingYear, int endingYear, String description, String url) {
        this.title = LocalServer.fixEscapeValues(title);
        this.startingYear = startingYear;
        this.endingYear = endingYear;
        this.description = LocalServer.fixEscapeValues(description);
        this.url = url;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                "\"startingYear\":" + startingYear + "," +
                "\"endingYear\":" + endingYear + "," +
                "\"description\":\"" + description + "\"," +
                "\"url\":\"" + url + "\"" +
                "}";
    }
}
