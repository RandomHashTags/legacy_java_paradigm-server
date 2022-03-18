package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONArray;
import org.json.JSONObject;

public final class VideoGameEvent extends UpcomingEvent {
    private final String platforms, genres;

    public VideoGameEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        platforms = properties.getString(UpcomingEventValue.VIDEO_GAME_PLATFORMS.getKey());
        genres = properties.getString(UpcomingEventValue.VIDEO_GAME_GENRES.getKey());
        insertProperties();
    }
    public VideoGameEvent(EventDate date, String title, String description, String coverArtURL, String platforms, String genres, JSONArray youtubeVideoIDs, EventSources sources) {
        super(date, title, description, coverArtURL, null, youtubeVideoIDs, sources);
        this.platforms = platforms;
        this.genres = genres;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("genres");
        json.put(UpcomingEventValue.VIDEO_GAME_PLATFORMS.getKey(), platforms);
        json.put(UpcomingEventValue.VIDEO_GAME_GENRES.getKey(), genres);
        return json;
    }
}
