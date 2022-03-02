package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public final class SpotifyNewMusicFridayEvent extends UpcomingEvent {

    private final JSONObject tracks;

    public SpotifyNewMusicFridayEvent(String title, String description, String imageURL, JSONObject tracks, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.tracks = tracks;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPOTIFY_NEW_MUSIC_FRIDAY;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"tracks\":" + tracks.toString() +
                "}";
    }
}
