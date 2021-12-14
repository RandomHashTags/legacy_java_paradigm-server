package me.randomhashtags.worldlaws.politics;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.WLSubdivisions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum Elections implements RestAPI, DataValues {
    INSTANCE;

    private String apiKey;

    public String getAPIKey() {
        if(apiKey == null) {
            apiKey = Jsonable.getSettingsPrivateValuesJSON().getJSONObject("google").getString("civic_api_key");
        }
        return apiKey;
    }

    public void refresh(CompletionHandler handler) {
        // https://developers.google.com/civic-information/docs/v2
        final long started = System.currentTimeMillis();
        final HashMap<String, String> query = new HashMap<>();
        query.put("key", getAPIKey());

        requestJSONObject("https://www.googleapis.com/civicinfo/v2/elections", RequestMethod.GET, CONTENT_HEADERS, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                String string = null;
                if(json != null) {
                    final JSONArray elections = json.getJSONArray("elections");
                    final HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> territoryElections = new HashMap<>();
                    for(Object obj : elections) {
                        final JSONObject electionJSON = (JSONObject) obj;
                        final String name = electionJSON.getString("name");
                        if(!name.equals("VIP Test Election")) {
                            final String id = electionJSON.getString("id");
                            final String electionDay = electionJSON.getString("electionDay"), ocdDivisionId = electionJSON.getString("ocdDivisionId");
                            final String[] electionDayValues = electionDay.split("-");
                            final int year = Integer.parseInt(electionDayValues[0]), month = Integer.parseInt(electionDayValues[1]), day = Integer.parseInt(electionDayValues[2]);
                            final EventDate date = new EventDate(Month.of(month), day, year);
                            final String dateString = date.getDateString();

                            String country = null, territory = null;
                            for(String value : ocdDivisionId.split("/")) {
                                if(value.startsWith("country:")) {
                                    final String abbreviation = value.split(":")[1].toUpperCase();
                                    final WLCountry targetCountry = WLCountry.valueOfISOAlpha2(abbreviation);
                                    country = targetCountry != null ? targetCountry.getBackendID() : "unknown";
                                } else if(value.startsWith("state:")) {
                                    final String abbreviation = value.split(":")[1].toUpperCase();
                                    final SovereignStateSubdivision subdivision = WLSubdivisions.valueOfString(abbreviation);
                                    if(subdivision != null) {
                                        territory = subdivision.getName().toLowerCase().replace(" ", "");
                                    }
                                }
                            }
                            final String electionString = new Election(id, name).toString();
                            territoryElections.putIfAbsent(dateString, new HashMap<>());
                            final HashMap<String, HashMap<String, HashSet<String>>> countryMap = territoryElections.get(dateString);
                            countryMap.putIfAbsent(country, new HashMap<>());
                            final HashMap<String, HashSet<String>> territoryMap = countryMap.get(country);
                            territoryMap.putIfAbsent(territory, new HashSet<>());
                            territoryMap.get(territory).add(electionString);
                        }
                    }

                    final StringBuilder builder = new StringBuilder("{");
                    boolean isFirstDate = true;
                    for(Map.Entry<String, HashMap<String, HashMap<String, HashSet<String>>>> datesMap : territoryElections.entrySet()) {
                        final String dateString = datesMap.getKey();
                        builder.append(isFirstDate ? "" : ",").append("\"").append(dateString).append("\":{");
                        isFirstDate = false;
                        boolean isFirstCountry = true;
                        for(Map.Entry<String, HashMap<String, HashSet<String>>> countriesMap : datesMap.getValue().entrySet()) {
                            final String country = countriesMap.getKey();
                            builder.append(isFirstCountry ? "" : ",").append("\"").append(country).append("\":{");
                            boolean isFirstTerritory = true;
                            for(Map.Entry<String, HashSet<String>> territoriesMap : countriesMap.getValue().entrySet()) {
                                final String territory = territoriesMap.getKey();
                                builder.append(isFirstTerritory ? "" : ",").append("\"").append(territory).append("\":{");
                                boolean isFirstElection = true;
                                for(String election : territoriesMap.getValue()) {
                                    builder.append(isFirstElection ? "" : ",").append(election);
                                    isFirstElection = false;
                                }
                                isFirstTerritory = false;
                                builder.append("}");
                            }
                            builder.append("}");
                            isFirstCountry = false;
                        }
                        builder.append("}");
                    }
                    builder.append("}");
                    string = builder.toString();
                }
                WLLogger.logInfo("Elections - refreshed upcoming elections (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleString(string);
            }
        });
    }
    private void getRepresentatives(String ocdDivisionId, CompletionHandler handler) {
        final HashMap<String, String> query = new HashMap<>();
        query.put("key", getAPIKey());
        requestJSONObject("https://www.googleapis.com/civicinfo/v2/representatives/" + ocdDivisionId.replace("/", "%2F").replace(":", "%3A"), RequestMethod.GET, CONTENT_HEADERS, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONArray offices = json.getJSONArray("offices"), officials = json.getJSONArray("officials");
                int index = 0;
                for(Object obj : offices) {
                    final JSONObject office = new JSONObject(obj.toString());
                    final JSONObject person = new JSONObject(officials.get(index).toString());
                    WLLogger.logInfo(office.getString("name") + " - " + person.getString("name"));
                    index += 1;
                }
                handler.handleString(null);
            }
        });
    }
}
