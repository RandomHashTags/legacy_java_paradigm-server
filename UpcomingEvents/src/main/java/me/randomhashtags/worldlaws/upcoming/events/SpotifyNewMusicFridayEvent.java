package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public final class SpotifyNewMusicFridayEvent extends UpcomingEvent {

    private final JSONObjectTranslatable tracks;

    public SpotifyNewMusicFridayEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        tracks = new JSONObjectTranslatable();
        tracks.setTranslatedKeys("artists");
        final JSONObject tracksJSON = properties.getJSONObject("tracks");
        for(String name : tracksJSON.keySet()) {
            final JSONObject trackJSON = tracksJSON.getJSONObject(name);
            final SpotifyTrack track = new SpotifyTrack(trackJSON);
            tracks.put(name, track);
        }
        insertProperties();
    }
    public SpotifyNewMusicFridayEvent(EventDate date, String title, String description, String imageURL, JSONObjectTranslatable tracks, EventSources sources) {
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
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("tracks", tracks);
        return json;
    }
}
