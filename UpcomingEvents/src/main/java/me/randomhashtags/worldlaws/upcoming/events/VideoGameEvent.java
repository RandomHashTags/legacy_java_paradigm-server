package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONArray;

public final class VideoGameEvent extends UpcomingEvent {
    private final String platforms, genres;

    public VideoGameEvent(String title, String description, String coverArtURL, String platforms, String genres, JSONArray youtubeVideoIDs, EventSources sources) {
        super(title, description, coverArtURL, null, youtubeVideoIDs, sources);
        this.platforms = platforms;
        this.genres = genres;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"" + UpcomingEventValue.VIDEO_GAME_PLATFORMS.getKey() + "\":\"" + platforms + "\"," +
                "\"" + UpcomingEventValue.VIDEO_GAME_GENRES.getKey() + "\":\"" + genres + "\"" +
                "}";
    }
}
