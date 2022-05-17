package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import org.json.JSONObject;

import java.util.HashSet;

public enum UpcomingEventType {
    ASTRONOMY_PICTURE_OF_THE_DAY,
    JOKE_OF_THE_DAY,
    MOVIE,
    MUSIC_ALBUM,

    PRESENTATION,

    SCIENCE_YEAR_REVIEW,
    SPACE_EVENT,
    SPACE_LUNAR_ECLIPSE,
    //SPACE_X,
    SPACE_NEAR_EARTH_OBJECT,
    SPACE_ROCKET_LAUNCH,
    SPORT_CHAMPIONSHIPS,
    SPORT_MLB,
    SPORT_NFL,
    SPORT_UFC,
    SPORT_PROFESSIONAL_WRESTLING,
    SPOTIFY_NEW_MUSIC_FRIDAY,

    TICKETMASTER_MUSIC_EVENT,
    TV_SHOW,
    VIDEO_GAME,
    WIKIPEDIA_TODAYS_FEATURED_PICTURE,
    WORD_OF_THE_DAY,
    ;

    public static JSONObjectTranslatable getTypesJSON() { // TODO: save to file
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.setTranslatedKeys("types");
        json.put("version", ResponseVersions.UPCOMING_EVENT_TYPES.getValue());

        final JSONObjectTranslatable typesJSON = new JSONObjectTranslatable();
        final JSONObject categoriesJSON = new JSONObject();
        for(UpcomingEventType type : UpcomingEventType.values()) {
            final String imageURLPrefix = type.getImageURLPrefix();
            final JSONObjectTranslatable typeJSON = new JSONObjectTranslatable("singular", "plural");

            final String name = "name", notificationDescription = "notificationDescription";
            final String[] translatableKeys = { name };

            final JSONObjectTranslatable singular = new JSONObjectTranslatable(translatableKeys);
            singular.put(name, type.getName(true));
            singular.put(notificationDescription, type.getNotificationDescription(true));
            typeJSON.put("singular", singular);

            final JSONObjectTranslatable plural = new JSONObjectTranslatable(translatableKeys);
            plural.put(name, type.getName(false));
            plural.put(notificationDescription, type.getNotificationDescription(false));
            typeJSON.put("plural", plural);

            final JSONObject priorities = new JSONObject();
            priorities.put("widget", type.getPriority(true));
            priorities.put("app", type.getPriority(false));
            typeJSON.put("priorities", priorities);

            if(imageURLPrefix != null) {
                typeJSON.put("imageURLPrefix", imageURLPrefix);
            }

            final UpcomingEventValue[] values = type.getValues();
            if(values != null) {
                final JSONObjectTranslatable valuesJSON = new JSONObjectTranslatable();
                final HashSet<UpcomingEventValueCategory> categories = new HashSet<>();
                for(UpcomingEventValue value : values) {
                    final String key = value.getKey();
                    categories.add(value.getCategory());
                    valuesJSON.put(key, value.toJSONObject());
                    valuesJSON.addTranslatedKey(key);
                }
                typeJSON.put("values", valuesJSON);

                for(UpcomingEventValueCategory category : categories) {
                    categoriesJSON.put(category.name(), category.toJSONObject());
                }
            }
            final String typeID = type.getID();
            typesJSON.put(typeID, typeJSON);
            typesJSON.addTranslatedKey(typeID);
        }
        json.put("types", typesJSON);
        json.put("categories", categoriesJSON);
        return json;
    }

    public String getID() {
        return name().toLowerCase();
    }

