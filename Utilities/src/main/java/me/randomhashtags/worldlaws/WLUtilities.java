package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.location.CustomCountry;
import org.json.JSONObject;

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
    public static void getCustomCountry(String countryBackendID, CompletionHandler handler) {
        TargetServer.COUNTRIES.sendResponse(RequestMethod.POST, countryBackendID, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject json = new JSONObject(object.toString());
                final CustomCountry country = new CustomCountry(json);
                handler.handleCustomCountry(country);
            }
        });
    }

    public static int getTodayYear() {
        return LocalDate.now().getYear();
    }
}
