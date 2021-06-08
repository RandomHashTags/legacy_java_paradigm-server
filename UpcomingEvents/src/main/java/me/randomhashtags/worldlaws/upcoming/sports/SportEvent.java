package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class SportEvent implements UpcomingEvent {
    private final String event, description, venue, location, posterURL;
    private final EventSources sources;

    public SportEvent(String event, String description, String location, String posterURL, String venue, EventSources sources) {
        this.event = event;
        this.description = description;
        this.location = location;
        this.posterURL = posterURL;
        this.venue = venue;
        this.sources = sources;
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
