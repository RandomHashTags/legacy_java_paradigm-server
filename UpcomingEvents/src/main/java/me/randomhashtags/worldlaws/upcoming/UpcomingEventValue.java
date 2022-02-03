package me.randomhashtags.worldlaws.upcoming;

public enum UpcomingEventValue {
    APOD_COPYRIGHT(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS, "copyright"),

    JOTD_COPYRIGHT(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS, "copyright"),
    JOTD_QUESTION(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS, "question"),
    JOTD_ANSWER(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS, "answer"),

    MLB_TEAM_AWAY(UpcomingEventValueCategory.MLB_DETAILS, "awayTeam"),
    MLB_TEAM_HOME(UpcomingEventValueCategory.MLB_DETAILS, "homeTeam"),

    MOVIE_RELEASE_INFO(UpcomingEventValueCategory.MOVIE_DETAILS, "releaseInfo"),
    MOVIE_RATINGS(UpcomingEventValueCategory.MOVIE_DETAILS, "ratings"),
    MOVIE_IMDB_INFO(UpcomingEventValueCategory.MOVIE_DETAILS, "imdbInfo", UpcomingEventValueType.JSON),
    MOVIE_PRODUCTION_COMPANIES(UpcomingEventValueCategory.MOVIE_PRODUCTION_COMPANIES, "productionCompanies", UpcomingEventValueType.PRODUCTION_COMPANIES, false, UpcomingEventValueCellType.PRODUCTION_COMPANIES),

    MUSIC_ALBUM_ARTIST(UpcomingEventValueCategory.MUSIC_ALBUM_DETAILS, "artist"),
    MUSIC_ALBUM_DETAILS_SPOTIFY(UpcomingEventValueCategory.MUSIC_ALBUM_DETAILS, "spotifyDetails", UpcomingEventValueType.DETAILS_SPOTIFY),
    MUSIC_ALBUM_DETAILS_ITUNES(UpcomingEventValueCategory.MUSIC_ALBUM_DETAILS, "itunesDetails", UpcomingEventValueType.DETAILS_ITUNES),

    NEO_CLOSE_APPROACH_EPOCH(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS, "closeApproachEpoch", UpcomingEventValueType.INT64),
    NEO_POTENTIALLY_HAZARDOUS(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS, "potentiallyHazardous", UpcomingEventValueType.BOOLEAN),
    NEO_ESTIMATED_DIAMETER_MAX(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS, "estimatedDiameterMax", UpcomingEventValueType.FLOAT),
    NEO_ESTIMATED_DIAMETER_MIN(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS, "estimatedDiameterMin", UpcomingEventValueType.FLOAT),
    NEO_RELATIVE_VELOCITY(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS, "relativeVelocity"),

    WRESTLING_MAIN_EVENT(UpcomingEventValueCategory.WRESTLING_DETAILS, "mainEvent"),
    WRESTLING_NOTES(UpcomingEventValueCategory.WRESTLING_DETAILS, "notes"),

    ROCKET_LAUNCH_MISSION_NAME(UpcomingEventValueCategory.ROCKET_DETAILS, "missionName", UpcomingEventValueCellType.LABEL_HEADER),
    ROCKET_LAUNCH_MISSION_TYPE(UpcomingEventValueCategory.ROCKET_DETAILS, "missionType", "Type: "),
    ROCKET_LAUNCH_WINDOW_START(UpcomingEventValueCategory.ROCKET_DETAILS, "windowStart"),
    ROCKET_LAUNCH_WINDOW_END(UpcomingEventValueCategory.ROCKET_DETAILS, "windowEnd"),
    ROCKET_LAUNCH_EXACT_DAY(UpcomingEventValueCategory.ROCKET_DETAILS, "exactDay", UpcomingEventValueType.BOOLEAN),
    ROCKET_LAUNCH_EXACT_TIME(UpcomingEventValueCategory.ROCKET_DETAILS, "exactTime", UpcomingEventValueType.BOOLEAN),
    ROCKET_LAUNCH_STATUS(UpcomingEventValueCategory.ROCKET_DETAILS, "status", "Status: ", "%status% (%probability%% probability of happening)"),
    ROCKET_LAUNCH_PROBABILITY(UpcomingEventValueCategory.ROCKET_DETAILS, "probability", UpcomingEventValueType.INT),

