package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class MusicAlbumEvent implements UpcomingEvent {
    private final String artist, album, albumImageURL, description;
    private final EventSources sources;

    public MusicAlbumEvent(String artist, String album, String albumImageURL, String description, EventSources sources) {
        this.artist = LocalServer.fixEscapeValues(artist);
        this.album = LocalServer.fixEscapeValues(album);
        this.albumImageURL = albumImageURL;
        this.description = description;
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
                "\"artist\":\"" + artist + "\"" +
                "}";
    }
}
