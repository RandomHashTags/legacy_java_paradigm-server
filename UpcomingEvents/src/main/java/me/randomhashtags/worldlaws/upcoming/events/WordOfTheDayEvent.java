package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

public final class WordOfTheDayEvent extends UpcomingEvent {

    private final String pronunciationURL, type, syllables;

    public WordOfTheDayEvent(String title, String description, String imageURL, String pronunciationURL, String type, String syllables, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.pronunciationURL = pronunciationURL;
        this.type = type;
        this.syllables = syllables;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WORD_OF_THE_DAY;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (pronunciationURL != null ? "\"" + UpcomingEventValue.WORD_OF_THE_DAY_PRONUNCIATION_URL.getKey() + "\":\"" + pronunciationURL + "\"," : "") +
                "\"" + UpcomingEventValue.WORD_OF_THE_DAY_TYPE.getKey() + "\":\"" + type + "\"," +
                "\"" + UpcomingEventValue.WORD_OF_THE_DAY_SYLLABLES.getKey() + "\":\"" + syllables + "\"" +
                "}";
    }
}
