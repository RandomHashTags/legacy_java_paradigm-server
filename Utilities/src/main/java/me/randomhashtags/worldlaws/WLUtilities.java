package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.location.CustomCountry;
import me.randomhashtags.worldlaws.location.Territory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
    public static void getCustomCountryCollection(CompletionHandler handler) {
        TargetServer.COUNTRIES.sendResponse(RequestMethod.POST, "collection", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONArray array = new JSONArray(object.toString());
                final Collection<CustomCountry> collection = new ArrayList<>();
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final CustomCountry country = new CustomCountry(json);
                    collection.add(country);
                }
                handler.handleCollection(collection);
            }
        });
    }
    public static void getCountryTerritories(String countryBackendID, CompletionHandler handler) {
        TargetServer.COUNTRIES.sendResponse(RequestMethod.POST, countryBackendID + "/territories", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONArray array = new JSONArray(object.toString());
                final HashSet<Territory> set = new HashSet<>();
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final Territory territory = Territory.fromJSON(json);
                    set.add(territory);
                }
                handler.handleCountryTerritories(set);
            }
        });
    }
}
