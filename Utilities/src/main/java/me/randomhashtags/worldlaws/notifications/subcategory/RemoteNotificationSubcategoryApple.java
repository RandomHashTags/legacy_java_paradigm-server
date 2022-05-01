package me.randomhashtags.worldlaws.notifications.subcategory;

import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;

public enum RemoteNotificationSubcategoryApple implements RemoteNotificationSubcategory {
    IOS_UPDATE,
    IPADOS_UPDATE,
    MACOS_UPDATE,
    MACOS_SECURITY_UPDATE,
    WATCHOS_UPDATE,
    TVOS_UPDATE,
    SAFARI_UPDATE,
    XCODE_UPDATE,
    ;

    @Override
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.APPLE;
    }

    private String getType() {
        switch (this) {
            case IOS_UPDATE: return "***REMOVED***";
            case IPADOS_UPDATE: return "***REMOVED***";
            case MACOS_UPDATE: return "***REMOVED***";
            case MACOS_SECURITY_UPDATE: return "***REMOVED***";
            case WATCHOS_UPDATE: return "***REMOVED***";
            case TVOS_UPDATE: return "***REMOVED***";
            case SAFARI_UPDATE: return "***REMOVED***";
            case XCODE_UPDATE: return "***REMOVED***";
            default: return "Unknown";
        }
    }
    @Override
    public String getName() {
        final String suffix = " Update";
        return getType() + suffix;
    }
}
