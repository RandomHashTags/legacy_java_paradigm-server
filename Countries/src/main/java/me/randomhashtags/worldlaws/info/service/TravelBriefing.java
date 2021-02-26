package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum TravelBriefing implements CountryService {
    INSTANCE;

    private HashMap<String, String> urls, countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_TRAVEL_BRIEFING;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(urls == null) {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    getBriefingJSON(countryBackendID, handler);
                }
            });
        } else {
            getBriefingJSON(countryBackendID, handler);
        }
    }

    @Override
    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        urls = new HashMap<>();
        countries = new HashMap<>();
        requestJSONArray("https://travelbriefing.org/countries.json", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String country = json.getString("name").toLowerCase().replace(" ", ""), url = json.getString("url");
                    urls.put(country, url);
                }
                WLLogger.log(Level.INFO, getInfo().name() + " - loaded urls (took " + (System.currentTimeMillis()-started) + "ms)");
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
            WLLogger.log(Level.WARN, getInfo().name() + " - missing for country \"" + countryBackendID + "\"!");
            handler.handle("null");
        }
    }

    private void loadCountry(String country, String url, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final String iso2 = json.getJSONObject("names").getString("iso2").toLowerCase();
                json.remove("names");
                json.remove("timezone");
                json.remove("electricity");
                json.remove("water");
                if(json.getJSONArray("vaccinations").isEmpty()) {
                    json.remove("vaccinations");
                }
                json.getJSONObject("currency").remove("compare");
                final List<String> neighbors = getNeighbors(json.getJSONArray("neighbors"));
                json.put("neighbors", neighbors);
                final String string = json.toString();
                countries.put(country, string);
                WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + country + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
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
