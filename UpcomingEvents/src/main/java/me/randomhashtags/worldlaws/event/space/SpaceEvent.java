package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.event.UpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEventType;

public final class SpaceEvent implements UpcomingEvent {
    private final UpcomingEventType type;
    private final EventDate date;
    private final String title, description, location;
    private final EventSources source;

    public SpaceEvent(UpcomingEventType type, EventDate date, String title, String description, String location, EventSources source) {
        this.type = type;
        this.date = date;
        this.title = title;
        this.description = description;
        this.location = location;
        this.source = source;
    }

    @Override
    public UpcomingEventType getType() {
        return type;
    }
    @Override
    public EventDate getDate() {
        return date;
    }
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public String getImageURL() {
        return null;
    }
    @Override
    public String getLocation() {
        return location;
    }
    @Override
    public EventSources getSources() {
        return source;
    }
    @Override
    public String getPropertiesJSONObject() {
        return null;
    }
}
