package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.service.WikipediaService;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface SovereignStateSubdivision extends WikipediaService {
    String name();
    WLCountry getCountry();
    default String getName() {
        return LocalServer.toCorrectCapitalization(name());
    }
    default String getBackendID() {
        return getName().toLowerCase().replace(" ", "");
    }
    String getPostalCodeAbbreviation();
    String getFlagURL();
    default String getGovernmentURL() {
        return null;
    }
    default String getWikipediaURL() {
        return "https://en.wikipedia.org/wiki/" + getName().replace(" ", "_");
    }
    //WLTimeZone[] getTimeZones(); // TODO: implement
    default WLTimeZone[] collectTimeZones(WLTimeZone...timezones) {
        return timezones;
    }
    default SovereignStateSubdivision[] collectNeighbors(SovereignStateSubdivision...subdivisions) {
        return subdivisions;
    }
    default SovereignStateSubdivision[] getNeighbors() {
        return null;
    }

    default void getInformation(CompletionHandler handler) {
        final Folder folder = Folder.SUBDIVISIONS_INFORMATION;
        folder.setCustomFolderName(folder.getFolderName(false).replace("%country%", getCountry().getBackendID()));
        getJSONObject(folder, name(), new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values = new ConcurrentHashMap<>();

                final HashSet<String> neighbors = getNeighborsJSON();
                if(neighbors != null) {
                    values.put(SovereignStateInformationType.NEIGHBORS, neighbors);
                }

                final SovereignStateInformation information = new SovereignStateInformation(values);
                handler.handleString(information.toString());
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                final String string = json != null ? json.toString() : null;
                handler.handleString(string);
            }
        });
    }
    void getCitiesHashSet(CompletionHandler handler);

    private HashSet<String> getNeighborsJSON() {
        final SovereignStateSubdivision[] neighbors = getNeighbors();
        if(neighbors != null) {
            final HashMap<String, HashSet<String>> values = new HashMap<>();
            for(SovereignStateSubdivision subdivision : neighbors) {
                final String countryBackendID = subdivision.getCountry().getBackendID();
                if(!values.containsKey(countryBackendID)) {
                    values.put(countryBackendID, new HashSet<>());
                }
                values.get(countryBackendID).add("\"" + subdivision.getBackendID() + "\"");
            }
            final HashSet<String> set = new HashSet<>();
            for(Map.Entry<String, HashSet<String>> map : values.entrySet()) {
                final String countryBackendID = map.getKey();
                final StringBuilder builder = new StringBuilder("\"" + countryBackendID + "\":[");
                boolean isFirst = true;
                for(String subdivision : map.getValue()) {
                    builder.append(isFirst ? "" : ",").append(subdivision);
                    isFirst = false;
                }
                builder.append("]");
                set.add(builder.toString());
            }
            return set;
        }
        return null;
    }

    default String toJSON() {
        final String flagURL = getFlagURL(), postalCodeAbbreviation = getPostalCodeAbbreviation(), governmentURL = getGovernmentURL();
        return "\"" + getName() + "\":{" +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                (governmentURL != null ? "\"governmentURL\":\"" + governmentURL + "\"," : "") +
                "\"postalCodeAbbreviation\":\"" + postalCodeAbbreviation + "\"" +
                "}";
    }
}
