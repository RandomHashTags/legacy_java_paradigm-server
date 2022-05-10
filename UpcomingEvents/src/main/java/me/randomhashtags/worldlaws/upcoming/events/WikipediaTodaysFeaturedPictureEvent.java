package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public class WikipediaTodaysFeaturedPictureEvent extends UpcomingEvent {

    private String videoURL;
    private EventSources externalSources;

    public WikipediaTodaysFeaturedPictureEvent(JSONObject json) {
        super(json);
        final JSONObject propertiesJSON = json.optJSONObject("properties", null);
        if(propertiesJSON != null) {
            videoURL = propertiesJSON.optString("videoURL", null);
            if(propertiesJSON.has("externalSources")) {
                externalSources = new EventSources(propertiesJSON.getJSONObject("externalSources"));
            }
        }
    }
    public WikipediaTodaysFeaturedPictureEvent(EventDate date, String title, String description, String imageURL, String videoURL, EventSources sources, EventSources externalSources) {
        super(date, title, description, imageURL, null, null, sources);
        this.videoURL = videoURL;
        this.externalSources = externalSources;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WIKIPEDIA_TODAYS_FEATURED_PICTURE;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(videoURL != null) {
            json.put("videoURL", videoURL);
        }
        if(externalSources != null && !externalSources.isEmpty()) {
            json.put("externalSources", externalSources.toJSONObject());
        }
        return json;
    }
}
