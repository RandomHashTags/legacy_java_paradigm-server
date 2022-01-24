package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public enum AppleNotifications implements DeviceTokenController {
    INSTANCE;

    private final HashSet<String> deviceTokens;

    AppleNotifications() {
        deviceTokens = new HashSet<>();
        final JSONArray array = Jsonable.getStaticFileJSONArray(Folder.DEVICE_TOKENS, "apple");
        if(array != null) {
            for(Object obj : array) {
                deviceTokens.add((String) obj);
            }
        }
    }

    @Override
    public void save() {
        Jsonable.setFileJSONArray(Folder.OTHER, "apple", new JSONArray(deviceTokens));
    }

    @Override
    public void register(String deviceToken) {
        deviceTokens.add(deviceToken);
    }

    @Override
    public void sendNotification(RemoteNotification notification) {
        if(!deviceTokens.isEmpty()) {
            final String key = Settings.PrivateValues.Apple.isProductionMode() ? "" : "sandbox.";
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
                final JSONObject postJSON = requestJSONObject(url, RequestMethod.POST);
            });
        }
    }
}
