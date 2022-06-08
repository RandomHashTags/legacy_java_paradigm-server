package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {
    SERVER_VERSION,

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
            case SERVER_VERSION:
                return 1;

            case COUNTRIES:
                return 19;
            case COUNTRY_CURRENCIES:
                return 1;
            case COUNTRY_FILTERS:
                return 2;
            case COUNTRY_INFORMATION:
                return 7;
            case SUBDIVISION_INFORMATION:
                return 5;

            case LAWS_RECENT_ACTIVITY:
                return 1;

            case MOVIE_PRODUCTION_COMPANIES:
                return 3;
            case MUSIC_ARTISTS:
                return 1;
            case PRESENTATIONS:
                return 4;
            case REMOTE_NOTIFICATIONS:
                return 4;
            case UPCOMING_EVENT_TYPES:
                return 14;
            case VIDEO_GAMES:
                return 1;

            case AVAILABILITIES:
                return 1;
            case HOLIDAYS:
                return 1;

            default:
                return 0;
        }
    }
}
