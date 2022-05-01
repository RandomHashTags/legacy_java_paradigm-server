package me.randomhashtags.worldlaws.upcoming.entertainment.videogames;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class VideoGameRelease extends UpcomingEvent {
    private final JSONArray genres;

    public VideoGameRelease(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        genres = properties.getJSONArray("genres");
    }
    public VideoGameRelease(EventDate date, String title, String description, String imageURL, JSONArray genres, EventSources sources) {
        super(date, title, description, imageURL, null, null, sources);
        this.genres = genres;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("genres", genres);
        return json;
    }
}
