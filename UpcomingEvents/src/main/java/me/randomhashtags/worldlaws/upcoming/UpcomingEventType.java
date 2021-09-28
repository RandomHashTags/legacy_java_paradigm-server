package me.randomhashtags.worldlaws.upcoming;

import org.json.JSONObject;

public enum UpcomingEventType {
    MOVIE,
    MUSIC_ALBUM,

    SPACE_EVENT,
    //SPACE_X,
    SPACE_NEAR_EARTH_OBJECT,
    SPACE_ROCKET_LAUNCH,

    SPORT_CHAMPIONSHIPS,
    SPORT_MLB,
    SPORT_UFC,

    TV_SHOW,
    VIDEO_GAME,
    ;

    public static String getTypesJSON() {
        final JSONObject json = new JSONObject();
        for(UpcomingEventType type : UpcomingEventType.values()) {
            final JSONObject typeJSON = new JSONObject();
            typeJSON.put("singularName", type.getSingularName());
            typeJSON.put("pluralName", type.getPluralName());
            typeJSON.put("singularNotificationDescription", type.getSingularNotificationDescription());
            json.put(type.getID(), typeJSON);
        }
        return json.toString();
    }

    public String getID() {
        return name().toLowerCase();
    }
    public String getSingularName() {
        switch (this) {
            case MOVIE: return "Movie Release";
            case MUSIC_ALBUM: return "Music Album";

            case SPACE_EVENT: return "Space Event";
            case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Object";
            case SPACE_ROCKET_LAUNCH: return "Rocket Launch";

            case SPORT_CHAMPIONSHIPS: return "Championship";
            case SPORT_MLB: return "MLB Event";
            case SPORT_UFC: return "UFC Event";

            case TV_SHOW: return "TV Show";
            case VIDEO_GAME: return "Video Game Release";
            default: return "Unknown Singular Name";
        }
    }
    public String getPluralName() {
        switch (this) {
            case MOVIE: return "Movie Releases";
            case MUSIC_ALBUM: return "Music Album Releases";

            case SPACE_EVENT: return "Space Events";
            case SPACE_NEAR_EARTH_OBJECT: return "Near Earth Objects";
            case SPACE_ROCKET_LAUNCH: return "Rocket Launches";

            case SPORT_CHAMPIONSHIPS: return "Championships";
            case SPORT_MLB: return "MLB Schedule";
            case SPORT_UFC: return "UFC Schedule";

            case TV_SHOW: return "TV Shows";
            case VIDEO_GAME: return "Video Game Releases";
            default: return "Unknown Plural Name";
        }
    }
    public String getSingularNotificationDescription() {
        final String suffix = " Open for details.";
        switch (this) {
            case MOVIE: return "%title% is making its cinematic debut today!" + suffix;
            case MUSIC_ALBUM: return "%title% by %artist% releases today!" + suffix;

            case SPACE_EVENT: return "%title% is scheduled for today!" + suffix;
            case SPACE_NEAR_EARTH_OBJECT: return "A near earth object is close by!" + suffix;
            case SPACE_ROCKET_LAUNCH: return "%title% is scheduled to take off today!" + suffix;

            case SPORT_CHAMPIONSHIPS: return "%title% begins today!" + suffix;
            case SPORT_MLB: return "%team% has a game today!" + suffix;
            case SPORT_UFC: return "%title% is tonight!" + suffix;

            case TV_SHOW: return "%title% has new episodes available!" + suffix;
            case VIDEO_GAME: return "%title% releases today!" + suffix;
            default: return "Unknown Notification Description!";
        }
    }
}