    private int getPriority(boolean widget) {
        if(widget) {
            switch (this) {
                case SPACE_LUNAR_ECLIPSE:
                    return 1;
                case SPOTIFY_NEW_MUSIC_FRIDAY:
                    return 2;
                case PRESENTATION:
                    return 3;

                case MOVIE:
                    return 4;
                case VIDEO_GAME:
                    return 5;
                case SPACE_EVENT:
                    return 6;
                case SPACE_NEAR_EARTH_OBJECT:
                    return 7;
                case SPACE_ROCKET_LAUNCH:
                    return 8;
                case MUSIC_ALBUM:
                    return 9;

                case SPORT_PROFESSIONAL_WRESTLING:
                    return 10;
                case SPORT_UFC:
                    return 11;

                case SPORT_MLB:
                    return 12;
                case SPORT_NFL:
                    return 13;

                case ASTRONOMY_PICTURE_OF_THE_DAY:
                    return 14;
                case SCIENCE_YEAR_REVIEW:
                    return 15;
                case WIKIPEDIA_TODAYS_FEATURED_PICTURE:
                    return 16;
                case WORD_OF_THE_DAY:
                    return 17;
                case JOKE_OF_THE_DAY:
                    return 18;

                case SPORT_CHAMPIONSHIPS:
                    return 19;
                case TICKETMASTER_MUSIC_EVENT:
                    return 20;
                case TV_SHOW:
                    return 21;

                default:
                    return 100;
            }
        } else {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY:
                    return 1;
                case WIKIPEDIA_TODAYS_FEATURED_PICTURE:
                    return 2;
                case WORD_OF_THE_DAY:
                    return 3;
                case JOKE_OF_THE_DAY:
                    return 4;


                case SPACE_LUNAR_ECLIPSE:
                    return 5;
                case SPOTIFY_NEW_MUSIC_FRIDAY:
                    return 6;
                case PRESENTATION:
                    return 7;
                case SCIENCE_YEAR_REVIEW:
                    return 8;

                case SPORT_MLB:
                    return 9;
                case SPORT_NFL:
                    return 10;
                case SPORT_PROFESSIONAL_WRESTLING:
                    return 11;
                case SPORT_UFC:
                    return 12;

                case MOVIE:
                    return 13;
                case VIDEO_GAME:
                    return 14;
                case SPACE_EVENT:
                    return 15;
                case SPACE_NEAR_EARTH_OBJECT:
                    return 16;
                case SPACE_ROCKET_LAUNCH:
                    return 17;
                case MUSIC_ALBUM:
                    return 18;

                case SPORT_CHAMPIONSHIPS:
                    return 19;
                case TICKETMASTER_MUSIC_EVENT:
                    return 20;
                case TV_SHOW:
                    return 21;

                default:
                    return 100;
            }
        }
    }

    public String getImageURLPrefix() {
        switch (this) {
            case ASTRONOMY_PICTURE_OF_THE_DAY:
                return "https://apod.nasa.gov/apod/image/";
            case JOKE_OF_THE_DAY:
                return "https://jokes.one/img/joke_of_the_day.jpg";
            case MOVIE:
                return "https://m.media-amazon.com/images/";
            case MUSIC_ALBUM:
            case SCIENCE_YEAR_REVIEW:
            case SPACE_LUNAR_ECLIPSE:
            case SPORT_CHAMPIONSHIPS:
            case SPORT_UFC:
            case SPORT_PROFESSIONAL_WRESTLING:
            case VIDEO_GAME:
                return "https://upload.wikimedia.org/wikipedia/";
            case SPACE_EVENT:
            case SPACE_ROCKET_LAUNCH:
                return "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/";
            case TICKETMASTER_MUSIC_EVENT:
                return "https://s1.ticketm.net/dam/";
            case TV_SHOW:
                return "https://static.tvmaze.com/uploads/images/original_untouched/";
            case WORD_OF_THE_DAY:
                return "https://www.trendingpod.com/wp-content/uploads/2017/12/1200px-Merriam-Webster_logo-1024x1024.png";
            default:
                return null;
        }
    }

    private String getName(boolean singular) {
        if(singular) {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "Astronomy Picture of the Day";
                case JOKE_OF_THE_DAY: return "Joke of the Day";
                case MOVIE: return "Movie Release";
                case MUSIC_ALBUM: return "Music Album";

                case PRESENTATION: return "Presentation";

                case SCIENCE_YEAR_REVIEW: return "Today in Science";
                case SPACE_EVENT: return "Space Event";
                case SPACE_LUNAR_ECLIPSE: return "Lunar Eclipse";
                case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Object";
                case SPACE_ROCKET_LAUNCH: return "Rocket Launch";
                case SPORT_CHAMPIONSHIPS: return "Championship";
                case SPORT_MLB: return "MLB Event";
                case SPORT_NFL: return "NFL Event";
                case SPORT_UFC: return "UFC Event";
                case SPORT_PROFESSIONAL_WRESTLING: return "Professional Wrestling";
                case SPOTIFY_NEW_MUSIC_FRIDAY: return "Spotify New Music Friday";

                case TICKETMASTER_MUSIC_EVENT: return "Music Event";
                case TV_SHOW: return "TV Show";
                case VIDEO_GAME: return "Video Game Release";
                case WIKIPEDIA_TODAYS_FEATURED_PICTURE: return "Wikipedia: Today's Featured Picture";
                case WORD_OF_THE_DAY: return "Word of the Day";
                default: return "Unknown Singular Name";
            }
        } else {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "Astronomy Picture of the Day";
                case JOKE_OF_THE_DAY: return "Joke of the Day";
                case MOVIE: return "Movie Releases";
                case MUSIC_ALBUM: return "Music Album Releases";

                case PRESENTATION: return "Presentations";

                case SCIENCE_YEAR_REVIEW: return "Today in Science";
                case SPACE_EVENT: return "Space Events";
                case SPACE_LUNAR_ECLIPSE: return "Lunar Eclipses";
                case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Objects";
                case SPACE_ROCKET_LAUNCH: return "Rocket Launches";
                case SPORT_CHAMPIONSHIPS: return "Championships";
                case SPORT_MLB: return "MLB Schedule";
                case SPORT_NFL: return "NFL Schedule";
                case SPORT_UFC: return "UFC Schedule";
                case SPORT_PROFESSIONAL_WRESTLING: return "Professional Wrestling";
                case SPOTIFY_NEW_MUSIC_FRIDAY: return "Spotify New Music Friday";

                case TICKETMASTER_MUSIC_EVENT: return "Music Events";
                case TV_SHOW: return "TV Shows";
                case VIDEO_GAME: return "Video Game Releases";
                case WIKIPEDIA_TODAYS_FEATURED_PICTURE: return "Wikipedia: Today's Featured Picture";
                case WORD_OF_THE_DAY: return "Word of the Day";
                default: return "Unknown Plural Name";
            }
        }
    }
    private String getNotificationDescription(boolean singular) {
        if(singular) {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "\"%title%\" is today's APOD!";
                case JOKE_OF_THE_DAY: return "Joke of the day: %title%";
                case MOVIE: return "\"%title%\" is making its cinematic debut today!";
                case MUSIC_ALBUM: return "\"%title%\" by %artist% releases today!";

                case PRESENTATION: return "%title% is happening today!";

                case SCIENCE_YEAR_REVIEW: return "See what happened today in the past with Today in Science!";
                case SPACE_EVENT: return "%title% is scheduled today!";
                case SPACE_LUNAR_ECLIPSE: return "%title% is happening today!";
                case SPACE_NEAR_EARTH_OBJECT: return "A near earth object is close by!";
                case SPACE_ROCKET_LAUNCH: return "%title% is scheduled to take off today!";
                case SPORT_CHAMPIONSHIPS: return "%title% begins today!";
                case SPORT_MLB: return "%team% has a game today!";
                case SPORT_NFL: return "%team% has a game today!";
                case SPORT_UFC: return "%title% is tonight!";
                case SPORT_PROFESSIONAL_WRESTLING: return "%title% is tonight!";
                case SPOTIFY_NEW_MUSIC_FRIDAY: return "Spotify's New Music Friday has been updated!";

                case TICKETMASTER_MUSIC_EVENT: return "\"%title%\" happens tonight!";
                case TV_SHOW: return "A new episode for \"%title%\" is now available!";
                case VIDEO_GAME: return "\"%title%\" releases today!";
                case WIKIPEDIA_TODAYS_FEATURED_PICTURE: return "Today's Wikipedia Featured Picture is \"%title%\"!";
                case WORD_OF_THE_DAY: return "Today's Word of the Day is \"%title%\"!";
                default: return "Unknown Singular Notification Description!";
            }
        } else {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "\"%title%\" are today's APODs!";
                case JOKE_OF_THE_DAY: return "Jokes of the Day: %title%";
                case MOVIE: return "\"%title%\" are making their cinematic debut today!";
                case MUSIC_ALBUM: return "\"%title%\" by %artist% releases today!";

                case PRESENTATION: return "%title% are happening today!";

                case SCIENCE_YEAR_REVIEW: return "See what happened today in the past with Today in Science!";
                case SPACE_EVENT: return "%title% are scheduled today!";
                case SPACE_LUNAR_ECLIPSE: return "%title% are happening today!";
                case SPACE_NEAR_EARTH_OBJECT: return "Near earth objects are close by!";
                case SPACE_ROCKET_LAUNCH: return "%title% are scheduled to take off today!";
                case SPORT_CHAMPIONSHIPS: return "%title% begins today!";
                case SPORT_MLB: return "%team% have games today!";
                case SPORT_NFL: return "%team% have games today!";
                case SPORT_UFC: return "%title% are tonight!";
                case SPORT_PROFESSIONAL_WRESTLING: return "%title% are tonight!";
                case SPOTIFY_NEW_MUSIC_FRIDAY: return "Spotify's New Music Friday has been updated!";

                case TICKETMASTER_MUSIC_EVENT: return "\"%title%\" happens tonight!";
                case TV_SHOW: return "New episodes for \"%title%\" are now available!";
                case VIDEO_GAME: return "\"%title%\" release today!";
                case WIKIPEDIA_TODAYS_FEATURED_PICTURE: return "Today's Wikipedia Featured Pictures are \"%title%\"!";
                case WORD_OF_THE_DAY: return "Today's Words of the Day are \"%title%\"!";
                default: return "Unknown Plural Notification Description!";
            }
        }
    }

    private UpcomingEventValue[] collectValues(UpcomingEventValue...values) {
        return values;
    }
    private UpcomingEventValue[] getValues() {
        switch (this) {
            case ASTRONOMY_PICTURE_OF_THE_DAY:
                return collectValues(
                        UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_COPYRIGHT
                );

            case JOKE_OF_THE_DAY:
                return collectValues(
                        UpcomingEventValue.JOKE_OF_THE_DAY_COPYRIGHT,
                        UpcomingEventValue.JOKE_OF_THE_DAY_QUESTION,
                        UpcomingEventValue.JOKE_OF_THE_DAY_ANSWER
                );

            case MOVIE:
                return collectValues(
                        UpcomingEventValue.MOVIE_IMDB_INFO,
                        UpcomingEventValue.MOVIE_RELEASE_INFO,
                        UpcomingEventValue.MOVIE_RATINGS,
                        UpcomingEventValue.MOVIE_PRODUCTION_COMPANIES
                );

            case MUSIC_ALBUM:
                return collectValues(
                        UpcomingEventValue.MUSIC_ALBUM_DETAILS_SPOTIFY,
                        UpcomingEventValue.MUSIC_ALBUM_DETAILS_ITUNES,
                        UpcomingEventValue.MUSIC_ALBUM_ARTIST
                );

            case SCIENCE_YEAR_REVIEW:
                return collectValues(
                        UpcomingEventValue.SCIENCE_YEAR_REVIEW_YEARS
                );

            case SPACE_EVENT:
                return collectValues(
                        UpcomingEventValue.SPACE_EVENT_NEWS_URL,
                        UpcomingEventValue.SPACE_EVENT_VIDEO_URL
                );

            case SPACE_LUNAR_ECLIPSE:
                return collectValues(
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_TIME_GREATEST_MILLISECONDS,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_ORBITAL_NODE,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_SAROS,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_GAMMA,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_MAGNITUDE_PENUMBRA,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_MAGNITUDE_UMBRA,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_DURATION_PARTIAL,
                        UpcomingEventValue.SPACE_LUNAR_ECLIPSE_DURATION_TOTAL
                );

            case SPACE_NEAR_EARTH_OBJECT:
                return collectValues(
                        UpcomingEventValue.NEAR_EARTH_OBJECT_CLOSE_APPROACH_EPOCH,
                        UpcomingEventValue.NEAR_EARTH_OBJECT_POTENTIALLY_HAZARDOUS,
                        UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MAX,
                        UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MIN,
                        UpcomingEventValue.NEAR_EARTH_OBJECT_RELATIVE_VELOCITY
                );

            case SPORT_MLB:
                return collectValues(
                        UpcomingEventValue.MLB_TEAM_AWAY,
                        UpcomingEventValue.MLB_TEAM_HOME
                );

            case SPORT_PROFESSIONAL_WRESTLING:
                return collectValues(
                        UpcomingEventValue.WRESTLING_MAIN_EVENT,
                        UpcomingEventValue.WRESTLING_NOTES
                );

            case SPACE_ROCKET_LAUNCH:
                return collectValues(
                        UpcomingEventValue.ROCKET_LAUNCH_STATUS,
                        UpcomingEventValue.ROCKET_LAUNCH_MISSION_TYPE,
                        UpcomingEventValue.ROCKET_LAUNCH_WINDOW_START,
                        UpcomingEventValue.ROCKET_LAUNCH_WINDOW_END,
                        UpcomingEventValue.ROCKET_LAUNCH_EXACT_DAY,
                        UpcomingEventValue.ROCKET_LAUNCH_EXACT_TIME,
                        UpcomingEventValue.ROCKET_LAUNCH_MISSION_NAME,
                        UpcomingEventValue.ROCKET_LAUNCH_MISSION_DESCRIPTION,
                        UpcomingEventValue.ROCKET_LAUNCH_PROBABILITY
                );

            case SPOTIFY_NEW_MUSIC_FRIDAY:
                return collectValues(
                        UpcomingEventValue.SPOTIFY_NEW_MUSIC_FRIDAY_TRACKS
                );

            case TICKETMASTER_MUSIC_EVENT:
                return collectValues(
                        UpcomingEventValue.TICKETMASTER_MUSIC_SEAT_MAP_URL,
                        UpcomingEventValue.TICKETMASTER_MUSIC_TICKET_LIMIT,
                        UpcomingEventValue.TICKETMASTER_MUSIC_PRICE_RANGE_CURRENCY,
                        UpcomingEventValue.TICKETMASTER_MUSIC_PRICE_RANGE_MAX,
                        UpcomingEventValue.TICKETMASTER_MUSIC_PRICE_RANGE_MIN,
                        UpcomingEventValue.TICKETMASTER_MUSIC_PRICE_RANGE_STRING,
                        UpcomingEventValue.TICKETMASTER_MUSIC_VENUES
                );

            case TV_SHOW:
                return null;

            case VIDEO_GAME:
                return collectValues(
                        UpcomingEventValue.VIDEO_GAME_PLATFORMS
                );

            case WORD_OF_THE_DAY:
                return collectValues(
                        UpcomingEventValue.WORD_OF_THE_DAY_TYPE,
                        UpcomingEventValue.WORD_OF_THE_DAY_SYLLABLES,
                        UpcomingEventValue.WORD_OF_THE_DAY_PRONUNCIATION_URL
                );

            case PRESENTATION:
            case SPORT_CHAMPIONSHIPS:
            case WIKIPEDIA_TODAYS_FEATURED_PICTURE:
                return null;

            default:
                return null;
        }
    }
}
