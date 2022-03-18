package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class APODEvent extends UpcomingEvent {

    private final String copyright, videoURL;

    public APODEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.optJSONObject("properties", new JSONObject());
        copyright = properties.optString(UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_COPYRIGHT.getKey(), null);
        videoURL = properties.optString(UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_VIDEO_URL.getKey(), null);
        insertProperties();
    }
    public APODEvent(EventDate date, String title, String description, String imageURL, String copyright, String videoURL, EventSources sources) {
        super(date, title, description, imageURL, null, null, sources);
        this.copyright = LocalServer.fixEscapeValues(copyright);
        this.videoURL = videoURL;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(copyright != null) {
            final String key = UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_COPYRIGHT.getKey();
            json.put(key, copyright);
            json.addTranslatedKey(key);
        }
        if(videoURL != null) {
            json.put(UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_VIDEO_URL.getKey(), videoURL);
        }
        return json;
    }
}
