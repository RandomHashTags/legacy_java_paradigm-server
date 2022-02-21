package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.RefreshableService;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum TwitchClips implements RefreshableService {
    INSTANCE;

    // TODO: Have to migrate to https://dev.twitch.tv/docs/api/reference#get-clips due to deprecation and shutdown of v5 Twitch API (kraken)

    private final HashMap<String, String> types, ids;
    private final List<String> clipTypes;
    private final String thumbnailURLPrefix;

    TwitchClips() {
        types = new HashMap<>();
        ids = new HashMap<>();
        clipTypes = Arrays.asList("day", "week", "month", "all");
        thumbnailURLPrefix = "https://clips-media-assets.twitch.tv/";
    }

    public String getResponse(String target) {
        final String[] values = target.split("/");
        switch (values[0]) {
            case "id":
                return getClip(values[1]);
            default:
                if(clipTypes.contains(target) || target.equals("getAll")) {
                    if(types.isEmpty()) {
                        refresh();
                    }
                    return types.get(target);
                }
                return null;
        }
    }

    @Override
    public String refresh() {
        final long started = System.currentTimeMillis();
        final String string = refreshKraken();
        WLLogger.logInfo("TwitchClips - refreshed (took " + WLUtilities.getElapsedTime(started) + ")");
        return string;
    }

    private String refreshHelix() {
        final String url = "https://api.twitch.tv/helix/clips";
        final HashMap<String, String> headers = new HashMap<>();
        final HashMap<String, String> query = new HashMap<>();
        query.put("game_id", null);
        query.put("first", Integer.toString(Settings.PrivateValues.Twitch.getRequestLimit()));
        return null;
    }

    private List<String> getSupportedGameIDs() {
        return Arrays.asList(
                "" // minecraft
        );
    }

    private String refreshKraken() {
        types.clear();
        final String clientID = Settings.PrivateValues.Twitch.getClientID();
        final HashMap<String, String> headers = new HashMap<>() {{
            put("Client-ID", clientID);
            put("Accept", "application/vnd.twitchtv.v5+json");
        }};
        final HashMap<String, String> query = new HashMap<>();
        query.put("limit", Integer.toString(Settings.PrivateValues.Twitch.getRequestLimit()));
        query.put("trending", "true");
        final JSONObject clipsJSON = new JSONObject();
        new ParallelStream<String>().stream(clipTypes, type -> {
            final JSONObject typeJSON = refreshKraken(headers, query, type);
            if(typeJSON != null) {
                clipsJSON.put(type, typeJSON);
                types.put(type, typeJSON.toString());
            }
        });
        final JSONObject json = new JSONObject();
        json.put("clips", clipsJSON);
        json.put("thumbnailURLPrefix", thumbnailURLPrefix);

        final String string = json.toString();
        types.put("getAll", string);
        return string;
    }
    private JSONObject refreshKraken(HashMap<String, String> headers, HashMap<String, String> query, String type) {
        final String url = "https://api.twitch.tv/kraken/clips/top";
        query.put("period", type);
        final JSONObject json = requestJSONObject(url, RequestMethod.GET, headers, query);
        JSONObject clipsJSON = null;
        if(json != null) {
            final JSONArray clipsArray = json.getJSONArray("clips");
            final JSONObject clips = new JSONObject();
            new ParallelStream<JSONObject>().stream(clipsArray.spliterator(), clipJSON -> {
                final String slug = clipJSON.getString("slug");

                final String clipURL = clipJSON.getString("url"), embedHTML = clipJSON.getString("embed_html");
                final String title = clipJSON.getString("title"), game = clipJSON.getString("game");
                final String thumbnail = clipJSON.getJSONObject("thumbnails").getString("medium");
                final long viewCount = clipJSON.getLong("views");
                final float duration = clipJSON.getFloat("duration");

                final JSONObject broadcasterJSON = clipJSON.getJSONObject("broadcaster");
                final String channelURL = broadcasterJSON.getString("channel_url");
                final String broadcasterName = broadcasterJSON.getString("display_name"), broadcasterProfileImageURL = broadcasterJSON.getString("logo");
                final ClipBroadcaster broadcaster = new ClipBroadcaster(broadcasterName, channelURL, broadcasterProfileImageURL);

                final EventSources sources = new EventSources();
                sources.add(new EventSource("Twitch: Clip URL", clipURL));

                final Clip clip = new Clip(title, broadcaster, game, thumbnail, viewCount, duration, embedHTML, sources);
                clips.put(slug, clip.toPreJSONObject());
                ids.put(slug, clip.toString());
            });
            clipsJSON = clips;
        }
        return clipsJSON;
    }

    private String getClip(String id) {
        return ids.get(id);
    }
}
