package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.LocalServer;

public final class PreTopClassActionSettlement {
    private final String title, description, potentialSettlement, imageURL, url;

    public PreTopClassActionSettlement(String title, String description, String potentialSettlement, String imageURL, String url) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.potentialSettlement = LocalServer.fixEscapeValues(potentialSettlement);
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                "\"description\":\"" + description + "\"," +
                "\"potentialSettlement\":\"" + potentialSettlement + "\"," +
                "\"imageURL\":\"" + imageURL + "\"" +
                "}";
    }
}
