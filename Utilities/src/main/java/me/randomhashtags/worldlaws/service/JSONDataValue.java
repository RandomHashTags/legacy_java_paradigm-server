package me.randomhashtags.worldlaws.service;

public enum JSONDataValue {
    MOVIE_DATABASE,
    QUOTAS,
    SPOTIFY,
    ;

    public String getIdentifier() {
        switch (this) {
            case MOVIE_DATABASE: return "movie_database";
            case QUOTAS: return "quotas";
            case SPOTIFY: return "spotify";
            default: return "null";
        }
    }
    public int getMaxQuotaRequestsPerDay() {
        switch (this) {
            case MOVIE_DATABASE: return 1000;
            case SPOTIFY: return 1000;
            default: return 0;
        }
    }
}
