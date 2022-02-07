package me.randomhashtags.worldlaws.upcoming;

import java.util.HashMap;

public class UpcomingEventValueMap extends HashMap<UpcomingEventValueKey, Object> {
    public void putCategory(UpcomingEventValueCategory category) {
        put(UpcomingEventValueKey.VALUE_CATEGORY, category);
    }
    public void putKey(String key) {
        put(UpcomingEventValueKey.KEY, key);
    }
    public void putValuePrefix(String valuePrefix) {
        put(UpcomingEventValueKey.VALUE_PREFIX, valuePrefix);
    }
    public void putValueString(String valueString) {
        put(UpcomingEventValueKey.VALUE_STRING, valueString);
    }
    public void putValueType(UpcomingEventValueType type) {
        put(UpcomingEventValueKey.VALUE_TYPE, type);
    }
    public void putCellType(UpcomingEventValueCellType cellType) {
        put(UpcomingEventValueKey.CELL_TYPE, cellType);
    }
    public void putContainsSpoiler(boolean containsSpoiler) {
        put(UpcomingEventValueKey.CONTAINS_SPOILER, containsSpoiler);
    }
    public void putValueTypeUnit(UpcomingEventValueTypeUnit unit) {
        put(UpcomingEventValueKey.VALUE_UNIT, unit);
    }
    public void putCellHeight(float cellHeight) {
        put(UpcomingEventValueKey.CELL_HEIGHT, cellHeight);
    }
}