    SPACE_EVENT_NEWS_URL(UpcomingEventValueCategory.SPACE_EVENT_DETAILS, "newsURL"),
    SPACE_EVENT_VIDEO_URL(UpcomingEventValueCategory.SPACE_EVENT_DETAILS, "videoURL"),

    TICKETMASTER_MUSIC_SEAT_MAP_URL(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS, "seatMapURL"),
    TICKETMASTER_MUSIC_TICKET_LIMIT(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS, "ticketLimit"),
    TICKETMASTER_MUSIC_PRICE_RANGE(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS, "priceRange", UpcomingEventValueType.CURRENCY),
    TICKETMASTER_MUSIC_PRICE_RANGE_STRING(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS, "priceRangeString", "Ticket Price Range: $", "%priceRangeMin% - $%priceRangeMax% (%priceRangeCurrency%)"),
    TICKETMASTER_MUSIC_VENUES(UpcomingEventValueCategory.TICKETMASTER_VENUES, "venues", UpcomingEventValueType.TICKETMASTER_VENUES),

    /*TV_SHOW_POPULARITY("popularity", UpcomingEventValueType.INT),
    TV_SHOW_LANGUAGE("language"),
    TV_SHOW_COUNTRY_CODE("countryCode"),
    TV_SHOW_OFFICIAL_SITE("officialSite"),
    TV_SHOW_NETWORK("network"),
    TV_SHOW_RUNTIME_MINUTES("runtimeMinutes", UpcomingEventValueType.INT),
    TV_SHOW_SEASON("season", UpcomingEventValueType.INT),
    TV_SHOW_EPISODE("episode", UpcomingEventValueType.INT),
    TV_SHOW_EPISODE_NAME("episodeName"),
    TV_SHOW_EPISODE_SUMMARY("episodeSummary"),
    TV_SHOW_GENRES("genres", UpcomingEventValueType.ARRAY_STRING),*/

    VIDEO_GAME_PLATFORMS(UpcomingEventValueCategory.VIDEO_GAME_DETAILS, "platforms"),
    ;

    private final UpcomingEventValueCategory category;
    private final String key, valuePrefix, valueString;
    private final UpcomingEventValueType type;
    private final boolean containsSpoiler;
    private final UpcomingEventValueCellType cellType;

    UpcomingEventValue(UpcomingEventValueCategory category, String key) {
        this(category, key, (String) null);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, String valuePrefix) {
        this(category, key, valuePrefix, UpcomingEventValueCellType.LABEL);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, String valuePrefix, String valueString) {
        this(category, key, valuePrefix, UpcomingEventValueCellType.LABEL, valueString);
    }

    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueCellType cellType) {
        this(category, key, UpcomingEventValueType.STRING, cellType);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueType type, UpcomingEventValueCellType cellType) {
        this(category, key, type, false, cellType);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, String valuePrefix, UpcomingEventValueCellType cellType) {
        this(category, key, valuePrefix, cellType, null);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, String valuePrefix, UpcomingEventValueCellType cellType, String valueString) {
        this(category, key, UpcomingEventValueType.STRING, false, cellType, valuePrefix, valueString);
    }

    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueType type) {
        this(category, key, type, false);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueType type, boolean containsSpoiler) {
        this(category, key, type, containsSpoiler, UpcomingEventValueCellType.LABEL);
    }

    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueType type, boolean containsSpoiler, UpcomingEventValueCellType cellType) {
        this(category, key, type, containsSpoiler, cellType, null);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueType type, boolean containsSpoiler, UpcomingEventValueCellType cellType, String valuePrefix) {
        this(category, key, type, containsSpoiler, cellType, valuePrefix, null);
    }
    UpcomingEventValue(UpcomingEventValueCategory category, String key, UpcomingEventValueType type, boolean containsSpoiler, UpcomingEventValueCellType cellType, String valuePrefix, String valueString) {
        this.category = category;
        this.key = key;
        this.type = type;
        this.containsSpoiler = containsSpoiler;
        this.cellType = cellType;
        this.valuePrefix = valuePrefix;
        this.valueString = valueString;
    }

    public String getKey() {
        return key;
    }
    public UpcomingEventValueType getType() {
        return type;
    }
}
