package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CountryInformationType;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum TravelBriefing implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_TRAVEL_BRIEFING;
    }

    @Override
    public CountryInformationType getInformationType() {
        return CountryInformationType.SERVICES;
    }

    @Override
    public void getCountryValue(String countryBackendID, CompletionHandler handler) {
        getCountryTravelBriefing(countryBackendID, handler);
    }

    @Override
    public void loadData(CompletionHandler handler) {
    }

    private void getCountryTravelBriefing(String country, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        if(countries == null) {
            countries = new HashMap<>();
        }

        final String targetCountry = country.toLowerCase().replace(" ", "_");
        if(countries.containsKey(targetCountry)) {
            handler.handleString(countries.get(targetCountry));
        } else {
            getJSONObject(FileType.COUNTRIES_SERVICES_TRAVEL_BRIEFING, country, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    String country = targetCountry;
                    switch (country) {
                        case "british_virgin_islands":
                            country = "virgin_islands-british";
                            break;
                        case "democratic_republic_of_the_congo":
                            country = "congo-kinshasa";
                            break;
                        case "republic_of_the_congo":
                            country = "congo-brazzaville";
                            break;
                        default:
                            break;
                    }
                    final String url = "https://travelbriefing.org/" + country + "?format=json";
                    requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
                        @Override
                        public void handleJSONObject(JSONObject json) {
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
                            handler.handleString(string);
                        }
                    });
                }

                @Override
                public void handleJSONObject(JSONObject object) {
                    final String string = new CountryServiceValue(TravelBriefing.INSTANCE, object.toString()).toString();
                    WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + country + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    countries.put(targetCountry, string);
                    handler.handleString(string);
                }
            });
        }
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
