package me.randomhashtags.worldlaws.event.sports;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.event.UpcomingEvent;

public final class SportEvent implements UpcomingEvent {
    private final EventDate date;
    private final String event, description, venue, location, posterURL;
    private final EventSources sources;

    public SportEvent(String event, String description, String location, String posterURL, String venue, EventSources sources) {
        this.event = event;
        this.date = null;
        this.description = description;
        this.location = location;
        this.posterURL = posterURL;
        this.venue = venue;
        this.sources = sources;
    }

    @Override
    public EventDate getDate() {
        return date;
    }
    @Override
    public String getTitle() {
        return event;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public String getImageURL() {
        return posterURL;
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
        return "{" +
                "\"venue\":\"" + venue + "\"" +
                "}";
    }
}
