package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public final class MusicAlbumEvent extends UpcomingEvent {
    private final String artist;
    private final JSONObject spotifyDetails, itunesDetails;

    public MusicAlbumEvent(String artist, String album, String albumImageURL, String description, JSONObject spotifyDetails, JSONObject itunesDetails, EventSources sources) {
        super(album, description, albumImageURL, null, null, sources);
        this.artist = LocalServer.fixEscapeValues(artist);
        this.spotifyDetails = spotifyDetails;
        this.itunesDetails = itunesDetails;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (spotifyDetails != null ? "\"spotifyDetails\":" + spotifyDetails.toString() + "," : "") +
                (itunesDetails != null ? "\"itunesDetails\":" + itunesDetails.toString() + "," : "") +
                "\"artist\":\"" + artist + "\"" +
                "}";
    }
}
