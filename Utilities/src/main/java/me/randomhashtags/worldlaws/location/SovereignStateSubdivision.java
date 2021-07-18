package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.ServerObject;

public interface SovereignStateSubdivision extends ServerObject {
    String name();
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

    @Override
    default String toServerJSON() {
        final String flagURL = getFlagURL(), postalCodeAbbreviation = getPostalCodeAbbreviation(), governmentURL = getGovernmentURL();
        return "\"" + getName() + "\":{" +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                (governmentURL != null ? "\"governmentURL\":\"" + governmentURL + "\"," : "") +
                "\"postalCodeAbbreviation\":\"" + postalCodeAbbreviation + "\"" +
                "}";
    }
}
