package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public enum AppleNotifications implements DeviceTokenController { // https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server
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

    private String getConnectionToken() {
        final JSONObject json = getJSONObject(Folder.OTHER, "appleConnectionToken", new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return null;
            }
        });
        String connectionToken = null;
        if(json == null) {
            WLLogger.logError(this, "failed to refresh connection token to the Apple Push Notification service (APNs)!");
        } else {
            connectionToken = "{" +
                    "\"alg\":\"ES256\"," +
                    "\"kid\":\"" + Settings.PrivateValues.Apple.getRemoteNotificationsEncryptionKeyID() + "\"" +
                    "}" +
                    "{" +
                    "\"iss\":\"" + Settings.PrivateValues.Apple.getRemoteNotificationsIssuerKey() + "\"," +
                    "\"iat\":" + // TODO: finish (https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_token-based_connection_to_apns)
                    "}";
        }
        return connectionToken != null ? Base64.getUrlEncoder().encodeToString(connectionToken.getBytes()) : null;
    }

    @Override
    public void sendNotification(RemoteNotification notification) {
        if(!deviceTokens.isEmpty()) {
            final String connectionToken = getConnectionToken();
            if(connectionToken != null) {
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
                    headers.put("authorization", "bearer " + connectionToken);
                    headers.put("path", "/3/device/" + deviceToken);
                    headers.put("apns-id", uuid);
                    headers.put("apns-expiration", "0");
                    headers.put("apns-priority", "5");
                    final JSONObject postJSON = requestJSONObject(url, RequestMethod.POST, headers);
                });
            }
        }
    }
}
