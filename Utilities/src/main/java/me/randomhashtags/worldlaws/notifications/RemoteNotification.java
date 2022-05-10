package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.TargetServer;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

public final class RemoteNotification extends JSONObject implements Jsonable {

    public RemoteNotification(RemoteNotificationSubcategory subcategory, boolean badge, String subtitle, String body, String openNotificationPath) {
        super();
        final String uuid = "***REMOVED***";
        put("uuid", uuid);
        put("category", subcategory.getCategory().getID());
        put("subcategory", subcategory.getID());
        put("title", subcategory.getName());
        if(subtitle != null) {
            put("subtitle", subtitle);
        }
        if(body != null) {
            put("body", body);
        }
        if(badge) {
            put("badge", true);
        }
        if(openNotificationPath != null) {
            put("openNotificationPath", openNotificationPath);
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
        return RemoteNotificationCategory.valueOfString(getString("category"));
    }
    public RemoteNotificationSubcategory getSubcategory() {
        return getCategory().valueOfSubcategory(getString("subcategory"));
    }
    public boolean hasBadge() {
        return optBoolean("badge", false);
    }
    public String getSubtitle() {
        return optString("subtitle", null);
    }
    public String getBody() {
        return optString("body", null);
    }
    public String getOpenNotificationPath() {
        return optString("openNotificationPath", null);
    }

    private void save() {
        final LocalDate now = LocalDate.now();
        final int year = now.getYear(), day = now.getDayOfMonth();
        final Month month = now.getMonth();
        final String uuid = getUUID();
        final Folder folder = Folder.REMOTE_NOTIFICATIONS;
        final RemoteNotificationCategory category = getCategory();
        final RemoteNotificationSubcategory subcategory = getSubcategory();
        final String fileName = folder.getFolderName()
                .replace("%year%", Integer.toString(year))
                .replace("%month%", month.name())
                .replace("%day%", Integer.toString(day))
                .replace("%category%", category.getID())
                .replace("%subcategory%", subcategory.getID())
                ;
        folder.setCustomFolderName(uuid, fileName);
        setFileJSON(folder, uuid, toString());
    }

    public static void pushPending() {
        final String identifier = Settings.Server.getUUID();
        final APIVersion apiVersion = APIVersion.v1;
        final String string = TargetServer.REMOTE_NOTIFICATIONS.sendResponse(identifier, "***REMOVED***" + identifier, apiVersion.name(), apiVersion, "push_pending", true);
    }
}
