package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.RequestMethod;
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

    @Override
    public String refresh() {
        final String clientID = Settings.PrivateValues.Twitch.getClientID();
        final HashMap<String, String> headers = new HashMap<>() {{
            put("Client-ID", clientID);
            put("Accept", "application/vnd.twitchtv.v5+json");
        }};
        final HashMap<String, String> query = new HashMap<>();
        query.put("limit", Integer.toString(Settings.PrivateValues.Twitch.getRequestLimit()));
        query.put("trending", "false");
        final List<String> types = Arrays.asList("day", "week", "month", "all");
        final JSONObject json = new JSONObject();
        new ParallelStream<String>().stream(types, type -> {
            final JSONObject typeJSON = refresh(headers, query, type);
            if(typeJSON != null) {
                json.put(type, typeJSON);
            }
        });
        return json.toString();
    }
    private JSONObject refresh(HashMap<String, String> headers, HashMap<String, String> query, String type) {
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
                final String channelURL = clipJSON.getString("channel_url");
                final String broadcasterName = broadcasterJSON.getString("display_name"), broadcasterProfileImageURL = broadcasterJSON.getString("logo");
                final ClipBroadcaster broadcaster = new ClipBroadcaster(broadcasterName, channelURL, broadcasterProfileImageURL);

                final EventSources sources = new EventSources();
                sources.add(new EventSource("Twitch: Clip URL", clipURL));

                final Clip clip = new Clip(title, broadcaster, game, thumbnail, viewCount, duration, embedHTML, sources);
                clips.put(slug, clip.toJSONObject());
            });
            clipsJSON = clips;
        }
        return clipsJSON;
    }
}
