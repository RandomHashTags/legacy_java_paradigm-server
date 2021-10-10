package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public final class MusicAlbumEvent extends UpcomingEvent {
    private final String artist;
    private final JSONObject spotifyDetails;

    public MusicAlbumEvent(String artist, String album, String albumImageURL, String description, JSONObject spotifyDetails, EventSources sources) {
        super(album, description, albumImageURL, null, null, sources);
        this.artist = LocalServer.fixEscapeValues(artist);
        this.spotifyDetails = spotifyDetails;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (spotifyDetails != null ? "\"spotifyDetails\":" + spotifyDetails.toString() + "," : "") +
                "\"artist\":\"" + artist + "\"" +
                "}";
    }
}
