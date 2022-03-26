package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {

    COUNTRIES,
    COUNTRY_INFORMATION,
    SUBDIVISION_INFORMATION,
    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,
    PRESENTATIONS,
    UPCOMING_EVENT_TYPES,
    UPDATE_NOTES,
    VIDEO_GAMES,

    // Server Side
    AVAILABILITIES,
    ;

    public boolean isClientSide() {
        switch (this) {
            case AVAILABILITIES:
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
                return 12;
            case COUNTRY_INFORMATION:
                return 1;
            case SUBDIVISION_INFORMATION:
                return 1;

            case MOVIE_PRODUCTION_COMPANIES:
                return 3;
            case MUSIC_ARTISTS:
                return 1;
            case PRESENTATIONS:
                return 2;
            case UPCOMING_EVENT_TYPES:
                return 6;
            case VIDEO_GAMES:
                return 1;

            case AVAILABILITIES:
                return 1;

            case UPDATE_NOTES:
                return 1;

            default:
                return 0;
        }
    }
}
