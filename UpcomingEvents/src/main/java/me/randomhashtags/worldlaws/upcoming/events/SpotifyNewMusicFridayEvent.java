package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class SpotifyNewMusicFridayEvent extends UpcomingEvent {

    private final JSONArrayTranslatable tracks;

    public SpotifyNewMusicFridayEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        tracks = new JSONArrayTranslatable();
        tracks.setTranslatedKeys("artists");
        final JSONObject tracksJSON = properties.getJSONObject("tracks");
        for(String name : tracksJSON.keySet()) {
            final JSONObject trackJSON = tracksJSON.getJSONObject(name);
            final SpotifyTrack track = new SpotifyTrack(trackJSON);
            tracks.put(track);
        }
        insertProperties();
    }
    public SpotifyNewMusicFridayEvent(EventDate date, String title, String description, String imageURL, JSONArrayTranslatable tracks, EventSources sources) {
        super(date, title, description, imageURL, null, null, sources);
        this.tracks = tracks;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPOTIFY_NEW_MUSIC_FRIDAY;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final String tracksKey = UpcomingEventValue.SPOTIFY_NEW_MUSIC_FRIDAY_TRACKS.getKey();
        final JSONObjectTranslatable json = new JSONObjectTranslatable(tracksKey);
        json.put(tracksKey, tracks);
        return json;
    }
}
