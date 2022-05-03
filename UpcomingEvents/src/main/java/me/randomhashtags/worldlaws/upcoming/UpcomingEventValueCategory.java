package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public enum UpcomingEventValueCategory {
    ASTRONOMY_PICTURE_OF_THE_DAY_DETAILS,
    JOKE_OF_THE_DAY_DETAILS,
    MLB_DETAILS,
    MOVIE_DETAILS(UpcomingEventValueCategoryPosition.PRE_CONTENT),
    MOVIE_PRODUCTION_COMPANIES(UpcomingEventValueCategoryPosition.POST_DESCRIPTION, "Production Companies"),
    MUSIC_ALBUM_DETAILS,
    NEAR_EARTH_OBJECT_DETAILS,
    ROCKET_DETAILS("Mission"),
    SCIENCE_YEAR_REVIEW_DETAILS("%year%"),
    SPACE_EVENT_DETAILS,
    SPOTIFY_NEW_MUSIC_FRIDAY_TRACKS("Songs"),
    TICKETMASTER_MUSIC_EVENT_DETAILS,
    TICKETMASTER_VENUES("Venues"),
    VIDEO_GAME_DETAILS(UpcomingEventValueCategoryPosition.PRE_CONTENT, "Releasing for platforms"),
    VIDEO_GAME_GENRES(UpcomingEventValueCategoryPosition.PRE_CONTENT, "Genres"),
    WORD_OF_THE_DAY_DETAILS(UpcomingEventValueCategoryPosition.ABOVE_DESCRIPTION),
    WRESTLING_DETAILS("Main Event"),
    ;

    private final String header;
    private final UpcomingEventValueCategoryPosition position;

    UpcomingEventValueCategory() {
        this(UpcomingEventValueCategoryPosition.POST_VIDEOS);
    }
    UpcomingEventValueCategory(UpcomingEventValueCategoryPosition position) {
        this(position, null);
    }
    UpcomingEventValueCategory(String header) {
        this(UpcomingEventValueCategoryPosition.POST_VIDEOS, header);
    }
    UpcomingEventValueCategory(UpcomingEventValueCategoryPosition position, String header) {
        this.position = position;
        this.header = header;
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        if(header != null) {
            json.put("header", LocalServer.fixEscapeValues(header));
        }
        json.put("position", position.name());
        return json;
    }
}
