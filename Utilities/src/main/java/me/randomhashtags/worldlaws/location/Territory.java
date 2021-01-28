package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.NotNull;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;

public interface Territory extends Jsoupable {
    HashMap<String, Territory> ABBREVIATIONS = new HashMap<>();
    HashMap<String, Territory> NAMES = new HashMap<>();

    String getBackendID();
    String getName();
    String getAbbreviation();
    String getFlagURL();
    String getGovernmentURL();

    default String toJSON() {
        final String flagURL = getFlagURL();
        return "{" +
                "\"backendID\":\"" + getBackendID() + "\"," +
                "\"name\":\"" + getName() + "\"," +
                "\"abbreviation\":\"" + getAbbreviation() + "\"," +
                "\"flagURL\":" + (flagURL != null ? "\"" + flagURL + "\"" : null) + "," +
                "\"governmentURL\":\"" + getGovernmentURL() + "\"" +
                "}";
    }

    static Territory fromJSON(JSONObject json) {
        return new Territory() {
            @Override
            public String getBackendID() {
                return json.getString("backendID");
            }
            @Override
            public String getName() {
                return json.getString("name");
            }
            @Override
            public String getAbbreviation() {
                return json.getString("abbreviation");
            }
            @Override
            public String getFlagURL() {
                return json.getString("flagURL");
            }
            @Override
            public String getGovernmentURL() {
                return json.getString("governmentURL");
            }
        };
    }

    static Territory valueOfAbbreviation(@NotNull String abbreviation, @NotNull Collection<Territory> territories) {
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
    static Territory valueOfName(@NotNull String name, @NotNull Collection<Territory> territories) {
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
