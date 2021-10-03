package me.randomhashtags.worldlaws;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public enum Folder {
    AVAILABILITIES("availabilities"),

    COUNTRIES,
    COUNTRIES_AVAILABILITIES,
    COUNTRIES_COUNTRIES,
    COUNTRIES_SUBDIVISIONS,
    COUNTRIES_HISTORY,
    COUNTRIES_LEGALITIES,
    COUNTRIES_NATIONAL,
    COUNTRIES_RANKINGS,
    COUNTRIES_RANKINGS_AGRICULTURE,
    COUNTRIES_INFO,
    COUNTRIES_INFORMATION,
    COUNTRIES_SERVICES,
    COUNTRIES_SERVICES_TRAVEL_BRIEFING("countries" + File.separator + "services" + File.separator + "travel briefing"),
    COUNTRIES_SERVICES_WIKIPEDIA,
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures"),
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures" + File.separator + "media"),

    COUNTRIES_VALUES,

    LAWS_USA_MEMBERS,
    LAWS_USA_CONGRESS("laws" + File.separator + "usa" + File.separator + "congress" + File.separator + "%version%"),

    SERVICES_FINANCE_YAHOO_TWELVE_DATA_CHARTS("services" + File.separator + "finance" + File.separator + "twelveData" + File.separator + "charts"),
    SERVICES_FINANCE_YAHOO_FINANCE_CHARTS("services" + File.separator + "finance" + File.separator + "yahooFinance" + File.separator + "charts"),

    SUBDIVISIONS,
    SUBDIVISIONS_SUBDIVISIONS, // TODO: split into respective country
    SUBDIVISIONS_CITIES,
    SUBDIVISIONS_INFORMATION("subdivisions" + File.separator + "information" + File.separator + "%country%"),
    SUBDIVISIONS_SERVICES_WIKIPEDIA,

    OTHER(null),
    LOGS,
    LOGS_ERRORS("logs" + File.separator + "errors" + File.separator + "%errorName%"),

    UPCOMING_EVENTS("upcoming events"),
    UPCOMING_EVENTS_YEAR_DAY("upcoming events" + File.separator + "%year%" + File.separator + "%day%"),
    UPCOMING_EVENTS_IDS("upcoming events" + File.separator + "%year%" + File.separator + "ids" + File.separator + "%month%" + File.separator + "%day%"),
    UPCOMING_EVENTS_HOLIDAYS("upcoming events" + File.separator + "holidays" + File.separator + "%year%"),
    UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS("upcoming events" + File.separator + "holidays" + File.separator + "descriptions"),
    UPCOMING_EVENTS_TV_SHOWS("upcoming events" + File.separator + "tv shows"),
    WEATHER_USA_ZONES,
    ;

    private final String folderName;
    private final ConcurrentHashMap<String, String> ids;

    Folder() {
        this.folderName = name().toLowerCase().replace("_", File.separator);
        ids = new ConcurrentHashMap<>();
    }
    Folder(String folderName) {
        this.folderName = folderName;
        ids = new ConcurrentHashMap<>();
    }

    public String getFolderName() {
        return folderName;
    }
    public String getFolderName(String id) {
        return ids.get(id);
    }
    public void setCustomFolderName(String id, String folderName) {
        ids.put(id, folderName);
    }
    public void removeCustomFolderName(String id) {
        ids.remove(id);
    }

    public String getFolderPath(String id) {
        final String folderName = getFolderName(id);
        return Jsonable.USER_DIR + "downloaded_pages" + File.separator + (folderName != null ? folderName :this.folderName);
    }
}
