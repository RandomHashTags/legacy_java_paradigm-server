package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

import java.util.HashMap;

public class WikipediaTodaysFeaturedPictureEvent extends UpcomingEvent {

    private String videoURL;
    private HashMap<String, String> hyperlinks;

    public WikipediaTodaysFeaturedPictureEvent(JSONObject json) {
        super(json);
        final JSONObject propertiesJSON = json.optJSONObject("properties", null);
        if(propertiesJSON != null) {
            videoURL = propertiesJSON.optString("videoURL", null);
            if(propertiesJSON.has("hyperlinks")) {
                hyperlinks = new HashMap<>();
                final JSONObject hyperlinksJSON = propertiesJSON.getJSONObject("hyperlinks");
                for(String key : hyperlinksJSON.keySet()) {
                    hyperlinksJSON.put(key, hyperlinksJSON.getString(key));
                }
            }
        }
    }
    public WikipediaTodaysFeaturedPictureEvent(EventDate date, String title, String description, String imageURL, String videoURL, EventSources sources, HashMap<String, String> hyperlinks) {
        super(date, title, description, imageURL, null, null, sources);
        this.videoURL = videoURL;
        this.hyperlinks = hyperlinks;
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
        if(hyperlinks != null && !hyperlinks.isEmpty()) {
            final JSONObject hyperlinksJSON = new JSONObject(hyperlinks);
            json.put("hyperlinks", hyperlinksJSON);
        }
        return json;
    }
}
