package me.randomhashtags.worldlaws.upcoming;

public enum UpcomingEventValueType {
    ARRAY_STRING,
    BOOLEAN,
    INT,
    INT64,
    FLOAT,
    JSON,
    STRING,

    CURRENCY,
    CURRENCY_ID,
    DETAILS_SPOTIFY,
    DETAILS_ITUNES,
    EVENT_SOURCE,
    IMAGE,
    PRODUCTION_COMPANIES,
    TICKETMASTER_VENUES,
    TIMESTAMP,
    ;

    public static final UpcomingEventValueType DEFAULT = UpcomingEventValueType.STRING;
}
