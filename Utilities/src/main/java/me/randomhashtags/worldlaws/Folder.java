package me.randomhashtags.worldlaws;

import java.io.File;

public enum Folder {
    COUNTRIES("countries"),
    COUNTRIES_AVAILABILITIES("countries" + File.separator + "availabilities"),
    COUNTRIES_COUNTRIES("countries" + File.separator + "countries"),
    COUNTRIES_SUBDIVISIONS("countries" + File.separator + "subdivisions"),
    COUNTRIES_LEGALITIES("countries" + File.separator + "legalities"),
    COUNTRIES_NATIONAL("countries" + File.separator + "national"),
    COUNTRIES_RANKINGS("countries" + File.separator + "rankings"),
    COUNTRIES_RANKINGS_AGRICULTURE("countries" + File.separator + "rankings" + File.separator + "agriculture"),
    COUNTRIES_INFO("countries" + File.separator + "info"),
    COUNTRIES_INFORMATION("countries" + File.separator + "information"),
    COUNTRIES_SERVICES("countries" + File.separator + "services"),
    COUNTRIES_SERVICES_TRAVEL_BRIEFING("countries" + File.separator + "services" + File.separator + "travel briefing"),
    COUNTRIES_SERVICES_WIKIPEDIA("countries" + File.separator + "services" + File.separator + "wikipedia"),
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures"),
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures" + File.separator + "media"),
    COUNTRIES_VALUES("countries" + File.separator + "values"),

    LAWS_USA_MEMBERS("laws" + File.separator + "usa" + File.separator + "members"),
    LAWS_USA_CONGRESS("laws" + File.separator + "usa" + File.separator + "congress" + File.separator + "%version%"),

    SERVICES_FINANCE_YAHOO_TWELVE_DATA_CHARTS("services" + File.separator + "finance" + File.separator + "twelveData" + File.separator + "charts"),
    SERVICES_FINANCE_YAHOO_FINANCE_CHARTS("services" + File.separator + "finance" + File.separator + "yahooFinance" + File.separator + "charts"),

    OTHER(null),
    LOGS("logs"),

    UPCOMING_EVENTS("upcoming events" + File.separator + "%year%" + File.separator + "%day%"),
    UPCOMING_EVENTS_HOLIDAYS("upcoming events" + File.separator + "holidays" + File.separator + "%year%"),
    UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS("upcoming events" + File.separator + "holidays" + File.separator + "descriptions"),
    WEATHER_USA_ZONES("weather" + File.separator + "usa" + File.separator + "zones"),
    ;

    private final String folderName;
    private String customFolderName;

    Folder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName(boolean checkCustomFolderName) {
        return checkCustomFolderName && customFolderName != null ? customFolderName : folderName;
    }
    public void setCustomFolderName(String folderName) {
        customFolderName = folderName;
    }
    public void resetCustomFolderName() {
        customFolderName = null;
    }
}
