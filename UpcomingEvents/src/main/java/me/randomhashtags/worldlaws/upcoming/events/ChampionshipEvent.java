package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public final class ChampionshipEvent extends UpcomingEvent {

    public ChampionshipEvent(JSONObject json) {
        super(json);
    }
    public ChampionshipEvent(EventDate date, String title, String description, String imageURL, String location, EventSources sources) {
        super(date, title, description, imageURL, location, null, sources);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_CHAMPIONSHIPS;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        return null;
    }
}
