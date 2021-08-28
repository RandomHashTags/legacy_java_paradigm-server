package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.TimeUnit;

public abstract class WLUtilities {
    public static final long LAWS_HOME_RESPONSE_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(30);
    public static final long PROXY_HOME_RESPONSE_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    public static final long UPCOMING_EVENTS_UPDATE_INTERVAL = TimeUnit.HOURS.toMillis(1);
    public static final long UPCOMING_EVENTS_TV_SHOW_UPDATE_INTERVAL = TimeUnit.DAYS.toMillis(1);
    public static final long WEATHER_ALERTS_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    public static final long WEATHER_EARTHQUAKES_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(30);
    public static final long WEATHER_NASA_WEATHER_EVENT_TRACKER_UPDATE_INTERVAL = TimeUnit.HOURS.toMillis(1);

    public static Month valueOfMonthFromInput(String input) {
        input = input.toLowerCase().substring(0, 3);
        switch (input) {
            case "jan": return Month.JANUARY;
            case "feb": return Month.FEBRUARY;
            case "mar": return Month.MARCH;
            case "apr": return Month.APRIL;
            case "may": return Month.MAY;
            case "jun": return Month.JUNE;
            case "jul": return Month.JULY;
            case "aug": return Month.AUGUST;
            case "sep": return Month.SEPTEMBER;
            case "oct": return Month.OCTOBER;
            case "nov": return Month.NOVEMBER;
            case "dec": return Month.DECEMBER;
            default: return null;
        }
    }
    public static LocalDate getNow() { // TODO: convert all LocalDate.now requests to this method - and convert it to use current UTC system time
        return LocalDate.now();
    }
    public static int getTodayYear() {
        return getNow().getYear();
    }

    public static LocalDate getNowUTC() {
        return LocalDate.now(Clock.systemUTC());
    }

    public static void saveException(Exception exception) {
        final StringBuilder builder = new StringBuilder(exception.getLocalizedMessage());
        for(StackTraceElement element : exception.getStackTrace()) {
            builder.append("\n").append(element.toString());
        }
        final String errorName = exception.getClass().getSimpleName();
        final Folder folder = Folder.LOGS_ERRORS;
        final String fileName = Long.toString(System.currentTimeMillis());
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%errorName%", errorName));
        Jsonable.saveFile("WLUtilities.saveException", Level.ERROR, folder, fileName, builder.toString(), "txt");
        folder.removeCustomFolderName(fileName);
    }
}
