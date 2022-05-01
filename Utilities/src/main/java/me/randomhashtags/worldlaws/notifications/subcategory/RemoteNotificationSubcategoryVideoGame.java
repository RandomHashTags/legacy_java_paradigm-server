package me.randomhashtags.worldlaws.notifications.subcategory;

import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;

public enum RemoteNotificationSubcategoryVideoGame implements RemoteNotificationSubcategory {
    VIDEO_GAME_UPDATE,
    ;

    @Override
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.VIDEO_GAME;
    }

    @Override
    public String getName() {
        switch (this) {
            case VIDEO_GAME_UPDATE: return "Video Game Update";
            default: return null;
        }
    }
}
