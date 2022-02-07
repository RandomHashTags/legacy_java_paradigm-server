package me.randomhashtags.worldlaws.upcoming;

public final class UpcomingEventValueKey {
    public static final UpcomingEventValueKey KEY = get("key");
    public static final UpcomingEventValueKey CONTAINS_SPOILER = get("containsSpoiler");

    public static final UpcomingEventValueKey VALUE_CATEGORY = get("valueCategory");
    public static final UpcomingEventValueKey VALUE_TYPE = get("valueType");
    public static final UpcomingEventValueKey VALUE_UNIT = get("valueUnit");
    public static final UpcomingEventValueKey VALUE_PREFIX = get("valuePrefix");
    public static final UpcomingEventValueKey VALUE_STRING = get("valueString");

    public static final UpcomingEventValueKey CELL_TYPE = get("cellType");
    public static final UpcomingEventValueKey CELL_HEIGHT = get("cellHeight");
    ;

    private static UpcomingEventValueKey get(String key) {
        return new UpcomingEventValueKey(key);
    }

    private final String key;

    private UpcomingEventValueKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
}
