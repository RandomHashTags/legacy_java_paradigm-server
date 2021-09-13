package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

public final class VideoGameUpdate {
    private final String title, description, imageURL;
    private final EventSources sources;

    public VideoGameUpdate(String title, String description, String imageURL, EventSources sources) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
