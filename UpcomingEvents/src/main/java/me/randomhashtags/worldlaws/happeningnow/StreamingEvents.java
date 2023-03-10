package me.randomhashtags.worldlaws.happeningnow;

public enum StreamingEvents {//implements UpcomingEventController, DataValues {
    /*
    TWITCH(null,
            "https://api.twitch.tv/helix/streams",
            "Client-ID",
            TWITCH_CLIENT_ID,
            TWITCH_ACCESS_TOKEN,
            new LinkedHashMap<>() {{
                put("first", Integer.toString(TWITCH_REQUEST_LIMIT));
            }}
    ),
    YOUTUBE(null,
            "https://youtube.googleapis.com/youtube/v3/search",
            YOUTUBE_KEY_IDENTIFIER,
            YOUTUBE_KEY_VALUE,
            new LinkedHashMap<>() {{
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
    private final HashMap<String, String> headers;
    private final LinkedHashMap<String, String> query;

    StreamingEvents(UpcomingEventType type, String url, LinkedHashMap<String, String> query) {
        this(type, url, null, null, query);
    }
    StreamingEvents(UpcomingEventType type, String url, String key, String value, LinkedHashMap<String, String> query) {
        this(type, url, key, value, null, query);
    }
    StreamingEvents(UpcomingEventType type, String url, String key, String value, String accessToken, LinkedHashMap<String, String> query) {
        this.type = type;
        this.url = url;
        headers = new HashMap<>();
        headers.putAll(CONTENT_HEADERS);
        if(key != null && value != null) {
            headers.put(key, value);
        }
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
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
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
                    handler.handleString(json);
                }
            }
        });
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
    }*/
}
