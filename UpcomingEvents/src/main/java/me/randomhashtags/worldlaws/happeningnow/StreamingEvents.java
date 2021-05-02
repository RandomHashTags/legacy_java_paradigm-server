package me.randomhashtags.worldlaws.happeningnow;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.EventController;
import me.randomhashtags.worldlaws.UpcomingEventType;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public enum StreamingEvents implements EventController, DataValues {
    TWITCH(null,
            "https://api.twitch.tv/helix/streams",
            "Client-ID",
            TWITCH_CLIENT_ID,
            TWITCH_ACCESS_TOKEN,
            new HashMap<>() {{
                put("first", Integer.toString(TWITCH_REQUEST_LIMIT));
            }}
    ),
    YOUTUBE(null,
            "https://www.googleapis.com/youtube/v3/search",
            YOUTUBE_KEY_IDENTIFIER,
            YOUTUBE_KEY_VALUE,
            new HashMap<>() {{
                put("part", "id");
                put("eventType", "live");
                put("type", "video");
                put("maxResults", Integer.toString(YOUTUBE_REQUEST_LIMIT));
                put("order", "viewCount");
                put("key", YOUTUBE_KEY);
            }}
    ),
    ;

    private final UpcomingEventType type;
    private String url, json;
    private final HashMap<String, String> headers, query;

    StreamingEvents(UpcomingEventType type, String url, String key, String value, HashMap<String, String> query) {
        this(type, url, key, value, null, query);
    }
    StreamingEvents(UpcomingEventType type, String url, String key, String value, String accessToken, HashMap<String, String> query) {
        this.type = type;
        this.url = url;
        headers = new HashMap<>();
        headers.putAll(CONTENT_HEADERS);
        headers.put(key, value);
        if(accessToken != null) {
            headers.put("Authorization", "Bearer " + accessToken);
        }
        this.query = query;
        autoupdate();
    }

    private void autoupdate() {
        final long THIRTY_MIN = 1000*60*30;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                load(null);
            }
        }, THIRTY_MIN, THIRTY_MIN);
    }

    @Override
    public UpcomingEventType getType() {
        return type;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    @Override
    public HashMap<String, NewPreUpcomingEvent> getPreEventURLs() {
        return null;
    }

    @Override
    public HashMap<String, String> getPreUpcomingEvents() {
        return null;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return null;
    }

    @Override
    public void load(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final boolean isTwitch = this == TWITCH;
        requestJSONObject(url, RequestMethod.GET, headers, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject response) {
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                if(isTwitch) {
                    final int minimum = 40_000;
                    final JSONArray array = response.getJSONArray("data");
                    for(Object obj : array) {
                        final JSONObject streamJSON = (JSONObject) obj;
                        final int viewers = streamJSON.getInt("viewer_count");
                        if(viewers >= minimum) {
                            final String streamerName = streamJSON.getString("user_name"), title = streamJSON.getString("title"), thumbnailURL = streamJSON.getString("thumbnail_url");
                            final Livestream stream = new Livestream(streamerName, title, thumbnailURL, viewers, LivestreamType.TWITCH);
                            builder.append(isFirst ? "" : ",").append(stream.toString());
                            isFirst = false;
                        }
                    }
                } else {
                    final int minimum = 40_000;
                }
                builder.append("]");
                json = builder.toString();
                WLLogger.log(Level.INFO, "StreamingEvents - updated " + getType() + " live streams (took " + (System.currentTimeMillis()-started) + "ms)");
                if(handler != null) {
                    handler.handle(json);
                }
            }
        });
    }

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
    }
}
