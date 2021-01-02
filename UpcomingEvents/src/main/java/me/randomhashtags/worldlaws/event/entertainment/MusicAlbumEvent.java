package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.UpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEventType;

public final class MusicAlbumEvent implements UpcomingEvent {
    private final EventDate releaseDate;
    private final String artist, album, albumImageURL, description;
    private final EventSources sources;

    public MusicAlbumEvent(EventDate releaseDate, String artist, String album, String albumImageURL, String description, EventSources sources) {
        this.releaseDate = releaseDate;
        this.artist = LocalServer.fixEscapeValues(artist);
        this.album = LocalServer.fixEscapeValues(album);
        this.albumImageURL = albumImageURL;
        this.description = description;
        this.sources = sources;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }
    @Override
    public EventDate getDate() {
        return releaseDate;
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
                "\"artist\":\"" + artist + "\"," +
                "\"albumURL\":\"" + albumImageURL + "\"" +
                "}";
    }
}
