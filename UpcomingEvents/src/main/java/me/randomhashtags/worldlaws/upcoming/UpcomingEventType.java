package me.randomhashtags.worldlaws.upcoming;

import org.json.JSONObject;

public enum UpcomingEventType {
    MOVIE,
    MUSIC_ALBUM,
    TICKETMASTER_MUSIC_EVENT,

    SPACE_EVENT,
    //SPACE_X,
    SPACE_NEAR_EARTH_OBJECT,
    SPACE_ROCKET_LAUNCH,

    SPORT_CHAMPIONSHIPS,
    SPORT_MLB,
    SPORT_NFL,
    SPORT_UFC,

    TV_SHOW,
    VIDEO_GAME,
    ;

    public static String getTypesJSON() {
        final JSONObject json = new JSONObject();
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

            if(imageURLPrefix != null) {
                typeJSON.put("imageURLPrefix", imageURLPrefix);
            }
            json.put(type.getID(), typeJSON);
        }
        return json.toString();
    }

    private String getID() {
        return name().toLowerCase();
    }

    public String getImageURLPrefix() {
        switch (this) {
            case MOVIE:
                return "https://m.media-amazon.com/images/";
            case MUSIC_ALBUM:
            case SPORT_CHAMPIONSHIPS:
            case SPORT_UFC:
            case VIDEO_GAME:
                return "https://upload.wikimedia.org/wikipedia/";
            case SPACE_EVENT:
            case SPACE_ROCKET_LAUNCH:
                return "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/";
            case TICKETMASTER_MUSIC_EVENT:
                return "https://s1.ticketm.net/dam/";
            case TV_SHOW:
                return "https://static.tvmaze.com/uploads/images/original_untouched/";
            default:
                return null;
        }
    }

    private String getName(boolean singular) {
        if(singular) {
            switch (this) {
                case MOVIE: return "Movie Release";
                case MUSIC_ALBUM: return "Music Album";

                case SPACE_EVENT: return "Space Event";
                case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Object";
                case SPACE_ROCKET_LAUNCH: return "Rocket Launch";

                case SPORT_CHAMPIONSHIPS: return "Championship";
                case SPORT_MLB: return "MLB Event";
                case SPORT_NFL: return "NFL Event";
                case SPORT_UFC: return "UFC Event";

                case TICKETMASTER_MUSIC_EVENT: return "Music Event";
                case TV_SHOW: return "TV Show";
                case VIDEO_GAME: return "Video Game Release";
                default: return "Unknown Singular Name";
            }
        } else {
            switch (this) {
                case MOVIE: return "Movie Releases";
                case MUSIC_ALBUM: return "Music Album Releases";

                case SPACE_EVENT: return "Space Events";
                case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Objects";
                case SPACE_ROCKET_LAUNCH: return "Rocket Launches";

                case SPORT_CHAMPIONSHIPS: return "Championships";
                case SPORT_MLB: return "MLB Schedule";
                case SPORT_NFL: return "NFL Schedule";
                case SPORT_UFC: return "UFC Schedule";

                case TICKETMASTER_MUSIC_EVENT: return "Music Events";
                case TV_SHOW: return "TV Shows";
                case VIDEO_GAME: return "Video Game Releases";
                default: return "Unknown Plural Name";
            }
        }
    }
    private String getNotificationDescription(boolean singular) {
        if(singular) {
            switch (this) {
                case MOVIE: return "\"%title%\" is making its cinematic debut today!";
                case MUSIC_ALBUM: return "\"%title%\" by %artist% releases today!";

                case SPACE_EVENT: return "%title% is scheduled today!";
                case SPACE_NEAR_EARTH_OBJECT: return "A near earth object is close by!";
                case SPACE_ROCKET_LAUNCH: return "%title% is scheduled to take off today!";

                case SPORT_CHAMPIONSHIPS: return "%title% begins today!";
                case SPORT_MLB: return "%team% has a game today!";
                case SPORT_NFL: return "%team% has a game today!";
                case SPORT_UFC: return "%title% is tonight!";

                case TICKETMASTER_MUSIC_EVENT: return "\"%title%\" happens tonight!";
                case TV_SHOW: return "A new episode for \"%title%\" is now available!";
                case VIDEO_GAME: return "\"%title%\" releases today!";
                default: return "Unknown Notification Description!";
            }
        } else {
            switch (this) {
                case MOVIE: return "\"%title%\" are making their cinematic debut today!";
                case MUSIC_ALBUM: return "\"%title%\" by %artist% releases today!";

                case SPACE_EVENT: return "%title% are scheduled today!";
                case SPACE_NEAR_EARTH_OBJECT: return "Near earth objects are close by!";
                case SPACE_ROCKET_LAUNCH: return "%title% are scheduled to take off today!";

                case SPORT_CHAMPIONSHIPS: return "%title% begins today!";
                case SPORT_MLB: return "%team% have games today!";
                case SPORT_NFL: return "%team% have games today!";
                case SPORT_UFC: return "%title% are tonight!";

                case TICKETMASTER_MUSIC_EVENT: return "\"%title%\" happens tonight!";
                case TV_SHOW: return "New episodes for \"%title%\" are now available!";
                case VIDEO_GAME: return "\"%title%\" release today!";
                default: return "Unknown Notification Description!";
            }
        }
    }
}
