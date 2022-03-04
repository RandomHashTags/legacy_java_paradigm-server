package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public enum AppleNotifications implements DeviceTokenController {
    // https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server
    // https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_token-based_connection_to_apns
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
    public void unregister(String deviceToken) {
        deviceTokens.remove(deviceToken);
    }

    private String getConnectionToken() {
        final JSONObject json = getJSONObject(Folder.OTHER, "appleRemoteNotificationsConnectionToken", new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return generateConnectionTokenJSON(System.currentTimeMillis()/1000);
            }
        });
        final String connectionToken;
        if(json.getLong("iat") + (30 * 60) <= System.currentTimeMillis()/1000) {
            connectionToken = refreshConnectionToken();
        } else {
            connectionToken = generateConnectionToken(json.getLong("iat"));
        }
        return Base64.getUrlEncoder().encodeToString(connectionToken.getBytes());
    }
    private String refreshConnectionToken() {
        return generateConnectionToken(System.currentTimeMillis()/1000);
    }
    private String generateConnectionToken(long issuedTime) {
        final JSONObject json = generateConnectionTokenJSON(issuedTime);
        return "{" +
                "\"alg\":\"" + json.getString("alg") + "\"," +
                "\"kid\":\"" + json.getString("kid") + "\"" +
                "}" +
                "{" +
                "\"iss\":\"" + json.getString("iss") + "\"," +
                "\"iat\":" + json.getLong("iat") +
                "}";
    }
    private JSONObject generateConnectionTokenJSON(long issuedTime) {
        final JSONObject json = new JSONObject();
        json.put("alg", "ES256");
        json.put("kid", Settings.PrivateValues.Apple.getRemoteNotificationsEncryptionKeyID());
        json.put("iss", Settings.PrivateValues.Apple.getRemoteNotificationsIssuerKey());
        json.put("iat", issuedTime);
        return json;
    }

    @Override
    public void sendNotification(RemoteNotification notification) {
        final long started = System.currentTimeMillis();
        final String uuid = notification.getUUID();
        if(!deviceTokens.isEmpty()) {
            final JSONObject json = new JSONObject();
            final JSONObject aps = new JSONObject();
            aps.put("alert", notification);
            aps.put("badge", notification.hasBadge() ? 1 : 0);
            aps.put("sound", "default");
            aps.put("category", notification.getCategory().name());
            json.put("aps", aps);

            final String connectionToken = getConnectionToken();
            final HashMap<String, String> primaryHeaders = new HashMap<>();
            primaryHeaders.put("authorization", "bearer " + connectionToken);
            primaryHeaders.put("apns-id", uuid);
            primaryHeaders.put("apns-expiration", "0");
            primaryHeaders.put("apns-priority", "5");

            final String key = Settings.PrivateValues.Apple.isProductionMode() ? "" : "sandbox.";
            final String url = "https://api." + key + "push.apple.com:443";
            new CompletableFutures<String>().stream(deviceTokens.spliterator(), deviceToken -> {
                final LinkedHashMap<String, String> headers = new LinkedHashMap<>(primaryHeaders);
                headers.put("path", "/3/device/" + deviceToken);
                final JSONObject postJSON = postJSONObject(url, null, true, headers, null);
            });
        }
        WLLogger.logInfo("AppleNotifications - sent " + uuid + " to " + deviceTokens.size() + " devices (took " + WLUtilities.getElapsedTime(started) + ")");
    }
}
