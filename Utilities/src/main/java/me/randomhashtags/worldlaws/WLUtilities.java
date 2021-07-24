package me.randomhashtags.worldlaws;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.TimeUnit;

public abstract class WLUtilities {
    public static final long PROXY_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    public static final long UPCOMING_EVENTS_UPDATE_INTERVAL = TimeUnit.HOURS.toMillis(1);
    public static final long WEATHER_ALERTS_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    public static final long WEATHER_EARTHQUAKES_UPDATE_INTERVAL = TimeUnit.HOURS.toMillis(1);

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
    public static int getTodayYear() {
        return LocalDate.now().getYear();
    }

    public static LocalDate getNowUTC() {
        return LocalDate.now(Clock.systemUTC());
    }
}
