package me.randomhashtags.worldlaws;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public enum Folder {
    COUNTRIES,
    COUNTRIES_AVAILABILITIES,
    COUNTRIES_LEGALITIES,
    COUNTRIES_NATIONAL,
    COUNTRIES_RANKINGS,
    COUNTRIES_RANKINGS_AGRICULTURE,
    COUNTRIES_INFO,
    COUNTRIES_INFORMATION,
    COUNTRIES_INFORMATION_SUBDIVISIONS("countries" + File.separator + "information" + File.separator + "%country%"),
    COUNTRIES_INFORMATION_CITIES("countries" + File.separator + "information" + File.separator + "%country%" + File.separator + "%subdivision%"),
    COUNTRIES_SERVICES,
    COUNTRIES_SERVICES_TRAVEL_BRIEFING("countries" + File.separator + "services" + File.separator + "travel briefing"),
    COUNTRIES_VALUES,
    COUNTRIES_WIKIPEDIA,
    COUNTRIES_WIKIPEDIA_SUBDIVISIONS("countries" + File.separator + "wikipedia" + File.separator + "%country%"),
    COUNTRIES_WIKIPEDIA_FEATURED_PICTURES("countries" + File.separator + "wikipedia" + File.separator + "featured pictures"),

    LAWS_COUNTRY_MEMBERS("laws" + File.separator + "%country%" + File.separator + "members"),
    LAWS_COUNTRY_CONGRESS("laws" + File.separator + "%country%" + File.separator + "congress" + File.separator + "%version%"),
    LAWS_COUNTRY_SUBDIVISIONS_SUBDIVISION_YEAR("laws" + File.separator + "%country%" + File.separator + "subdivisions" + File.separator + "%subdivision%" + File.separator + "%year%"),

    SERVICES_FINANCE_TWELVE_DATA_CHARTS("services" + File.separator + "finance" + File.separator + "twelveData" + File.separator + "charts"),
    SERVICES_FINANCE_YAHOO_FINANCE_CHARTS("services" + File.separator + "finance" + File.separator + "yahooFinance" + File.separator + "charts"),

    DIRECTORY(""),
    DEVICE_TOKENS("deviceTokens" + File.separator + "%type%" + File.separator + "%category%"),
    ERRORS("errors" + File.separator + "%errorName%"),
    FEEDBACK_BUG_REPORTS("feedback" + File.separator + "bug reports"),
    FEEDBACK_FEATURE_REQUEST("feedback" + File.separator + "feature request"),
    LOGS("logs" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%" + File.separator + "%type%" + File.separator + "%server%"),
    OTHER(null),
    REMOTE_NOTIFICATIONS("remoteNotifications" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%"),
    UPDATES("_updates"),
    UPDATES_FILES("_updates" + File.separator + "files"),

    UPCOMING_EVENTS("upcoming events"),
    UPCOMING_EVENTS_IDS("upcoming events" + File.separator + "%year%" + File.separator + "%month%" + File.separator + "%day%" + File.separator + "%type%"),
    UPCOMING_EVENTS_HOLIDAYS_TYPE("upcoming events" + File.separator + "holidays" + File.separator + "%type%"),
    UPCOMING_EVENTS_MOVIES("upcoming events" + File.separator + "movies"),
    WEATHER_COUNTRY_ZONES("weather" + File.separator + "%country%" + File.separator + "zones" + File.separator + "%type%" + File.separator + "%subdivision%"),
    WEATHER_COUNTRY_OFFICES("weather" + File.separator + "%country%" + File.separator + "offices"),
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
        if(id != null && folderName != null) {
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
        final String currentFolder = Jsonable.CURRENT_FOLDER;
        final String folderName = getCurrentTargetFolderName(id);
        return currentFolder + folderName;
    }

    public String getFullFolderPath(String id) {
        final String currentFolder = Jsonable.CURRENT_FOLDER;
        return currentFolder + getFolderPath(id);
    }
    public String getFolderPath(String id) {
        final String folderName = getCurrentTargetFolderName(id);
        switch (this) {
            case DIRECTORY:
            case UPDATES:
            case UPDATES_FILES:
                return folderName;
            case DEVICE_TOKENS:
            case FEEDBACK_BUG_REPORTS:
            case FEEDBACK_FEATURE_REQUEST:
            case LOGS:
            case REMOTE_NOTIFICATIONS:
            case ERRORS:
                return "_" + folderName;
            default:
                return "_downloaded_pages" + File.separator + folderName;
        }
    }

    public File literalFileExists(String fileName) {
        return literalFileExists(null, fileName);
    }
    public File literalFileExists(String id, String fullFileName) {
        final String currentFolderName = id != null ? getCurrentFolderName(id) + File.separator : "";
        final String fullPath = currentFolderName + fullFileName;
        final Path path = Paths.get(fullPath);
        return Files.exists(path) ? path.toFile() : null;
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
        return paths;
    }
}
