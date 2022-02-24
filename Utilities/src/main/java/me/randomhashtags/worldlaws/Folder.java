package me.randomhashtags.worldlaws;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public enum Folder {
    COUNTRIES,
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
    COUNTRIES_VALUES,
    COUNTRIES_WIKIPEDIA_PAGES("countries" + File.separator + "wikipediaPages"),

    LAWS_USA_MEMBERS,
    LAWS_USA_CONGRESS("laws" + File.separator + "unitedstates" + File.separator + "congress" + File.separator + "%version%"),
    LAWS_COUNTRY_SUBDIVISIONS_SUBDIVISION_YEAR("laws" + File.separator + "%country%" + File.separator + "subdivisions" + File.separator + "%subdivision%" + File.separator + "%year%"),

    SERVICES_FINANCE_TWELVE_DATA_CHARTS("services" + File.separator + "finance" + File.separator + "twelveData" + File.separator + "charts"),
    SERVICES_FINANCE_YAHOO_FINANCE_CHARTS("services" + File.separator + "finance" + File.separator + "yahooFinance" + File.separator + "charts"),

    DIRECTORY(""),
    DEVICE_TOKENS("device tokens"),
    ERRORS("errors" + File.separator + "%errorName%"),
    FEEDBACK_BUG_REPORTS("feedback" + File.separator + "bug reports"),
    FEEDBACK_FEATURE_REQUEST("feedback" + File.separator + "feature request"),
    LOGS("logs" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%" + File.separator + "%type%" + File.separator + "%server%"),
    OTHER(null),
    REMOTE_NOTIFICATIONS("remoteNotifications" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%"),
    UPDATES("_updates"),
    UPDATES_FILES("_updates" + File.separator + "files"),

    UPCOMING_EVENTS("upcoming events"),
    UPCOMING_EVENTS_IDS("upcoming events" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%" + File.separator + "ids" + File.separator + "%type%"),
    UPCOMING_EVENTS_HOLIDAYS("upcoming events" + File.separator + "holidays" + File.separator + "%year%"),
    UPCOMING_EVENTS_HOLIDAYS_COUNTRIES("upcoming events" + File.separator + "holidays" + File.separator + "%year%" + File.separator + "countries"),
    UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS("upcoming events" + File.separator + "holidays" + File.separator + "descriptions" + File.separator + "%type%"),
    UPCOMING_EVENTS_MOVIES("upcoming events" + File.separator + "movies"),
    UPCOMING_EVENTS_TV_SHOWS("upcoming events" + File.separator + "tv shows"),
    WEATHER_USA_ZONES,
    WEATHER_USA_OFFICES,
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
        return id != null ? ids.get(id) : null;
    }
    public void setCustomFolderName(String id, String folderName) {
        if(folderName != null) {
            ids.put(id, folderName);
        }
    }
    public void removeCustomFolderName(String id) {
        if(id != null) {
            ids.remove(id);
        }
    }

    private String getCurrentTargetFolderName(String id) {
        String folderName = getFolderName(id);
        if(folderName == null) {
            folderName = this.folderName;
        }
        return folderName;
    }
    private String getCurrentFolderName(String id) {
        final String currentFolder = Jsonable.USER_DIR;
        final String folderName = getCurrentTargetFolderName(id);
        return currentFolder + folderName;
    }

    public String getFullFolderPath(String id) {
        final String currentFolder = Jsonable.USER_DIR;
        final String folderName = getCurrentTargetFolderName(id);
        switch (this) {
            case DIRECTORY:
            case UPDATES:
            case UPDATES_FILES:
                return currentFolder + folderName;
            case DEVICE_TOKENS:
            case FEEDBACK_BUG_REPORTS:
            case FEEDBACK_FEATURE_REQUEST:
            case LOGS:
            case REMOTE_NOTIFICATIONS:
            case ERRORS:
                return currentFolder + "_" + folderName;
            default:
                return currentFolder + "_downloaded_pages" + File.separator + folderName;
        }
    }

    public File literalFileExists(String fileName) {
        return literalFileExists(null, fileName);
    }
    public File literalFileExists(String id, String fullFileName) {
        fullFileName = getCurrentFolderName(id) + File.separator + fullFileName;
        final File file = new File(fullFileName);
        return file.exists() ? file : null;
    }

    public HashSet<Path> getAllFilePaths(String id) {
        final String folderName = getCurrentFolderName(id);
        final File folderFile = new File(folderName);
        final File[] files = folderFile.exists() && folderFile.isDirectory() ? folderFile.listFiles() : null;
        removeCustomFolderName(id);

        final HashSet<Path> paths = new HashSet<>();
        if(files != null) {
            for(File file : files) {
                paths.add(file.toPath());
            }
        }
        return paths.isEmpty() ? null : paths;
    }
}
