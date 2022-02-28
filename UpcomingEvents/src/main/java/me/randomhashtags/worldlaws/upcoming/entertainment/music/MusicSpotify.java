package me.randomhashtags.worldlaws.upcoming.entertainment.music;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public enum MusicSpotify implements SpotifyService {
    INSTANCE;

    public String getNewMusicFriday(CompletionHandler handler) {
        final JSONObject json = getSpotifyPlaylistJSON("37i9dQZF1DX4JAvHpjipBk");
        String string = null;
        if(json != null) {
            final String description = LocalServer.fixEscapeValues(json.getString("description"));
            final JSONArray tracks = json.getJSONObject("tracks").getJSONArray("items");
            final HashSet<String> values = new HashSet<>();
            new CompletableFutures<JSONObject>().stream(tracks.spliterator(), track -> {
                final JSONObject trackJSON = track.getJSONObject("track");
                final String name = trackJSON.getString("name");
                final boolean isExplicit = trackJSON.getBoolean("explicit");
                final long duration = trackJSON.getLong("duration_ms");
                final EventSources sources = new EventSources();
                final JSONObject externalURLs = trackJSON.getJSONObject("external_urls");
                if(externalURLs.has("spotify")) {
                    sources.add(new EventSource("Spotify: " + name, externalURLs.getString("spotify")));
                }
            });
            final StringBuilder builder = new StringBuilder("{");
            builder.append("\"description\":\"").append(description).append("\"");
            for(String value : values) {
                builder.append(",").append(value);
            }
            builder.append("}");

            string = builder.toString();
        }
        return string;
    }
}
