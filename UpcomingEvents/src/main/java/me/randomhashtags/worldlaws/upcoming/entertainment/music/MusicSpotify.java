package me.randomhashtags.worldlaws.upcoming.entertainment.music;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public enum MusicSpotify implements SpotifyService {
    INSTANCE;

    public void getNewMusicFriday(CompletionHandler handler) {
        getSpotifyPlaylistJSON("37i9dQZF1DX4JAvHpjipBk", new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final String description = LocalServer.fixEscapeValues(json.getString("description"));
                    final JSONArray tracks = json.getJSONObject("tracks").getJSONArray("items");
                    final HashSet<String> values = new HashSet<>();
                    ParallelStream.stream(tracks.spliterator(), track -> {
                        final JSONObject trackJSON = ((JSONObject) track).getJSONObject("track");
                        final String name = trackJSON.getString("name");
                        final boolean isExplicit = trackJSON.getBoolean("explicit");
                        final long duration = trackJSON.getLong("duration_ms");
                        final EventSources sources = new EventSources();
                        final JSONObject externalURLs = trackJSON.getJSONObject("external_urls");
                        if(externalURLs.has("spotify")) {
                            sources.append(new EventSource("Spotify: " + name, externalURLs.getString("spotify")));
                        }
                    });
                    final StringBuilder builder = new StringBuilder("{");
                    builder.append("\"description\":\"").append(description).append("\"");
                    for(String value : values) {
                        builder.append(",").append(value);
                    }
                    builder.append("}");

                    handler.handleString(builder.toString());
                } else {
                    handler.handleString(null);
                }
            }
        });
    }
}
