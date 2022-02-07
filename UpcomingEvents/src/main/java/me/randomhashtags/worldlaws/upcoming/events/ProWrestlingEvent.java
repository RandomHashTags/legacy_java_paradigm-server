package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

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
                (mainEvent != null ? "\"" + UpcomingEventValue.WRESTLING_MAIN_EVENT.getKey() +  "\":\"" + mainEvent + "\"" + (notes != null ? "," : "") : "") +
                (notes != null ? "\"" + UpcomingEventValue.WRESTLING_NOTES.getKey() + "\":\"" + notes + "\"" : "") +
                "}";
    }
}
