package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public enum RemoteNotifications implements RestAPI {
    INSTANCE;

    private final boolean productionMode;

    RemoteNotifications() {
        productionMode = Jsonable.getSettingsPrivateValuesJSON().getJSONObject("apple").getBoolean("production_mode");
    }

    public void sendAppleNotification(RemoteNotification notification) {
        final JSONArray deviceTokens = Jsonable.getStaticFileJSONArray(Folder.OTHER, "apple");
        if(deviceTokens != null && !deviceTokens.isEmpty()) {
            final String key = productionMode ? "" : "sandbox.";
            final String url = "https://api." + key + "push.apple.com:443";

            final JSONObject json = new JSONObject();

            final JSONObject aps = new JSONObject();
            aps.put("alert", notification);
            aps.put("badge", notification.hasBadge() ? 1 : 0);
            aps.put("sound", "default");
            aps.put("category", notification.getCategory().name());

            json.put("aps", aps);

            final String uuid = "***REMOVED***";
            ParallelStream.stream(deviceTokens.spliterator(), deviceTokenObj -> {
                final String deviceToken = (String) deviceTokenObj;
                final HashMap<String, String> headers = new HashMap<>();
                headers.put("path", "/3/device/" + deviceToken);
                headers.put("apns-id", uuid);
                headers.put("apns-expiration", "0");
                headers.put("apns-priority", "5");
                requestJSONObject(url, RequestMethod.POST, new CompletionHandler() {
                    @Override
                    public void handleJSONObject(JSONObject json) {
                    }
                });
            });
        }
    }
}
