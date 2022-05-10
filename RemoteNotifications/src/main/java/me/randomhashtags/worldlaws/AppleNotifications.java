package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public enum AppleNotifications implements RemoteNotificationDeviceTokenController {
    // https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server
    // https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_token-based_connection_to_apns
    INSTANCE;

    private final HashMap<RemoteNotificationSubcategory, HashSet<String>> deviceTokens;
    private String connectionToken;

    AppleNotifications() {
        deviceTokens = new HashMap<>();
        insertDeviceTokens(deviceTokens);
    }

    @Override
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.APPLE;
    }

    @Override
    public DeviceTokenType getDeviceTokenType() {
        return DeviceTokenType.APPLE;
    }

    @Override
    public HashMap<RemoteNotificationSubcategory, HashSet<String>> getDeviceTokens() {
        return deviceTokens;
    }

    public void tryUpdatingToken() {
        final Folder folder = Folder.DEVICE_TOKENS;
        final String fileName = "connectionToken";
        folder.setCustomFolderName(fileName, getCategoryFolderName());
        final JSONObject json = getJSONObject(Folder.DEVICE_TOKENS, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return generateConnectionTokenJSON(System.currentTimeMillis()/1000);
            }
        });
        final String token;
        if(json.getLong("iat") + (30 * 60) <= System.currentTimeMillis()/1000) {
            token = refreshConnectionToken();
        } else {
            token = generateConnectionToken(json.getLong("iat"));
        }
        connectionToken = Base64.getUrlEncoder().encodeToString(token.getBytes());
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
        final RemoteNotificationSubcategory category = notification.getSubcategory();
        if(!deviceTokens.get(category).isEmpty()) {
            final HashSet<String> tokens = deviceTokens.get(category);
            final JSONObject aps = new JSONObject();
            aps.put("alert", notification);
            aps.put("badge", notification.hasBadge() ? 1 : 0);
            aps.put("sound", "default");
            aps.put("category", notification.getCategory().name());

            final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
            postData.put("aps", aps);
            postData.put("subcategory", notification.getSubcategory().getName());
            final String openNotificationPath = notification.getOpenNotificationPath();
            if(openNotificationPath != null) {
                postData.put("openNotificationPath", openNotificationPath);
            }

            final HashMap<String, String> primaryHeaders = new HashMap<>();
            primaryHeaders.put("authorization", "bearer " + connectionToken);
            primaryHeaders.put("apns-id", uuid);
            primaryHeaders.put("apns-expiration", "0");
            primaryHeaders.put("apns-priority", "5");
            primaryHeaders.put("apns-push-type", "alert");
            primaryHeaders.put("apns-topic", "***REMOVED***");

            final String key = Settings.PrivateValues.Apple.isProductionMode() ? "" : "sandbox.";
            final String url = "https://api." + key + "push.apple.com:443";
            new CompletableFutures<String>().stream(tokens, deviceToken -> {
                final LinkedHashMap<String, String> headers = new LinkedHashMap<>(primaryHeaders);
                headers.put("path", "/3/device/" + deviceToken);
                final JSONObject postJSON = postJSONObject(url, postData, true, headers);
            });
            WLLogger.logInfo("AppleNotifications - sent " + uuid + " (" + category.name() + ") to " + tokens.size() + " devices (took " + WLUtilities.getElapsedTime(started) + ")");
        }
    }
}
