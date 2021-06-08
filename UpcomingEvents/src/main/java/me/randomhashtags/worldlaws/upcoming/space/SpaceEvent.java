package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class SpaceEvent implements UpcomingEvent {
    private final String title, description, location;
    private final EventSources source;

    public SpaceEvent(String title, String description, String location, EventSources source) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.source = source;
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
