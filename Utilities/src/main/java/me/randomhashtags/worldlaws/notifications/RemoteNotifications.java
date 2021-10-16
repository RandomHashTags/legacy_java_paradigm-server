package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import org.json.JSONObject;

import java.util.HashSet;

public enum RemoteNotifications implements RestAPI {
    ;
    private static final boolean IS_PRODUCTION = false;

    public void sendAppleNotification(NotificationCategory category, String body, HashSet<String> deviceTokens) {
        final String serverIP = IS_PRODUCTION ? "https://api.push.apple.com" : "https://api.sandbox.push.apple.com";

        final JSONObject json = new JSONObject();

        final JSONObject aps = new JSONObject();
        aps.put("alert", body);
        aps.put("badge", "1");
        aps.put("sound", "default");
        aps.put("category", category.name());

        json.put("aps", aps);

        deviceTokens.parallelStream().forEach(deviceToken -> {
            requestJSONObject(serverIP, RequestMethod.POST, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                }
            });
        });
    }
}
