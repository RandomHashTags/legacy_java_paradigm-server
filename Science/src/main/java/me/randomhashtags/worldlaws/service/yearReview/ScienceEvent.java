package me.randomhashtags.worldlaws.service.yearReview;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public final class ScienceEvent extends JSONObject {
    public ScienceEvent(String description, EventSources externalLinks, EventSources sources) {
        put("description", LocalServer.fixEscapeValues(description));
        if(externalLinks != null) {
            put("externalLinks", externalLinks.toJSONObject());
        }
        put("sources", sources.toJSONObject());
    }

    public void updateMentionedCountries(WLCountry[] countries) {
        final String description = getString("description").toLowerCase();
        final JSONArray mentionedCountries = new JSONArray();
        for(WLCountry country : countries) {
            if(description.contains(country.getShortName().toLowerCase())) {
                mentionedCountries.put(country.getBackendID());
            } else {
                final HashSet<String> aliases = country.getAliases();
                if(aliases != null) {
                    for(String alias : aliases) {
                        if(description.contains(alias.toLowerCase())) {
                            mentionedCountries.put(country.getBackendID());
                            break;
                        }
                    }
                }
            }
        }
        if(!mentionedCountries.isEmpty()) {
            put("countries", mentionedCountries);
        }
    }
}
