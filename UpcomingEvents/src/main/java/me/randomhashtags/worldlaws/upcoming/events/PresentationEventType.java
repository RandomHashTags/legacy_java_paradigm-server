package me.randomhashtags.worldlaws.upcoming.events;

public enum PresentationEventType {
    AWARD_CEREMONY,
    CONFERENCE,
    CONFERENCE_DEVELOPER,
    CONVENTION_GAMING,
    EXHIBIT_FASHION,
    EXPO_GAMING,
    EVENT_GATHERING,
    FESTIVAL_GAMING,
    FESTIVAL_MUSIC,
    PRESENTATION,
    SUMMIT,
    TOURNAMENT_GAMING,
    ;

    public String getName() {
        switch (this) {
            case AWARD_CEREMONY: return "Award Ceremony";
            case CONFERENCE: return "Conference";
            case CONFERENCE_DEVELOPER: return "Developer Conference";
            case CONVENTION_GAMING: return "Gaming Convention";
            case EXHIBIT_FASHION: return "Fashion Exhibit";
            case EXPO_GAMING: return "Gaming Expo";
            case EVENT_GATHERING: return "Gathering Event";
            case FESTIVAL_GAMING: return "Gaming Festival";
            case FESTIVAL_MUSIC: return "Music Festival";
            case SUMMIT: return "Summit";
            case TOURNAMENT_GAMING: return "Gaming Tournament";
            default: return "Presentation";
        }
    }
}
