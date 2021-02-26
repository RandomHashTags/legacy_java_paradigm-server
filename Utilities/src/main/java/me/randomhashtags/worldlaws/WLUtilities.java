package me.randomhashtags.worldlaws;

import java.time.LocalDate;
import java.time.Month;

public final class WLUtilities {
    public static Month getMonthFromPrefix(String prefix) {
        prefix = prefix.substring(0, 3);
        switch (prefix.toLowerCase()) {
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
}
