package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public class ProWrestlingEvent extends UpcomingEvent {
    private final String mainEvent, notes;

    public ProWrestlingEvent(String title, String description, String imageURL, String location, String mainEvent, String notes, EventSources sources) {
        super(title, description, imageURL, location, null, sources);
        this.mainEvent = LocalServer.fixEscapeValues(mainEvent);
        this.notes = LocalServer.fixEscapeValues(notes);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_PROFESSIONAL_WRESTLING;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (mainEvent != null ? "\"mainEvent\":\"" + mainEvent + "\"" + (notes != null ? "," : "") : "") +
                (notes != null ? "\"notes\":\"" + notes + "\"" : "") +
                "}";
    }
}
