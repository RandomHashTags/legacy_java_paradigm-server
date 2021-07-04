package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import org.json.JSONObject;

public final class MusicAlbumEvent implements UpcomingEvent {
    private final String artist, album, albumImageURL, description;
    private final JSONObject spotifyDetails;
    private final EventSources sources;

    public MusicAlbumEvent(String artist, String album, String albumImageURL, String description, JSONObject spotifyDetails, EventSources sources) {
        this.artist = LocalServer.fixEscapeValues(artist);
        this.album = LocalServer.fixEscapeValues(album);
        this.albumImageURL = albumImageURL;
        this.description = description;
        this.spotifyDetails = spotifyDetails;
        this.sources = sources;
    }

    @Override
    public String getTitle() {
        return album;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public String getImageURL() {
        return albumImageURL;
    }
    @Override
    public String getLocation() {
        return null;
    }
    @Override
    public EventSources getSources() {
        return sources;
    }
    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (spotifyDetails != null ? "\"spotifyDetails\":" + spotifyDetails.toString() + "," : "") +
                "\"artist\":\"" + artist + "\"" +
                "}";
    }
}
