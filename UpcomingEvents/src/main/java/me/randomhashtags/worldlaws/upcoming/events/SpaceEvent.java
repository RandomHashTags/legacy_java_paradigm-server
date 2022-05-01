package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class SpaceEvent extends UpcomingEvent {

    private final String newsURL, videoURL;

    public SpaceEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.optJSONObject("properties", new JSONObject());
        newsURL = properties.optString(UpcomingEventValue.SPACE_EVENT_NEWS_URL.getKey(), null);
        videoURL = properties.optString(UpcomingEventValue.SPACE_EVENT_VIDEO_URL.getKey(), null);
    }
    public SpaceEvent(EventDate date, String title, String description, String imageURL, String location, String newsURL, String videoURL, EventSources sources) {
        super(date, title, description, imageURL, location, null, sources);
        this.newsURL = newsURL;
        this.videoURL = videoURL;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_EVENT;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(newsURL != null) {
            json.put(UpcomingEventValue.SPACE_EVENT_NEWS_URL.getKey(), newsURL);
        }
        if(videoURL != null) {
            json.put(UpcomingEventValue.SPACE_EVENT_VIDEO_URL.getKey(), videoURL);
        }
        return json;
    }
}
