package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {

    COUNTRIES,
    COUNTRY_CURRENCIES,
    COUNTRY_FILTERS,
    COUNTRY_INFORMATION,
    SUBDIVISION_INFORMATION,
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
                return 16;
            case COUNTRY_CURRENCIES:
                return 1;
            case COUNTRY_FILTERS:
                return 1;
            case COUNTRY_INFORMATION:
                return 5;
            case SUBDIVISION_INFORMATION:
                return 3;

            case MOVIE_PRODUCTION_COMPANIES:
                return 3;
            case MUSIC_ARTISTS:
                return 1;
            case PRESENTATIONS:
                return 3;
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
