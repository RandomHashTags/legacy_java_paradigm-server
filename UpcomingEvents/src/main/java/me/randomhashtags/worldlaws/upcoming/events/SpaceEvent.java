package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class SpaceEvent extends UpcomingEvent {

    public SpaceEvent(String title, String description, String imageURL, String location, EventSources sources) {
        super(title, description, imageURL, location, null, sources);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_EVENT;
    }

    @Override
    public String getPropertiesJSONObject() {
        return null;
    }
}
