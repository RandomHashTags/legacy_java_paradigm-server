package me.randomhashtags.worldlaws;

import java.io.File;

public enum FileType {
    COUNTRIES("countries"),
    COUNTRIES_AVAILABILITIES("countries" + File.separator + "availabilities"),
    COUNTRIES_SUBDIVISIONS("countries" + File.separator + "subdivisions"),
    COUNTRIES_LEGALITIES("countries" + File.separator + "legalities"),
    COUNTRIES_NATIONAL("countries" + File.separator + "national"),
    COUNTRIES_RANKINGS("countries" + File.separator + "rankings"),
    COUNTRIES_RANKINGS_AGRICULTURE("countries" + File.separator + "rankings" + File.separator + "agriculture"),
    COUNTRIES_INFO("countries" + File.separator + "info"),
    COUNTRIES_SERVICES("countries" + File.separator + "services"),
    COUNTRIES_SERVICES_CIA("countries" + File.separator + "services" + File.separator + "cia"),
    COUNTRIES_SERVICES_TRAVEL_BRIEFING("countries" + File.separator + "services" + File.separator + "travel briefing"),
    COUNTRIES_SERVICES_WIKIPEDIA("countries" + File.separator + "services" + File.separator + "wikipedia"),
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures"),
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures" + File.separator + "media"),
    COUNTRIES_VALUES("countries" + File.separator + "values"),

    OTHER(null),

    PEOPLE_POLITICIANS_UNITED_STATES("people" + File.separator + "politicians" + File.separator + "unitedstates"),

    UPCOMING_EVENTS_HOLIDAYS("upcoming events" + File.separator + "holidays"),
    ;

    private final String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
