package me.randomhashtags.worldlaws.settings;

public enum CustomResponseVersion {

    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,

    ;

    public String getKey() {
        return name().toLowerCase();
    }
    public int getValue() {
        switch (this) {
            case MOVIE_PRODUCTION_COMPANIES:
                return 1;
            case MUSIC_ARTISTS:
                return 1;
            default:
                return 0;
        }
    }
}
