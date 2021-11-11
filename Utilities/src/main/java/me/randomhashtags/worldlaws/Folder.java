package me.randomhashtags.worldlaws;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public enum Folder {
    COUNTRIES,
    COUNTRIES_AVAILABILITIES,
    COUNTRIES_HISTORY,
    COUNTRIES_LEGALITIES,
    COUNTRIES_NATIONAL,
    COUNTRIES_RANKINGS,
    COUNTRIES_RANKINGS_AGRICULTURE,
    COUNTRIES_INFO,
    COUNTRIES_INFORMATION,
    COUNTRIES_SERVICES,
    COUNTRIES_SERVICES_AVAILABILITIES,
    COUNTRIES_SERVICES_TRAVEL_BRIEFING("countries" + File.separator + "services" + File.separator + "travel briefing"),
    COUNTRIES_SERVICES_WIKIPEDIA,
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures"),
    COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA("countries" + File.separator + "services" + File.separator + "wikipedia" + File.separator + "featured pictures" + File.separator + "media"),
    COUNTRIES_SUBDIVISIONS,
    COUNTRIES_SUBDIVISIONS_WIKIPEDIA_PAGES("countries" + File.separator + "subdivisions" + File.separator + "wikipediaPages"), // TODO: split into respective country
    COUNTRIES_SUBDIVISIONS_CITIES,
    COUNTRIES_SUBDIVISIONS_INFORMATION("countries" + File.separator + "subdivisions" + File.separator + "information" + File.separator + "%country%"),
    COUNTRIES_SUBDIVISIONS_SERVICES_WIKIPEDIA,
    COUNTRIES_VALUES,
    COUNTRIES_WIKIPEDIA_PAGES("countries" + File.separator + "wikipediaPages"),

    LAWS_USA_MEMBERS,
    LAWS_USA_CONGRESS("laws" + File.separator + "unitedstates" + File.separator + "congress" + File.separator + "%version%"),
    LAWS_COUNTRY_SUBDIVISIONS_SUBDIVISION_YEAR("laws" + File.separator + "%country%" + File.separator + "subdivisions" + File.separator + "%subdivision%" + File.separator + "%year%"),

    SERVICES_FINANCE_TWELVE_DATA_CHARTS("services" + File.separator + "finance" + File.separator + "twelveData" + File.separator + "charts"),
    SERVICES_FINANCE_YAHOO_FINANCE_CHARTS("services" + File.separator + "finance" + File.separator + "yahooFinance" + File.separator + "charts"),

    ERRORS("errors" + File.separator + "%errorName%"),
    FEEDBACK_BUG_REPORTS("feedback" + File.separator + "bug reports"),
    FEEDBACK_FEATURE_REQUEST("feedback" + File.separator + "feature request"),
    LOGS,
    OTHER(null),

    UPCOMING_EVENTS("upcoming events"),
    UPCOMING_EVENTS_YEAR_MONTH_DAY("upcoming events" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%"),
    UPCOMING_EVENTS_IDS("upcoming events" + File.separator + "%year%" + File.separator + "ids" + File.separator + "%month%" + File.separator + "%day%"),
    UPCOMING_EVENTS_HOLIDAYS("upcoming events" + File.separator + "holidays" + File.separator + "%year%"),
    UPCOMING_EVENTS_HOLIDAYS_COUNTRIES("upcoming events" + File.separator + "holidays" + File.separator + "%year%" + File.separator + "countries"),
    UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS("upcoming events" + File.separator + "holidays" + File.separator + "descriptions"),
    UPCOMING_EVENTS_MOVIES("upcoming events" + File.separator + "movies"),
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
        final String currentFolder = Jsonable.USER_DIR;
        String folderName = getFolderName(id);
        if(folderName == null) {
            folderName = this.folderName;
        }
        switch (this) {
            case FEEDBACK_BUG_REPORTS:
            case FEEDBACK_FEATURE_REQUEST:
            case LOGS:
            case ERRORS:
                return currentFolder + "_" + folderName;
            default:
                return currentFolder + "_downloaded_pages" + File.separator + folderName;
        }
    }
}
