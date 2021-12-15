package me.randomhashtags.worldlaws.settings;

public enum ResponseVersions {

    COUNTRIES,
    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,

    ;

    public String getKey() {
        return name().toLowerCase();
    }
    public int getValue() {
        switch (this) {
            case COUNTRIES:
                return 6;
            case MOVIE_PRODUCTION_COMPANIES:
                return 1;
            case MUSIC_ARTISTS:
                return 1;
            default:
                return 0;
        }
    }
}