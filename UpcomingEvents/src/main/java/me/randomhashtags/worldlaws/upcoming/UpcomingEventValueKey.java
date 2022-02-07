package me.randomhashtags.worldlaws.upcoming;

public enum UpcomingEventValueKey {
    VALUE_CATEGORY,
    VALUE_TYPE,
    VALUE_UNIT,

    VALUE_PREFIX,
    VALUE_STRING,

    KEY,
    CELL_TYPE,
    CELL_HEIGHT,
    CONTAINS_SPOILER,
    ;

    public String getKey() {
        switch (this) {
            case VALUE_CATEGORY: return "category";
            case VALUE_TYPE: return "type";
            case KEY: return "key";
            case CELL_TYPE: return "cellType";
            case CELL_HEIGHT: return "cellHeight";
            case CONTAINS_SPOILER: return "containsSpoiler";
            case VALUE_PREFIX: return "valuePrefix";
            case VALUE_STRING: return "valueString";
            default: return null;
        }
    }
}
