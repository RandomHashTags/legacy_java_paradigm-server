package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.LocalServer;

public final class WikipediaPicture {
    public String country;
    private final String name, title, imageURL;

    public WikipediaPicture(String name, String title, String imageURL) {
        this.name = LocalServer.fixEscapeValues(name);
        this.title = LocalServer.fixEscapeValues(title);
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "\"" + name + "\":{" +
                (title != null ? "\"title\":\"" + title + "\"," : "") +
                "\"imageURL\":\"" + imageURL + "\"," +
                "}";
    }
}
