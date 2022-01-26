package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.TargetServer;
import org.json.JSONObject;

public final class RemoteNotification extends JSONObject {
    private final RemoteNotificationCategory category;
    private final boolean badge;

    public RemoteNotification(RemoteNotificationCategory category, boolean badge, String title, String subtitle, String body) {
        super();
        this.category = category;
        this.badge = badge;
        if(title != null) {
            put("title", LocalServer.fixEscapeValues(title));
        }
        if(subtitle != null) {
            put("subtitle", LocalServer.fixEscapeValues(subtitle));
        }
        if(body != null) {
            put("body", LocalServer.fixEscapeValues(body));
        }
    }

    public RemoteNotificationCategory getCategory() {
        return category;
    }
    public boolean hasBadge() {
        return badge;
    }

    public void send() {
        TargetServer.REMOTE_NOTIFICATIONS.sendResponse(APIVersion.v1, null, null, null, null);
    }
}
