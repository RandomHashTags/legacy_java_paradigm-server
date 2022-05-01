package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

import java.util.HashSet;

public final class SportEvent extends UpcomingEvent {
    private final String venue;

    public SportEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        venue = properties.getString("venue");
    }
    public SportEvent(EventDate date, String event, String description, String location, String posterURL, String venue, EventSources sources) {
        super(date, event, description, posterURL, location, null, sources);
        this.venue = venue;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public HashSet<String> getTranslatedKeys() {
        return collectKeys("venue");
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("venue", venue);
        return json;
    }
}
