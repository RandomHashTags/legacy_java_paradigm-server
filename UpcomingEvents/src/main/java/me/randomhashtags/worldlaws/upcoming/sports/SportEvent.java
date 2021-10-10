package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class SportEvent extends UpcomingEvent {
    private final String venue;

    public SportEvent(String event, String description, String location, String posterURL, String venue, EventSources sources) {
        super(event, description, posterURL, location, null, sources);
        this.venue = venue;
    }

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"venue\":\"" + venue + "\"" +
                "}";
    }
}
