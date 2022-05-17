package me.randomhashtags.worldlaws.upcoming;

import org.json.JSONObject;

public enum UpcomingEventValue {
    ASTRONOMY_PICTURE_OF_THE_DAY_COPYRIGHT(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ASTRONOMY_PICTURE_OF_THE_DAY_DETAILS);
        putKey("copyright");
        putValueType(UpcomingEventValueType.IMAGE_COPYRIGHT);
        putValuePrefix("Copyright: ");
    }}),
    ASTRONOMY_PICTURE_OF_THE_DAY_VIDEO_URL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ASTRONOMY_PICTURE_OF_THE_DAY_DETAILS);
        putKey("videoURL");
        putValueType(UpcomingEventValueType.VIDEO_URL);
    }}),

    JOKE_OF_THE_DAY_COPYRIGHT(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS);
        putKey("copyright");
    }}),
    JOKE_OF_THE_DAY_QUESTION(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS);
        putKey("question");
    }}),
    JOKE_OF_THE_DAY_ANSWER(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.JOKE_OF_THE_DAY_DETAILS);
        putKey("answer");
        putContainsSpoiler(true);
    }}),

    MLB_TEAM_AWAY(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MLB_DETAILS);
        putKey("teamAway");
        putValueType(UpcomingEventValueType.MLB_TEAM);
    }}),
    MLB_TEAM_HOME(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MLB_DETAILS);
        putKey("teamHome");
        putValueType(UpcomingEventValueType.MLB_TEAM);
    }}),

    MOVIE_RELEASE_INFO(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MOVIE_DETAILS);
        putKey("releaseInfo");
    }}),
    MOVIE_RATINGS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MOVIE_DETAILS);
        putKey("ratings");
    }}),
    MOVIE_IMDB_INFO(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MOVIE_DETAILS);
        putKey("imdbInfo");
        putValueType(UpcomingEventValueType.IMDB_INFO);
    }}),
    MOVIE_PRODUCTION_COMPANIES(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MOVIE_PRODUCTION_COMPANIES);
        putKey("productionCompanies");
        putValueType(UpcomingEventValueType.PRODUCTION_COMPANIES);
        putCellType(UpcomingEventValueCellType.PRODUCTION_COMPANIES);
    }}),

    MUSIC_ALBUM_ARTIST(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MUSIC_ALBUM_DETAILS);
        putKey("artist");
    }}),
    MUSIC_ALBUM_DETAILS_SPOTIFY(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MUSIC_ALBUM_DETAILS);
        putKey("spotifyDetails");
        putValueType(UpcomingEventValueType.DETAILS_SPOTIFY);
    }}),
    MUSIC_ALBUM_DETAILS_ITUNES(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.MUSIC_ALBUM_DETAILS);
        putKey("itunesDetails");
        putValueType(UpcomingEventValueType.DETAILS_ITUNES);
    }}),

    NEAR_EARTH_OBJECT_CLOSE_APPROACH_EPOCH(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS);
        putKey("closeApproachEpoch");
        putValueType(UpcomingEventValueType.INT64);
    }}),
    NEAR_EARTH_OBJECT_POTENTIALLY_HAZARDOUS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS);
        putKey("potentiallyHazardous");
        putValueType(UpcomingEventValueType.BOOLEAN);
    }}),
    NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MAX(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS);
        putKey("estimatedDiameterMaxMeters");
        putValueType(UpcomingEventValueType.FLOAT);
        putValueTypeUnit(UpcomingEventValueTypeUnit.METERS);
    }}),
    NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MIN(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS);
        putKey("estimatedDiameterMinMeters");
        putValueType(UpcomingEventValueType.FLOAT);
        putValueTypeUnit(UpcomingEventValueTypeUnit.METERS);
    }}),
    NEAR_EARTH_OBJECT_RELATIVE_VELOCITY(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.NEAR_EARTH_OBJECT_DETAILS);
        putKey("relativeVelocity");
        putValuePrefix("Relative Velocity: ");
    }}),

    WRESTLING_MAIN_EVENT(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.WRESTLING_DETAILS);
        putKey("mainEvent");
    }}),
    WRESTLING_NOTES(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.WRESTLING_DETAILS);
        putKey("notes");
    }}),

    ROCKET_LAUNCH_MISSION_DESCRIPTION(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("missionDescription");
    }}),
    ROCKET_LAUNCH_MISSION_NAME(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("missionName");
        putCellType(UpcomingEventValueCellType.LABEL_HEADER);
    }}),
    ROCKET_LAUNCH_MISSION_TYPE(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("missionType");
        putValuePrefix("Type: ");
    }}),
    ROCKET_LAUNCH_WINDOW_START(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("windowStart");
        putValueType(UpcomingEventValueType.TIMESTAMP);
        putValuePrefix("Window Start: ");
    }}),
    ROCKET_LAUNCH_WINDOW_END(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("windowEnd");
        putValueType(UpcomingEventValueType.TIMESTAMP);
        putValuePrefix("Window End: ");
    }}),
    ROCKET_LAUNCH_EXACT_DAY(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("exactDay");
        putValueType(UpcomingEventValueType.STRING);
        putValuePrefix("Exact Day: ");
    }}),
    ROCKET_LAUNCH_EXACT_TIME(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("exactTime");
        putValueType(UpcomingEventValueType.STRING);
        putValuePrefix("Exact Time: ");
    }}),
    ROCKET_LAUNCH_STATUS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("status");
        putValuePrefix("Status: ");
        putValueString("%status%%probability%");
    }}),
    ROCKET_LAUNCH_PROBABILITY(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.ROCKET_DETAILS);
        putKey("probability");
        putValueType(UpcomingEventValueType.PLACEHOLDER);
    }}),

    SCIENCE_YEAR_REVIEW_YEARS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SCIENCE_YEAR_REVIEW_DETAILS);
        putKey("years");
        putValueType(UpcomingEventValueType.WIKIPEDIA_EVENTS);
    }}),

    SPACE_EVENT_NEWS_URL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_EVENT_DETAILS);
        putKey("newsURL");
    }}),
    SPACE_EVENT_VIDEO_URL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_EVENT_DETAILS);
        putKey("videoURL");
        putValueType(UpcomingEventValueType.VIDEO_URL);
    }}),

    SPACE_LUNAR_ECLIPSE_TIME_GREATEST_MILLISECONDS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("timeGreatestMilliseconds");
        putValueType(UpcomingEventValueType.TIMESTAMP);
    }}),
    SPACE_LUNAR_ECLIPSE_ORBITAL_NODE(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("orbitalNode");
    }}),
    SPACE_LUNAR_ECLIPSE_SAROS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("saros");
    }}),
    SPACE_LUNAR_ECLIPSE_GAMMA(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("gamma");
    }}),
    SPACE_LUNAR_ECLIPSE_MAGNITUDE_PENUMBRA(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("magnitudePenumbra");
    }}),
    SPACE_LUNAR_ECLIPSE_MAGNITUDE_UMBRA(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("magnitudeUmbra");
    }}),
    SPACE_LUNAR_ECLIPSE_DURATION_PARTIAL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("durationPartial");
        putValueType(UpcomingEventValueType.INT);
    }}),
    SPACE_LUNAR_ECLIPSE_DURATION_TOTAL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPACE_LUNAR_ECLIPSE_DETAILS);
        putKey("durationTotal");
        putValueType(UpcomingEventValueType.INT);
    }}),

    SPOTIFY_NEW_MUSIC_FRIDAY_TRACKS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.SPOTIFY_NEW_MUSIC_FRIDAY_TRACKS);
        putKey("tracks");
        putValueType(UpcomingEventValueType.SPOTIFY_TRACKS);
        putCellType(UpcomingEventValueCellType.SPOTIFY_TRACKS);
    }}),

    TICKETMASTER_MUSIC_SEAT_MAP_URL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS);
        putKey("seatMapURL");
        putValueType(UpcomingEventValueType.IMAGE);
        putCellHeight(250);
    }}),
    TICKETMASTER_MUSIC_TICKET_LIMIT(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS);
        putKey("ticketLimit");
    }}),
    TICKETMASTER_MUSIC_PRICE_RANGE_CURRENCY(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS);
        putKey("priceRangeCurrency");
        putValueType(UpcomingEventValueType.CURRENCY_ID);
    }}),
    TICKETMASTER_MUSIC_PRICE_RANGE_MAX(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS);
        putKey("priceRangeMax");
        putValueType(UpcomingEventValueType.CURRENCY);
    }}),
    TICKETMASTER_MUSIC_PRICE_RANGE_MIN(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS);
        putKey("priceRangeMin");
        putValueType(UpcomingEventValueType.CURRENCY);
    }}),
    TICKETMASTER_MUSIC_PRICE_RANGE_STRING(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_MUSIC_EVENT_DETAILS);
        putKey("priceRangeString");
        putValuePrefix("Ticket Price Range: $");
        putValueString("%priceRangeMin% - $%priceRangeMax% (%priceRangeCurrency%)");
    }}),
    TICKETMASTER_MUSIC_VENUES(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.TICKETMASTER_VENUES);
        putKey("venues");
        putValueType(UpcomingEventValueType.TICKETMASTER_VENUES);
        putCellType(UpcomingEventValueCellType.TICKETMASTER_VENUE);
        putCellHeight(500);
    }}),

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

    VIDEO_GAME_PLATFORMS(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.VIDEO_GAME_DETAILS);
        putKey("platforms");
    }}),
    VIDEO_GAME_GENRES(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.VIDEO_GAME_DETAILS);
        putKey("genres");
    }}),

    WORD_OF_THE_DAY_PRONUNCIATION_URL(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.WORD_OF_THE_DAY_DETAILS);
        putKey("pronunciationURL");
        putCellType(UpcomingEventValueCellType.AUDIO);
    }}),
    WORD_OF_THE_DAY_SYLLABLES(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.WORD_OF_THE_DAY_DETAILS);
        putKey("syllables");
        putValuePrefix("Syllables: ");
    }}),
    WORD_OF_THE_DAY_TYPE(new UpcomingEventValueMap() {{
        putCategory(UpcomingEventValueCategory.WORD_OF_THE_DAY_DETAILS);
        putKey("type");
        putValuePrefix("Type: ");
    }})
    ;
    private final UpcomingEventValueMap values;

    UpcomingEventValue(UpcomingEventValueMap values) {
        this.values = values;
    }

    public UpcomingEventValueCategory getCategory() {
        return (UpcomingEventValueCategory) values.get(UpcomingEventValueKey.VALUE_CATEGORY);
    }
    public String getKey() {
        return (String) values.get(UpcomingEventValueKey.KEY);
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        if(!values.containsKey(UpcomingEventValueKey.VALUE_TYPE)) {
            values.put(UpcomingEventValueKey.VALUE_TYPE, UpcomingEventValueType.DEFAULT);
        }
        if(!values.containsKey(UpcomingEventValueKey.CELL_TYPE)) {
            values.put(UpcomingEventValueKey.CELL_TYPE, UpcomingEventValueCellType.LABEL);
        }
        for(UpcomingEventValueKey key : values.keySet()) {
            if(key != UpcomingEventValueKey.KEY) {
                final Object value = values.get(key);
                final Object realValue = value instanceof Enum ? ((Enum<?>) value).name() : value;
                json.put(key.getKey(), realValue);
            }
        }
        return json;
    }
}