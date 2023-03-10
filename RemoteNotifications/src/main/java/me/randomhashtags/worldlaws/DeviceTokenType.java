package me.randomhashtags.worldlaws;

public enum DeviceTokenType {
    APPLE,
    GOOGLE,
    ;

    public static DeviceTokenType valueOfString(String input) {
        for(DeviceTokenType type : DeviceTokenType.values()) {
            if(type.getID().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
    public static RemoteNotificationDeviceTokenController valueOfStringGetController(String input) {
        final DeviceTokenType tokenType = valueOfString(input);
        return tokenType != null ? tokenType.getController() : null;
    }

    public String getID() {
        return name().toLowerCase();
    }

    public RemoteNotificationDeviceTokenController getController() {
        switch (this) {
            case APPLE: return AppleNotifications.INSTANCE;
            default: return null;
        }
    }
}
