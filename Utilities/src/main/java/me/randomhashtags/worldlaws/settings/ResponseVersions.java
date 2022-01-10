package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {

    COUNTRIES,
    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,
    UPCOMING_EVENT_TYPES,

    ;

    public String getKey() {
        return name().toLowerCase();
    }
    public int getValue() {
        switch (this) {
            case COUNTRIES:
                return 9;
            case MOVIE_PRODUCTION_COMPANIES:
                return 2;
            case MUSIC_ARTISTS:
                return 1;
            case UPCOMING_EVENT_TYPES:
                return 1;
            default:
                return 0;
        }
    }
}
