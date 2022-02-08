package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

public final class RemoteNotification extends JSONObject implements Jsonable {

    public RemoteNotification(RemoteNotificationCategory category, boolean badge, String title, String subtitle, String body) {
        super();
        final String uuid = "***REMOVED***";
        put("uuid", uuid);
        put("category", category.name());
        put("badge", badge);
        if(title != null) {
            put("title", LocalServer.fixEscapeValues(title));
        }
        if(subtitle != null) {
            put("subtitle", LocalServer.fixEscapeValues(subtitle));
        }
        if(body != null) {
            put("body", LocalServer.fixEscapeValues(body));
        }
        save();
    }
    public RemoteNotification(JSONObject json) {
        for(String key : json.keySet()) {
            json.put(key, json.get(key));
        }
    }

    public String getUUID() {
        return getString("uuid");
    }
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.valueOf(getString("category"));
    }
    public boolean hasBadge() {
        return getBoolean("badge");
    }
    public String getTitle() {
        return getString("title");
    }
    public String getSubtitle() {
        return getString("subtitle");
    }
    public String getBody() {
        return getString("body");
    }

    private void save() {
        final LocalDate now = LocalDate.now();
        final int year = now.getYear(), day = now.getDayOfMonth();
        final Month month = now.getMonth();
        final String uuid = getString("uuid");
        final Folder folder = Folder.REMOTE_NOTIFICATIONS;
        final String fileName = folder.getFolderName().replace("%year%", Integer.toString(year)).replace("%month%", month.name()).replace("%day%", Integer.toString(day));
        folder.setCustomFolderName(uuid, fileName);
        setFileJSON(folder, uuid, toString());
    }

    public static void pushPending() {
        final String identifier = Settings.Server.getUUID();
        final String string = TargetServer.REMOTE_NOTIFICATIONS.sendResponse(APIVersion.v1, identifier, RequestMethod.POST, "push_pending", null);
    }
}
