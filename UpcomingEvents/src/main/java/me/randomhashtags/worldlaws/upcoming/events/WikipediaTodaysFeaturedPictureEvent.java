package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public class WikipediaTodaysFeaturedPictureEvent extends UpcomingEvent {

    private EventSources externalSources;

    public WikipediaTodaysFeaturedPictureEvent(JSONObject json) {
        super(json);
    }
    public WikipediaTodaysFeaturedPictureEvent(EventDate date, String title, String description, String imageURL, EventSources externalSources) {
        super(date, title, description, imageURL, null, null, null);
        this.externalSources = externalSources;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WIKIPEDIA_TODAYS_FEATURED_PICTURE;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(externalSources != null) {
            json.put("externalSources", externalSources.toJSONObject());
        }
        return json;
    }
}
