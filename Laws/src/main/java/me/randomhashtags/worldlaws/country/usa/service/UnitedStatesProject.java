package me.randomhashtags.worldlaws.country.usa.service;

import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.usa.CongressService;
import me.randomhashtags.worldlaws.people.HumanName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum UnitedStatesProject implements RestAPI, CongressService {
    INSTANCE;

    private final HashMap<String, String> politicians;

    UnitedStatesProject() {
        politicians = new HashMap<>();
    }

    public String getPolitician(String id) {
        if(!politicians.containsKey(id)) {
            loadPoliticians(CongressType.CURRENT);
            if(!politicians.containsKey(id)) {
                loadPoliticians(CongressType.HISTORICAL);
                if(!politicians.containsKey(id)) {
                    WLLogger.logError(this, "politician doesn't exist with id \"" + id + "\"!");
                }
            }
        }
        return politicians.get(id);
    }

    private void loadPoliticians(CongressType type) {
        final long started = System.currentTimeMillis();
        final String typeString = type.name();
        final String url = "https://theunitedstates.io/congress-legislators/legislators-" + typeString.toLowerCase() + ".json";
        final JSONArray array = requestJSONArray(url, RequestMethod.GET);
        int amount = 0;
        if(array != null) {
            amount = array.length();
            for(Object obj : array) {
                final JSONObject json = (JSONObject) obj;
                final UnitedStatesProjectPolitician politician = new UnitedStatesProjectPolitician(json);
                final HumanName name = politician.getName();
                final String id = name.getFirstName() + name.getMiddleName() + name.getLastName();
                politicians.put(id, politician.toString());
            }
        } else {
            WLLogger.logError(this, "loadPoliticians - array == null!");
        }
        WLLogger.logInfo("UnitedStatesProject - loaded " + amount + " " + typeString + " politicians (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private enum CongressType {
        CURRENT,
        HISTORICAL
    }
}
