package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class APODEvent extends UpcomingEvent {

    private final String copyright;

    public APODEvent(String title, String description, String imageURL, String copyright, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.copyright = LocalServer.fixEscapeValues(copyright);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"copyright\":\"" + copyright + "\"" +
                "}";
    }
}
