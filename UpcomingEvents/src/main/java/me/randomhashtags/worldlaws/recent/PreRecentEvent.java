package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.LocalServer;

public final class PreRecentEvent {
    private final String id, title, imageURL;

    public PreRecentEvent(String id, String title, String imageURL) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
