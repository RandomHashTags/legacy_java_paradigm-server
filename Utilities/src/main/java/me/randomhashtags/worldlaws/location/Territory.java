package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.NotNull;
import me.randomhashtags.worldlaws.PopulationEstimate;

import java.util.HashMap;

public interface Territory extends Jsoupable {
    HashMap<String, Territory> ABBREVIATIONS = new HashMap<>();
    HashMap<String, Territory> NAMES = new HashMap<>();

    Country getCountry();
    String getBackendID();
    String getName();
    String getAbbreviation();
    String getFlagURL();
    PopulationEstimate getPopulationEstimate();
    String getGovernmentURL();

    default String toJSON() {
        final PopulationEstimate estimate = getPopulationEstimate();
        final String flagURL = getFlagURL();
        return "{" +
                "\"country\":\"" + getCountry().name() + "\"," +
                "\"backendID\":\"" + getBackendID() + "\"," +
                "\"name\":\"" + getName() + "\"," +
                "\"abbreviation\":\"" + getAbbreviation() + "\"," +
                "\"flagURL\":" + (flagURL != null ? "\"" + flagURL + "\"" : null) + "," +
                "\"populationEstimate\":" + (estimate != null ? estimate.toString() : "null") + "," +
                "\"governmentURL\":\"" + getGovernmentURL() + "\"" +
                "}";
    }

    static Territory valueOfAbbreviation(@NotNull String abbreviation, @NotNull Territory[] territories) {
        abbreviation = abbreviation.toLowerCase();
        if(ABBREVIATIONS.containsKey(abbreviation)) {
            return ABBREVIATIONS.get(abbreviation);
        } else {
            for(Territory territory : territories) {
                final String target = territory.getAbbreviation();
                if(abbreviation.equalsIgnoreCase(target)) {
                    ABBREVIATIONS.put(abbreviation, territory);
                    return territory;
                }
            }
            return null;
        }
    }
    static Territory valueOfName(@NotNull String name, @NotNull Territory[] territories) {
        name = name.toLowerCase();
        if(NAMES.containsKey(name)) {
            return NAMES.get(name);
        } else {
            for(Territory territory : territories) {
                final String target = territory.getName();
                if(name.equalsIgnoreCase(target)) {
                    NAMES.put(name, territory);
                    return territory;
                }
            }
            return null;
        }
    }
}
