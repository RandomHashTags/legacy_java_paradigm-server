package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

public final class PreRecentEvent {
    private final EventDate date;
    private final String title, description, imageURL;
    private final EventSources sources;

    public PreRecentEvent(EventDate date, String title, String description, String imageURL, EventSources sources) {
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.sources = sources;
    }

    public EventDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
