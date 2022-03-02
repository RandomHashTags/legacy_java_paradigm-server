package me.randomhashtags.worldlaws.upcoming.entertainment.music;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SpotifyNewMusicFridayEvent;
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

    private void refreshNewMusicFriday(LocalDate date) {
        final JSONObject responseJSON = getSpotifyPlaylistJSON("37i9dQZF1DX4JAvHpjipBk");
        if(responseJSON != null) {
            final UpcomingEventType type = getType();
            final String dateString = EventDate.getDateString(date);
            final String imageURL = responseJSON.getJSONArray("images").getJSONObject(0).getString("url");
            final String title = "Spotify: New Music Friday", description = LocalServer.fixEscapeValues(responseJSON.getString("description"));

            final JSONArray tracksArray = responseJSON.getJSONObject("tracks").getJSONArray("items");
            final JSONObject tracks = new JSONObject();
            for(Object obj : tracksArray) {
                final JSONObject trackJSON = (JSONObject) obj;

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
                final JSONObject artists = new JSONObject();
                for(Object artistObj : artistsArray) {
                    final JSONObject artistJSON = (JSONObject) artistObj;
                    final String id = artistJSON.getString("id"), name = LocalServer.fixEscapeValues(artistJSON.getString("name"));
                    artists.put(id, name);
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

            final String identifier = getEventDateIdentifier(dateString, description);
            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(identifier, description, null, null);
            putLoadedPreUpcomingEvent(identifier, preUpcomingEvent.toStringWithImageURL(type, imageURL));

            final EventSources sources = new EventSources();
            sources.add(new EventSource(title + " Playlist", "https://open.spotify.com/playlist/37i9dQZF1DX4JAvHpjipBk"));
            final SpotifyNewMusicFridayEvent event = new SpotifyNewMusicFridayEvent(title, description, imageURL, tracks, sources);
            putUpcomingEvent(identifier, event.toString());
        }
    }

    private final class SpotifyTrack extends JSONObject {

        public SpotifyTrack(JSONObject artists, String imageURL, boolean isExplicit, long duration, String previewURL, EventSources sources) {
            put("artists", artists);
            put("imageURL", imageURL);
            if(isExplicit) {
                put("explicit", true);
            }
            put("duration", duration);
            if(previewURL != null) {
                put("previewURL", previewURL);
            }
            put("sources", sources.toJSONObject());
        }
    }
}
