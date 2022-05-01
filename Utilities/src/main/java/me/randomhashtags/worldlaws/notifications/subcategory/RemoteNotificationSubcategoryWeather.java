package me.randomhashtags.worldlaws.notifications.subcategory;

import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;

public enum RemoteNotificationSubcategoryWeather implements RemoteNotificationSubcategory {
    LOCAL_ALERT,
    ;

    @Override
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.WEATHER;
    }

    @Override
    public String getName() {
        switch (this) {
            case LOCAL_ALERT: return "Local Weather Alert";
            default: return null;
        }
    }
}
