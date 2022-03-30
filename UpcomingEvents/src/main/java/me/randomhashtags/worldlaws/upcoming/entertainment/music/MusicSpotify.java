package me.randomhashtags.worldlaws.upcoming.entertainment.music;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SpotifyNewMusicFridayEvent;
import me.randomhashtags.worldlaws.upcoming.events.SpotifyTrack;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class MusicSpotify extends LoadedUpcomingEventController implements SpotifyService {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPOTIFY_NEW_MUSIC_FRIDAY;
    }

    @Override
    public void load() {
        final LocalDate now = LocalDate.now();
        if(now.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            refreshNewMusicFriday(now);
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new SpotifyNewMusicFridayEvent(json);
    }

    private void refreshNewMusicFriday(LocalDate date) {
        final JSONObject responseJSON = getSpotifyPlaylistJSON("37i9dQZF1DX4JAvHpjipBk");
        if(responseJSON != null) {
            final UpcomingEventType type = getType();
            final EventDate eventDate = new EventDate(date);
            final String dateString = eventDate.getDateString();
            final String imageURL = responseJSON.getJSONArray("images").getJSONObject(0).getString("url");
            final String title = "Spotify: New Music Friday", description = LocalServer.fixEscapeValues(responseJSON.getString("description"));

            final JSONArray tracksArray = responseJSON.getJSONObject("tracks").getJSONArray("items");
            final JSONObjectTranslatable tracks = new JSONObjectTranslatable();
            for(Object obj : tracksArray) {
                final JSONObject trackJSON = ((JSONObject) obj).getJSONObject("track");
                if(trackJSON.has("album")) {
                    final JSONObject albumJSON = trackJSON.getJSONObject("album");
                    final JSONArray albumImages = albumJSON.getJSONArray("images");
                    String trackImageURL = null;
                    int maxWidth = 0;
                    for(Object imageObj : albumImages) {
                        final JSONObject imageJSON = (JSONObject) imageObj;
                        if(imageJSON.getInt("width") > maxWidth) {
                            trackImageURL = imageJSON.getString("url");
                        }
                    }

                    final JSONArray artistsArray = trackJSON.getJSONArray("artists");
                    final JSONObjectTranslatable artists = new JSONObjectTranslatable();
                    for(Object artistObj : artistsArray) {
                        final JSONObject artistJSON = (JSONObject) artistObj;
                        final String id = artistJSON.getString("id"), name = LocalServer.fixEscapeValues(artistJSON.getString("name"));
                        artists.put(id, name);
                        artists.addTranslatedKey(id);
                    }

                    final String name = trackJSON.getString("name");
                    final String previewURL = trackJSON.get("preview_url") instanceof String ? trackJSON.getString("preview_url") : null;
                    final boolean isExplicit = trackJSON.getBoolean("explicit");
                    final long duration = trackJSON.getLong("duration_ms");
                    final EventSources sources = new EventSources();
                    final JSONObject externalURLs = trackJSON.getJSONObject("external_urls");
                    if(externalURLs.has("spotify")) {
                        sources.add(new EventSource("Spotify: " + name, externalURLs.getString("spotify")));
                    }
                    final SpotifyTrack track = new SpotifyTrack(artists, trackImageURL, isExplicit, duration, previewURL, sources);
                    tracks.put(name, track);
                }
            }

            final String identifier = getEventDateIdentifier(dateString, description);
            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(identifier, description, null, null);
            putLoadedPreUpcomingEvent(preUpcomingEvent.toLoadedPreUpcomingEventWithImageURL(type, imageURL));

            final EventSources sources = new EventSources();
            sources.add(new EventSource(title + " Playlist", "https://open.spotify.com/playlist/37i9dQZF1DX4JAvHpjipBk"));
            final SpotifyNewMusicFridayEvent event = new SpotifyNewMusicFridayEvent(eventDate, title, description, imageURL, tracks, sources);
            putUpcomingEvent(identifier, event);
        }
    }
}
