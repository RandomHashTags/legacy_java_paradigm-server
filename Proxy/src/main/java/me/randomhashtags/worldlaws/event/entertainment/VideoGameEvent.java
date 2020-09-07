package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.EventSources;
import me.randomhashtags.worldlaws.event.UpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEventType;

public final class VideoGameEvent implements UpcomingEvent {

    private EventDate releaseDate;
    private String title, description, location;
    private EventSources sources;

    public VideoGameEvent(EventDate releaseDate, String title, String description, EventSources sources) {
        this.releaseDate = releaseDate;
        this.title = title;
        this.description = description;
        this.sources = sources;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public EventDate getDate() {
        return releaseDate;
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
    public String getLocation() {
        return location;
    }

    @Override
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{}";
    }
}
