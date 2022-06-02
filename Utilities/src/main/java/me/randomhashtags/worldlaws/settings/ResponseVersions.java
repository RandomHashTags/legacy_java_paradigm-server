package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {

    COUNTRIES,
    COUNTRY_CURRENCIES,
    COUNTRY_FILTERS,
    COUNTRY_INFORMATION,
    SUBDIVISION_INFORMATION,
    LAWS_RECENT_ACTIVITY,
    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,
    PRESENTATIONS,
    REMOTE_NOTIFICATIONS,
    UPCOMING_EVENT_TYPES,
    UPDATE_NOTES,
    VIDEO_GAMES,

    // Server Side
    AVAILABILITIES,
    HOLIDAYS,
    ;

    public boolean isClientSide() {
        switch (this) {
            case AVAILABILITIES:
            case HOLIDAYS:
                return false;
            default:
                return true;
        }
    }
    public String getKey() {
        return name().toLowerCase();
    }
    public int getValue() {
        switch (this) {
            case COUNTRIES:
                return 17;
            case COUNTRY_CURRENCIES:
                return 1;
            case COUNTRY_FILTERS:
                return 1;
            case COUNTRY_INFORMATION:
                return 6;
            case SUBDIVISION_INFORMATION:
                return 4;

            case LAWS_RECENT_ACTIVITY:
                return 1;

            case MOVIE_PRODUCTION_COMPANIES:
                return 3;
            case MUSIC_ARTISTS:
                return 1;
            case PRESENTATIONS:
                return 4;
            case REMOTE_NOTIFICATIONS:
                return 3;
            case UPCOMING_EVENT_TYPES:
                return 14;
            case VIDEO_GAMES:
                return 1;

            case AVAILABILITIES:
                return 1;
            case HOLIDAYS:
                return 1;

            case UPDATE_NOTES:
                return 1;

            default:
                return 0;
        }
    }
}
