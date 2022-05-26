package me.randomhashtags.worldlaws.politics;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.WLSubdivisions;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Elections implements RestAPI, DataValues {
    INSTANCE;
    
    public String getAPIKey() {
        return Settings.PrivateValues.Google.getCivicAPIKey();
    }

    public JSONObjectTranslatable refresh() {
        // https://developers.google.com/civic-information/docs/v2
        final long started = System.currentTimeMillis();
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("key", getAPIKey());

        final JSONObject requestJSON = requestJSONObject("https://www.googleapis.com/civicinfo/v2/elections", GET_CONTENT_HEADERS, query);
        JSONObjectTranslatable json = null;
        if(requestJSON != null) {
            final JSONArray elections = requestJSON.getJSONArray("elections");
            final HashMap<String, HashMap<String, HashMap<String, HashSet<Election>>>> territoryElections = new HashMap<>();
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
                            final WLCountry targetCountry = WLCountry.valueOfString(abbreviation);
                            country = targetCountry != null ? targetCountry.getBackendID() : "unknown";
                        } else if(value.startsWith("state:")) {
                            final String abbreviation = value.split(":")[1].toUpperCase();
                            final SovereignStateSubdivision subdivision = WLSubdivisions.valueOfString(abbreviation);
                            if(subdivision != null) {
                                territory = subdivision.getName().toLowerCase().replace(" ", "");
                            }
                        }
                    }
                    final Election electionString = new Election(id, name);
                    territoryElections.putIfAbsent(dateString, new HashMap<>());
                    final HashMap<String, HashMap<String, HashSet<Election>>> countryMap = territoryElections.get(dateString);
                    countryMap.putIfAbsent(country, new HashMap<>());
                    final HashMap<String, HashSet<Election>> territoryMap = countryMap.get(country);
                    territoryMap.putIfAbsent(territory, new HashSet<>());
                    territoryMap.get(territory).add(electionString);
                }
            }

            json = new JSONObjectTranslatable();
            for(Map.Entry<String, HashMap<String, HashMap<String, HashSet<Election>>>> datesMap : territoryElections.entrySet()) {
                final String dateString = datesMap.getKey();
                final JSONObjectTranslatable dateStringJSON = new JSONObjectTranslatable();
                for(Map.Entry<String, HashMap<String, HashSet<Election>>> countriesMap : datesMap.getValue().entrySet()) {
                    final String country = countriesMap.getKey();
                    final JSONObjectTranslatable countryJSON = new JSONObjectTranslatable();
                    for(Map.Entry<String, HashSet<Election>> territoriesMap : countriesMap.getValue().entrySet()) {
                        final String territory = territoriesMap.getKey();
                        final JSONObjectTranslatable territoryJSON = new JSONObjectTranslatable();
                        for(Election election : territoriesMap.getValue()) {
                            final String id = election.getID();
                            territoryJSON.put(id, election.toJSONObject(), true);
                        }
                        countryJSON.put(territory, territoryJSON, true);
                    }
                    dateStringJSON.put(country, countryJSON, true);
                }
                json.put(dateString, dateStringJSON, true);
            }
        }
        WLLogger.logInfo("Elections - refreshed upcoming elections (took " + WLUtilities.getElapsedTime(started) + ")");
        return json;
    }
    private void getRepresentatives(String ocdDivisionId, CompletionHandler handler) {
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("key", getAPIKey());
        final JSONObject json = requestJSONObject("https://www.googleapis.com/civicinfo/v2/representatives/" + ocdDivisionId.replace("/", "%2F").replace(":", "%3A"), GET_CONTENT_HEADERS, query);
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
}
