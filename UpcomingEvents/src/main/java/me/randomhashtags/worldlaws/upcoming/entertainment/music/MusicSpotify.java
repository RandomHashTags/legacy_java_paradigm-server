package me.randomhashtags.worldlaws.upcoming.entertainment.music;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.service.SpotifyService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum MusicSpotify implements SpotifyService {
    INSTANCE;

    public void getNewMusicFriday(CompletionHandler handler) {
        getSpotifyPlaylistJSON("37i9dQZF1DX4JAvHpjipBk", new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final String description = LocalServer.fixEscapeValues(json.getString("description"));
                    final JSONArray tracks = json.getJSONObject("tracks").getJSONArray("items");
                    final int max = tracks.length();
                    final AtomicInteger completed = new AtomicInteger(0);
                    final HashSet<String> values = new HashSet<>();
                    StreamSupport.stream(tracks.spliterator(), true).forEach(track -> {
                        final JSONObject trackJSON = ((JSONObject) track).getJSONObject("track");
                        final String name = trackJSON.getString("name");
                        final boolean isExplicit = trackJSON.getBoolean("explicit");
                        final long duration = trackJSON.getLong("duration_ms");
                        final EventSources sources = new EventSources();
                        final JSONObject externalURLs = trackJSON.getJSONObject("external_urls");
                        if(externalURLs.has("spotify")) {
                            sources.append(new EventSource("Spotify: " + name, externalURLs.getString("spotify")));
                        }
                        if(completed.addAndGet(1) == max) {
                            final StringBuilder builder = new StringBuilder("{");
                            builder.append("\"description\":\"").append(description).append("\"");
                            for(String value : values) {
                                builder.append(",").append(value);
                            }
                            builder.append("}");

                            handler.handleString(builder.toString());
                        }
                    });
                } else {
                    handler.handleString(null);
                }
            }
        });
    }
}
