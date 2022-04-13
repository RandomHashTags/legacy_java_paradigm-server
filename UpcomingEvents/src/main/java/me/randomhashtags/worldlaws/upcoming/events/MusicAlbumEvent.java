package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class MusicAlbumEvent extends UpcomingEvent {
    private final String artist;
    private final JSONObject spotifyDetails, itunesDetails;

    public MusicAlbumEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        artist = properties.getString(UpcomingEventValue.MUSIC_ALBUM_ARTIST.getKey());
        spotifyDetails = properties.getJSONObject(UpcomingEventValue.MUSIC_ALBUM_DETAILS_SPOTIFY.getKey());
        itunesDetails = properties.getJSONObject(UpcomingEventValue.MUSIC_ALBUM_DETAILS_ITUNES.getKey());
        insertProperties();
    }
    public MusicAlbumEvent(EventDate date, String artist, String album, String albumImageURL, String description, JSONObject spotifyDetails, JSONObject itunesDetails, EventSources sources) {
        super(date, album, description, albumImageURL, null, null, sources);
        this.artist = artist;
        this.spotifyDetails = spotifyDetails;
        this.itunesDetails = itunesDetails;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(spotifyDetails != null) {
            json.put("spotifyDetails", spotifyDetails);
        }
        if(itunesDetails != null) {
            json.put("itunesDetails", itunesDetails);
        }
        json.put(UpcomingEventValue.MUSIC_ALBUM_ARTIST.getKey(), artist);
        return json;
    }
}
