package me.randomhashtags.worldlaws.upcoming;

public enum UpcomingEventValueType {
    ARRAY_STRING,
    BOOLEAN,
    INT,
    INT64,
    FLOAT,
    JSON,
    STRING,
    PLACEHOLDER,

    CURRENCY,
    CURRENCY_ID,
    DETAILS_SPOTIFY,
    DETAILS_ITUNES,
    IMAGE,
    IMAGE_COPYRIGHT,
    IMDB_INFO,
    MLB_TEAM,
    PRODUCTION_COMPANIES,
    SPOTIFY_TRACKS,
    TICKETMASTER_VENUES,
    TIMESTAMP,
    VIDEO_URL,
    ;

    public static final UpcomingEventValueType DEFAULT = UpcomingEventValueType.STRING;
}
