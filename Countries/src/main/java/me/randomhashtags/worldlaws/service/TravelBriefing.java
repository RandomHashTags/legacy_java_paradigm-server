package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public enum TravelBriefing implements CountryService {
    INSTANCE;

    private HashMap<String, String> urls, countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_TRAVEL_BRIEFING;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(urls == null) {
            load(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    getBriefingJSON(countryBackendID, handler);
                }
            });
        } else {
            getBriefingJSON(countryBackendID, handler);
        }
    }

    private void load(CompletionHandler handler) {
        urls = new HashMap<>();
        countries = new HashMap<>();

        final long started = System.currentTimeMillis();
        requestJSON("https://travelbriefing.org/countries.json", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONArray array = new JSONArray(object.toString());
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String country = json.getString("name").toLowerCase().replace(" ", ""), url = json.getString("url");
                    urls.put(country, url);
                }
                WLLogger.log(Level.INFO, "TravelBriefing - loaded urls (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handle(null);
            }
        });
    }

    private void getBriefingJSON(String countryBackendID, CompletionHandler handler) {
        if(countries.containsKey(countryBackendID)) {
            handler.handle(countries.get(countryBackendID));
        } else if(urls.containsKey(countryBackendID)) {
            loadCountry(countryBackendID, urls.get(countryBackendID), handler);
        } else {
            WLLogger.log(Level.WARNING, "TravelBriefing - missing for country \"" + countryBackendID + "\"!");
            handler.handle("null");
        }
    }

    private void loadCountry(String country, String url, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        requestJSON(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject json = new JSONObject(object.toString());
                final String iso2 = json.getJSONObject("names").getString("iso2").toLowerCase();
                json.remove("names");
                json.remove("timezone");
                json.remove("electricity");
                json.remove("water");
                json.getJSONObject("currency").remove("compare");
                final List<String> neighbors = getNeighbors(json.getJSONArray("neighbors"));
                json.put("neighbors", neighbors);
                final String string = json.toString();
                countries.put(country, string);
                WLLogger.log(Level.INFO, "TravelBriefing - loaded country \"" + country + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handle(string);
            }
        });
    }
    private List<String> getNeighbors(JSONArray array) {
        final List<String> neighbors = new ArrayList<>();
        for(Object obj : array) {
            final JSONObject json = (JSONObject) obj;
            final String name = json.getString("name").toLowerCase().replace(" ", "");
            neighbors.add(name);
        }
        return neighbors;
    }
}
