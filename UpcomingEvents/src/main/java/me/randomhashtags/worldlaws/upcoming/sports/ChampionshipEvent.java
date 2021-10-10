package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class ChampionshipEvent extends UpcomingEvent {

    public ChampionshipEvent(String title, String description, String imageURL, String location, EventSources sources) {
        super(title, description, imageURL, location, null, sources);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_CHAMPIONSHIPS;
    }

    @Override
    public String getPropertiesJSONObject() {
        return null;
    }
}
