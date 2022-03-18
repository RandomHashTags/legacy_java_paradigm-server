package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public class ProWrestlingEvent extends UpcomingEvent {
    private final String mainEvent, notes;

    public ProWrestlingEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        mainEvent = properties.getString(UpcomingEventValue.WRESTLING_MAIN_EVENT.getKey());
        notes = properties.getString(UpcomingEventValue.WRESTLING_NOTES.getKey());
        insertProperties();
    }
    public ProWrestlingEvent(EventDate date, String title, String description, String imageURL, String location, String mainEvent, String notes, EventSources sources) {
        super(date, title, description, imageURL, location, null, sources);
        this.mainEvent = LocalServer.fixEscapeValues(mainEvent);
        this.notes = LocalServer.fixEscapeValues(notes);
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_PROFESSIONAL_WRESTLING;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put(UpcomingEventValue.WRESTLING_MAIN_EVENT.getKey(), mainEvent);
        json.put(UpcomingEventValue.WRESTLING_NOTES.getKey(), notes);
        return json;
    }
}
