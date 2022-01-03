package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class RemoteNotification extends JSONObject {
    private final NotificationCategory category;
    private final boolean badge;

    public RemoteNotification(NotificationCategory category, boolean badge, String title, String subtitle, String body) {
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

    public NotificationCategory getCategory() {
        return category;
    }
    public boolean hasBadge() {
        return badge;
    }
}
