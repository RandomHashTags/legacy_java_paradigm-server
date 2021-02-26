package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventDate;

public final class PreUpcomingEvent {
    private final UpcomingEventType type;
    private final String id, title, tag, imageURL;
    private final EventDate date;

    public PreUpcomingEvent(UpcomingEventType type, EventDate date, String title, String tag, String imageURL) {
        this.type = type;
        this.id = title.replace(" ", "").toLowerCase().replace("|", "-");
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.tag = LocalServer.fixEscapeValues(tag);
        this.imageURL = LocalServer.fixEscapeValues(imageURL);
    }

    public UpcomingEventType getType() {
        return type;
    }
    public String getID() {
        return id;
    }
    public EventDate getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }
    public String getTag() {
        return tag;
    }
    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type.name() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"date\":" + (date != null ? date.toString() : "null") + "," +
                "\"title\":\"" + title + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"" : "") +
                "\"tag\":\"" + tag + "\"," +
                "}";
    }
}
