package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.LocalServer;

public final class PreRecentEvent {
    private final String id, title, description, imageURL;

    public PreRecentEvent(String id, String title, String description, String imageURL) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
