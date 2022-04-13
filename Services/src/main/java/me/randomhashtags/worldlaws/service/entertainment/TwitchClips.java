package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.RefreshableService;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public enum TwitchClips implements RefreshableService {
    INSTANCE;

    // TODO: Have to migrate to https://dev.twitch.tv/docs/api/reference#get-clips due to deprecation and shutdown of v5 Twitch API (kraken)

    private final HashMap<String, JSONObjectTranslatable> types, ids;
    private final List<String> clipTypes;
    private final String thumbnailURLPrefix;

    TwitchClips() {
        types = new HashMap<>();
        ids = new HashMap<>();
        clipTypes = Arrays.asList("day", "week", "month", "all");
        thumbnailURLPrefix = "https://clips-media-assets.twitch.tv/";
    }

    public JSONObjectTranslatable getResponse(String[] values) {
        final String key = values[0];
        switch (key) {
            case "id":
                return getClip(values[1]);
            default:
                if(clipTypes.contains(key) || key.equals("getAll")) {
                    if(types.isEmpty()) {
                        refresh();
                    }
                    return types.get(key);
                }
                return null;
        }
    }

    @Override
    public JSONObjectTranslatable refresh() {
        final long started = System.currentTimeMillis();
        final JSONObjectTranslatable string = refreshKraken();
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

    private JSONObjectTranslatable refreshKraken() {
        types.clear();
        final String clientID = Settings.PrivateValues.Twitch.getClientID();
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>() {{
            put("Client-ID", clientID);
            put("Accept", "application/vnd.twitchtv.v5+json");
        }};
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("limit", Integer.toString(Settings.PrivateValues.Twitch.getRequestLimit()));
        query.put("trending", "true");
        final JSONObject clipsJSON = new JSONObject();
        new CompletableFutures<String>().stream(clipTypes, type -> {
            final JSONObjectTranslatable typeJSON = refreshKraken(headers, query, type);
            if(typeJSON != null) {
                clipsJSON.put(type, typeJSON);
                types.put(type, typeJSON);
            }
        });
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("clips", clipsJSON);
        json.put("thumbnailURLPrefix", thumbnailURLPrefix);

        types.put("getAll", json);
        return json;
    }
    private JSONObjectTranslatable refreshKraken(LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query, String type) {
        final String url = "https://api.twitch.tv/kraken/clips/top";
        query.put("period", type);
        final JSONObject json = requestJSONObject(url, headers, query);
        JSONObject clipsJSON = null;
        if(json != null) {
            final JSONArray clipsArray = json.getJSONArray("clips");
            final JSONObject clips = new JSONObject();
            new CompletableFutures<JSONObject>().stream(clipsArray, clipJSON -> {
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
                ids.put(slug, clip.toJSONObject());
            });
            clipsJSON = clips;
        }
        return JSONObjectTranslatable.copy(clipsJSON);
    }

    private JSONObjectTranslatable getClip(String id) {
        return ids.get(id);
    }
}
