package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.LocalServer;

public final class PreUpcomingEvent {

    private final UpcomingEventType type;
    private final EventDate date;
    private final String title, tag, imageURL;

    public PreUpcomingEvent(UpcomingEventType type, EventDate date, String title, String tag, String imageURL) {
        this.type = type;
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.tag = LocalServer.fixEscapeValues(tag);
        this.imageURL = LocalServer.fixEscapeValues(imageURL);
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type.name() + "\"," +
                "\"date\":" + (date != null ? date.toString() : "null") + "," +
                "\"title\":\"" + title + "\"," +
                "\"tag\":\"" + tag + "\"," +
                "\"imageURL\":\"" + imageURL + "\"" +
                "}";
    }
}
