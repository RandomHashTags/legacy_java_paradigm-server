package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

import java.util.HashSet;

public final class SpotifyTrack extends JSONObjectTranslatable {

    public SpotifyTrack(JSONObject json) {
        for(String key : json.keySet()) {
            put(key, json.get(key));
        }
    }
    public SpotifyTrack(String name, JSONObjectTranslatable artists, String imageURL, boolean isExplicit, long duration, String previewURL, EventSources sources) {
        put("name", name);
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

    @Override
    public HashSet<String> getTranslatedKeys() {
        return collectKeys("artists");
    }
}
