package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.settings.ResponseVersions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public enum UpcomingEventType {
    ASTRONOMY_PICTURE_OF_THE_DAY,
    JOKE_OF_THE_DAY,
    MOVIE,
    MUSIC_ALBUM,

    SPACE_EVENT,
    //SPACE_X,
    SPACE_NEAR_EARTH_OBJECT,
    SPACE_ROCKET_LAUNCH,
    SPORT_CHAMPIONSHIPS,
    SPORT_MLB,
    SPORT_NFL,
    SPORT_UFC,
    SPORT_PROFESSIONAL_WRESTLING,

    TICKETMASTER_MUSIC_EVENT,
    TV_SHOW,
    VIDEO_GAME,
    WORD_OF_THE_DAY,
    ;

    public static String getTypesJSON() {
        final JSONObject json = new JSONObject();
        json.put("version", ResponseVersions.UPCOMING_EVENT_TYPES.getValue());

        final JSONObject typesJSON = new JSONObject(), categoriesJSON = new JSONObject();
        for(UpcomingEventType type : UpcomingEventType.values()) {
            final String imageURLPrefix = type.getImageURLPrefix();
            final JSONObject typeJSON = new JSONObject();

            final String name = "name";
            final String notificationDescription = "notificationDescription";

            final JSONObject singular = new JSONObject();
            singular.put(name, type.getName(true));
            singular.put(notificationDescription, type.getNotificationDescription(true));
            typeJSON.put("singular", singular);

            final JSONObject plural = new JSONObject();
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
                final JSONArray valuesJSON = new JSONArray();
                final HashSet<UpcomingEventValueCategory> categories = new HashSet<>();
                for(UpcomingEventValue value : values) {
                    categories.add(value.getCategory());
                    valuesJSON.put(value.toJSONObject());
                }
                typeJSON.put("values", valuesJSON);

                for(UpcomingEventValueCategory category : categories) {
                    categoriesJSON.put(category.name(), category.toJSONObject());
                }
            }
            typesJSON.put(type.getID(), typeJSON);
        }
        json.put("types", typesJSON);
        json.put("categories", categoriesJSON);
        return json.toString();
    }

    private String getID() {
        return name().toLowerCase();
    }

    private int getPriority(boolean widget) {
        if(widget) {
            switch (this) {
                case WORD_OF_THE_DAY:
                    return 1;
                case TICKETMASTER_MUSIC_EVENT:
                    return 3;
                case TV_SHOW:
                    return 4;
                default:
                    return 2;
            }
        } else {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY:
                case WORD_OF_THE_DAY:
                    return 1;
                case TICKETMASTER_MUSIC_EVENT:
                    return 3;
                case TV_SHOW:
                    return 4;
                default:
                    return 2;
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
                case MOVIE: return "Movie Release";
                case MUSIC_ALBUM: return "Music Album";
                case JOKE_OF_THE_DAY: return "Joke of the Day";

                case SPACE_EVENT: return "Space Event";
                case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Object";
                case SPACE_ROCKET_LAUNCH: return "Rocket Launch";
                case SPORT_CHAMPIONSHIPS: return "Championship";
                case SPORT_MLB: return "MLB Event";
                case SPORT_NFL: return "NFL Event";
                case SPORT_UFC: return "UFC Event";
                case SPORT_PROFESSIONAL_WRESTLING: return "Professional Wrestling";

                case TICKETMASTER_MUSIC_EVENT: return "Music Event";
                case TV_SHOW: return "TV Show";
                case VIDEO_GAME: return "Video Game Release";
                case WORD_OF_THE_DAY: return "Word of the Day";
                default: return "Unknown Singular Name";
            }
        } else {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "Astronomy Picture of the Day";
                case MOVIE: return "Movie Releases";
                case MUSIC_ALBUM: return "Music Album Releases";
                case JOKE_OF_THE_DAY: return "Joke of the Day";

                case SPACE_EVENT: return "Space Events";
                case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Objects";
                case SPACE_ROCKET_LAUNCH: return "Rocket Launches";
                case SPORT_CHAMPIONSHIPS: return "Championships";
                case SPORT_MLB: return "MLB Schedule";
                case SPORT_NFL: return "NFL Schedule";
                case SPORT_UFC: return "UFC Schedule";
                case SPORT_PROFESSIONAL_WRESTLING: return "Professional Wrestling";

                case TICKETMASTER_MUSIC_EVENT: return "Music Events";
                case TV_SHOW: return "TV Shows";
                case VIDEO_GAME: return "Video Game Releases";
                case WORD_OF_THE_DAY: return "Word of the Day";
                default: return "Unknown Plural Name";
            }
        }
    }
    private String getNotificationDescription(boolean singular) {
        if(singular) {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "\"%title%\" is today's APOD!";
                case MOVIE: return "\"%title%\" is making its cinematic debut today!";
                case MUSIC_ALBUM: return "\"%title%\" by %artist% releases today!";
                case JOKE_OF_THE_DAY: return "Joke of the day: %title%";

                case SPACE_EVENT: return "%title% is scheduled today!";
                case SPACE_NEAR_EARTH_OBJECT: return "A near earth object is close by!";
                case SPACE_ROCKET_LAUNCH: return "%title% is scheduled to take off today!";
                case SPORT_CHAMPIONSHIPS: return "%title% begins today!";
                case SPORT_MLB: return "%team% has a game today!";
                case SPORT_NFL: return "%team% has a game today!";
                case SPORT_UFC: return "%title% is tonight!";
                case SPORT_PROFESSIONAL_WRESTLING: return "%title% is tonight!";

                case TICKETMASTER_MUSIC_EVENT: return "\"%title%\" happens tonight!";
                case TV_SHOW: return "A new episode for \"%title%\" is now available!";
                case VIDEO_GAME: return "\"%title%\" releases today!";
                case WORD_OF_THE_DAY: return "Today's Word of the Day is \"%title%\"!";
                default: return "Unknown Singular Notification Description!";
            }
        } else {
            switch (this) {
                case ASTRONOMY_PICTURE_OF_THE_DAY: return "\"%title%\" are today's APODs!";
                case MOVIE: return "\"%title%\" are making their cinematic debut today!";
                case MUSIC_ALBUM: return "\"%title%\" by %artist% releases today!";
                case JOKE_OF_THE_DAY: return "Jokes of the Day: %title%";

                case SPACE_EVENT: return "%title% are scheduled today!";
                case SPACE_NEAR_EARTH_OBJECT: return "Near earth objects are close by!";
                case SPACE_ROCKET_LAUNCH: return "%title% are scheduled to take off today!";
                case SPORT_CHAMPIONSHIPS: return "%title% begins today!";
                case SPORT_MLB: return "%team% have games today!";
                case SPORT_NFL: return "%team% have games today!";
                case SPORT_UFC: return "%title% are tonight!";
                case SPORT_PROFESSIONAL_WRESTLING: return "%title% are tonight!";

                case TICKETMASTER_MUSIC_EVENT: return "\"%title%\" happens tonight!";
                case TV_SHOW: return "New episodes for \"%title%\" are now available!";
                case VIDEO_GAME: return "\"%title%\" release today!";
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
                        UpcomingEventValue.APOD_COPYRIGHT
                );

            case JOKE_OF_THE_DAY:
                return collectValues(
                        UpcomingEventValue.JOTD_COPYRIGHT,
                        UpcomingEventValue.JOTD_QUESTION,
                        UpcomingEventValue.JOTD_ANSWER
                );

            case MOVIE:
                return collectValues(
                        UpcomingEventValue.MUSIC_ALBUM_ARTIST,
                        UpcomingEventValue.MUSIC_ALBUM_DETAILS_SPOTIFY,
                        UpcomingEventValue.MUSIC_ALBUM_DETAILS_ITUNES
                );

            case MUSIC_ALBUM:
                return collectValues(
                        UpcomingEventValue.MUSIC_ALBUM_DETAILS_SPOTIFY,
                        UpcomingEventValue.MUSIC_ALBUM_DETAILS_ITUNES,
                        UpcomingEventValue.MUSIC_ALBUM_ARTIST
                );

            case SPACE_EVENT:
                return collectValues(
                        UpcomingEventValue.SPACE_EVENT_NEWS_URL,
                        UpcomingEventValue.SPACE_EVENT_VIDEO_URL
                );

            case SPORT_MLB:
                return collectValues(
                        UpcomingEventValue.MLB_TEAM_AWAY,
                        UpcomingEventValue.MLB_TEAM_HOME
                );

            case SPACE_NEAR_EARTH_OBJECT:
                return collectValues(
                        UpcomingEventValue.NEO_CLOSE_APPROACH_EPOCH,
                        UpcomingEventValue.NEO_POTENTIALLY_HAZARDOUS,
                        UpcomingEventValue.NEO_ESTIMATED_DIAMETER_MAX,
                        UpcomingEventValue.NEO_ESTIMATED_DIAMETER_MIN,
                        UpcomingEventValue.NEO_RELATIVE_VELOCITY
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
                        UpcomingEventValue.WOTD_PRONUNCIATION_URL
                );

            default:
                return null;
        }
    }
}
