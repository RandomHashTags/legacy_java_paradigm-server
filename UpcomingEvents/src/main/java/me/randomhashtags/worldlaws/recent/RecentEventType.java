package me.randomhashtags.worldlaws.recent;

public enum RecentEventType {
    SOFTWARE_UPDATES,
    VIDEO_GAME_UPDATES,
    ;

    public String getName() {
        switch (this) {
            case SOFTWARE_UPDATES: return "Software Updates";
            case VIDEO_GAME_UPDATES: return "Video Game Updates";
            default: return "Unknown";
        }
    }
}
