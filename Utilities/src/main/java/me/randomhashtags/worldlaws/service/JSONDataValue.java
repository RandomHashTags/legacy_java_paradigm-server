package me.randomhashtags.worldlaws.service;

public enum JSONDataValue {
    MOVIE_DATABASE,
    QUOTAS,
    SPOTIFY,
    FINANCE_YAHOO_FINANCE,
    FINANCE_TWELVE_DATA,

    TV_SHOWS,
    ;

    public String getIdentifier() {
        switch (this) {
            case MOVIE_DATABASE: return "movie_database";
            case QUOTAS: return "quotas";
            case SPOTIFY: return "spotify";
            case FINANCE_YAHOO_FINANCE: return "yahoo_finance";
            case FINANCE_TWELVE_DATA: return "twelve_data";
            case TV_SHOWS: return "tv_shows";
            default: return "null";
        }
    }
    public int getMaxQuotaRequestsPerDay() {
        switch (this) {
            case MOVIE_DATABASE: return 1000;
            case SPOTIFY: return 1000;
            case FINANCE_YAHOO_FINANCE: return 16;
            case FINANCE_TWELVE_DATA: return 800;
            default: return 0;
        }
    }
}
