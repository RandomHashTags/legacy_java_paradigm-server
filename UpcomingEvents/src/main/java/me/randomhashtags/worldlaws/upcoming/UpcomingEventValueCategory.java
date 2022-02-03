package me.randomhashtags.worldlaws.upcoming;

public enum UpcomingEventValueCategory {
    JOKE_OF_THE_DAY_DETAILS,
    MLB_DETAILS,
    MOVIE_DETAILS(UpcomingEventValueCategoryPosition.PRE_CONTENT),
    MOVIE_PRODUCTION_COMPANIES(UpcomingEventValueCategoryPosition.POST_DESCRIPTION, "Production Companies"),
    MUSIC_ALBUM_DETAILS,
    NEAR_EARTH_OBJECT_DETAILS,
    ROCKET_DETAILS("Mission"),
    SPACE_EVENT_DETAILS,
    TICKETMASTER_MUSIC_EVENT_DETAILS,
    TICKETMASTER_VENUES("Venues"),
    VIDEO_GAME_DETAILS(UpcomingEventValueCategoryPosition.PRE_CONTENT, "Releasing for platforms"),
    WRESTLING_DETAILS,
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

    public String getHeader() {
        return header;
    }
    public UpcomingEventValueCategoryPosition getPosition() {
        return position;
    }
}
