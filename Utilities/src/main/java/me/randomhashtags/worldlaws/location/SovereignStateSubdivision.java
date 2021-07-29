package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.service.WikipediaService;
import org.json.JSONObject;

public interface SovereignStateSubdivision extends WikipediaService {
    String name();
    WLCountry getCountry();
    default String getName() {
        return LocalServer.toCorrectCapitalization(name());
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
    default WLTimeZone[] getTimeZones(WLTimeZone...timezones) {
        return timezones;
    }

    default void getInformation(CompletionHandler handler) {
        final String fileName = getCountry().getBackendID() + "-" + name();
        getJSONObject(Folder.SUBDIVISIONS_SUBDIVISIONS, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handler.handleString(null);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(null);
            }
        });
    }
    void getCitiesHashSet(CompletionHandler handler);

    default String toJSON() {
        final String flagURL = getFlagURL(), postalCodeAbbreviation = getPostalCodeAbbreviation(), governmentURL = getGovernmentURL();
        return "\"" + getName() + "\":{" +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                (governmentURL != null ? "\"governmentURL\":\"" + governmentURL + "\"," : "") +
                "\"postalCodeAbbreviation\":\"" + postalCodeAbbreviation + "\"" +
                "}";
    }
}
