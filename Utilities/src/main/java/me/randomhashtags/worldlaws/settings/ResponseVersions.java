package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {

    COUNTRIES,
    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,
    UPCOMING_EVENT_TYPES,
    UPDATE_NOTES,

    // Server Side
    AVAILABILITIES,
    COUNTRY_INFORMATION,
    HOLIDAYS,

    ;

    public boolean isClientSide() {
        switch (this) {
            case AVAILABILITIES:
            case COUNTRY_INFORMATION:
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
                return 11;
            case MOVIE_PRODUCTION_COMPANIES:
                return 2;
            case MUSIC_ARTISTS:
                return 1;
            case UPCOMING_EVENT_TYPES:
                return 3;

            case AVAILABILITIES:
                return 1;
            case COUNTRY_INFORMATION:
                return 1;
            case HOLIDAYS:
                return 3;

            case UPDATE_NOTES:
                return 1;

            default:
                return 0;
        }
    }
}
