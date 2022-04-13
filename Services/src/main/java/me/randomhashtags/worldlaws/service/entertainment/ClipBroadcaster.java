package me.randomhashtags.worldlaws.service.entertainment;

import org.json.JSONObject;

public final class ClipBroadcaster extends JSONObject {
    public ClipBroadcaster(String name, String channelURL, String profileImageURL) {
        put("name", name);
        put("channelURL", channelURL);
        put("profileImageURL", profileImageURL);
    }
}
