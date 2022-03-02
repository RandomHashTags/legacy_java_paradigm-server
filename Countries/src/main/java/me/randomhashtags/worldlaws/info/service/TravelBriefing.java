package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.CountryService;
import me.randomhashtags.worldlaws.service.CountryServiceValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum TravelBriefing implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_TRAVEL_BRIEFING;
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_STATIC;
    }

    @Override
    public String getCountryValue(String countryBackendID) {
        return getCountryTravelBriefing(countryBackendID);
    }

    @Override
    public String loadData() {
        return null;
    }

    private String getCountryTravelBriefing(String country) {
        final long started = System.currentTimeMillis();
        if(countries == null) {
            countries = new HashMap<>();
        }

        final String targetCountry = country.toLowerCase().replace(" ", "_");
        String string = null;
        if(countries.containsKey(targetCountry)) {
            string = countries.get(targetCountry);
        } else {
            final JSONObject json = getJSONObject(Folder.COUNTRIES_SERVICES_TRAVEL_BRIEFING, country, new CompletionHandler() {
                @Override
                public JSONObject loadJSONObject() {
                    final String country;
                    switch (targetCountry) {
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
                            country = targetCountry;
                            break;
                    }
                    final String url = "https://travelbriefing.org/" + country + "?format=json";
                    JSONObject json = requestJSONObject(url);
                    if(json != null) {
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
                    } else {
                        WLUtilities.saveLoggedError("TravelBriefing", "Failed to get details for country \"" + country + "\", targetCountry=\"" + targetCountry + "\"!");
                        json = new JSONObject();
                    }
                    return json;
                }
            });
            string = new CountryServiceValue(TravelBriefing.INSTANCE, json.toString()).toString();
            WLLogger.logInfo(getInfo().name() + " - loaded \"" + country + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
            countries.put(targetCountry, string);
        }
        return string;
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
