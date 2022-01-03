package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class WordOfTheDayEvent extends UpcomingEvent {

    public WordOfTheDayEvent(String title, String description, String imageURL, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WORD_OF_THE_DAY;
    }

    @Override
    public String getPropertiesJSONObject() {
        return null;
    }
}
