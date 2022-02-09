package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class ClipBroadcaster extends JSONObject {
    public ClipBroadcaster(String name, String channelURL, String profileImageURL) {
        put("name", LocalServer.fixEscapeValues(name));
        put("channelURL", channelURL);
        put("profileImageURL", profileImageURL);
    }
}
