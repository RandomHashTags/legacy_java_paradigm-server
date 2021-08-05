package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.LocalServer;

public final class PreRecentEvent {
    private final String title, description, imageURL;

    public PreRecentEvent(String title, String description, String imageURL) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        final boolean hasImageURL = imageURL != null;
        return "\"" + title + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"" + (hasImageURL ? "," : "") : "") +
                (hasImageURL ? "\"imageURL\":\"" + imageURL + "\"" : "") +
                "}";
    }
}
