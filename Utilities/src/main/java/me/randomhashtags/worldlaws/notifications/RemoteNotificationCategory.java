package me.randomhashtags.worldlaws.notifications;

public enum RemoteNotificationCategory {
    SOFTWARE_UPDATE_APPLE_IOS,
    SOFTWARE_UPDATE_APPLE_IPADOS,
    SOFTWARE_UPDATE_APPLE_MACOS,
    SOFTWARE_UPDATE_APPLE_WATCHOS,
    SOFTWARE_UPDATE_APPLE_TVOS,
    SOFTWARE_UPDATE_APPLE_APPLE_TV,
    SOFTWARE_UPDATE_APPLE_SAFARI,
    SOFTWARE_UPDATE_APPLE_XCODE,
    SOFTWARE_UPDATE_APPLE_SECURITY,

    SOFTWARE_UPDATE_CONSOLE_PLAYSTATION_4,
    SOFTWARE_UPDATE_CONSOLE_PLAYSTATION_5,

    VIDEO_GAME_UPDATE,

    WEATHER_LOCAL_ALERT,
    ;

    public static RemoteNotificationCategory valueOfString(String input) {
        for(RemoteNotificationCategory category : RemoteNotificationCategory.values()) {
            if(category.name().equalsIgnoreCase(input)) {
                return category;
            }
        }
        return null;
    }

    public String getTitle() {
        switch (this) {
            case SOFTWARE_UPDATE_APPLE_IOS:
            case SOFTWARE_UPDATE_APPLE_IPADOS:
            case SOFTWARE_UPDATE_APPLE_MACOS:
            case SOFTWARE_UPDATE_APPLE_WATCHOS:
            case SOFTWARE_UPDATE_APPLE_TVOS:
            case SOFTWARE_UPDATE_APPLE_APPLE_TV:
            case SOFTWARE_UPDATE_APPLE_SAFARI:
            case SOFTWARE_UPDATE_APPLE_XCODE:
                return "Apple Software Update";
            case SOFTWARE_UPDATE_CONSOLE_PLAYSTATION_4:
            case SOFTWARE_UPDATE_CONSOLE_PLAYSTATION_5:
                return "PlayStation Console Update";
            case VIDEO_GAME_UPDATE:
                return "Video Game Update";
            case WEATHER_LOCAL_ALERT:
                return "Local Weather Alert";
            default:
                return "Unknown";
        }
    }

}
