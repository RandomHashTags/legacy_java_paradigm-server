package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class WordOfTheDayEvent extends UpcomingEvent {

    private final String pronunciationURL, type, syllables;

    public WordOfTheDayEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        pronunciationURL = properties.optString(UpcomingEventValue.WORD_OF_THE_DAY_PRONUNCIATION_URL.getKey(), null);
        type = properties.getString(UpcomingEventValue.WORD_OF_THE_DAY_TYPE.getKey());
        syllables = properties.getString(UpcomingEventValue.WORD_OF_THE_DAY_SYLLABLES.getKey());
        insertProperties();
    }
    public WordOfTheDayEvent(EventDate date, String title, String description, String imageURL, String pronunciationURL, String type, String syllables, EventSources sources) {
        super(date, title, description, imageURL, null, null, sources);
        this.pronunciationURL = pronunciationURL;
        this.type = type;
        this.syllables = syllables;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WORD_OF_THE_DAY;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(pronunciationURL != null) {
            json.put(UpcomingEventValue.WORD_OF_THE_DAY_PRONUNCIATION_URL.getKey(), pronunciationURL);
        }
        json.put(UpcomingEventValue.WORD_OF_THE_DAY_TYPE.getKey(), type);
        json.put(UpcomingEventValue.WORD_OF_THE_DAY_SYLLABLES.getKey(), syllables);
        return json;
    }
}
