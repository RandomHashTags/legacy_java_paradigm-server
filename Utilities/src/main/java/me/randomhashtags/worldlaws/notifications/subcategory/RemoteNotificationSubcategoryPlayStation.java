package me.randomhashtags.worldlaws.notifications.subcategory;

import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;

public enum RemoteNotificationSubcategoryPlayStation implements RemoteNotificationSubcategory {
    PLAYSTATION_4_SYSTEM_UPDATE,
    PLAYSTATION_5_SYSTEM_UPDATE,
    ;

    public static RemoteNotificationSubcategoryPlayStation valueOfString(String input) {
        for(RemoteNotificationSubcategoryPlayStation category : RemoteNotificationSubcategoryPlayStation.values()) {
            if(category.name().equalsIgnoreCase(input)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.PLAYSTATION;
    }

    private String getType() {
        switch (this) {
            case PLAYSTATION_4_SYSTEM_UPDATE: return "PlayStation 4 System Update";
            case PLAYSTATION_5_SYSTEM_UPDATE: return "PlayStation 5 System Update";
            default: return "Unknown";
        }
    }
    @Override
    public String getName() {
        return getType();
    }
}
