package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class ClipBroadcaster {

    private final String name, channelURL, profileImageURL;

    public ClipBroadcaster(String name, String channelURL, String profileImageURL) {
        this.name = LocalServer.fixEscapeValues(name);
        this.channelURL = channelURL;
        this.profileImageURL = profileImageURL;
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("channelURL", channelURL);
        json.put("profileImageURL", profileImageURL);
        return json;
    }
}
