package me.randomhashtags.worldlaws.country.usa.service.usaproject;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.usa.service.CongressService;
import me.randomhashtags.worldlaws.people.HumanName;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum UnitedStatesProject implements RestAPI, CongressService {
    INSTANCE;

    private HashMap<String, String> politicians;

    UnitedStatesProject() {
        politicians = new HashMap<>();
    }

    public void getPolitician(String id, CompletionHandler handler) {
        if(!politicians.containsKey(id)) {
            loadPoliticians(CongressType.CURRENT, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(!politicians.containsKey(id)) {
                        loadPoliticians(CongressType.HISTORICAL, new CompletionHandler() {
                            @Override
                            public void handle(Object object) {
                                final String target = object.toString();
                                if(politicians.containsKey(target)) {
                                    handler.handle(target);
                                } else {
                                    WLLogger.log(Level.WARN, "UnitedStatesProject - politician doesn't exist with id \"" + id + "\"!");
                                }
                            }
                        });
                    } else {
                        handler.handle(politicians.get(id));
                    }
                }
            });
        } else {
            handler.handle(politicians.get(id));
        }
    }

    private void loadPoliticians(CongressType type, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String typeString = type.name();
        final String url = "https://theunitedstates.io/congress-legislators/legislators-" + typeString.toLowerCase() + ".json";
        requestJSONArray(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final UnitedStatesProjectPolitician politician = new UnitedStatesProjectPolitician(json);
                    final HumanName name = politician.getName();
                    final String id = name.getFirstName() + name.getMiddleName() + name.getLastName();
                    politicians.put(id, politician.toString());
                }
                WLLogger.log(Level.INFO, "UnitedStatesProject - loaded " + typeString + " politicians (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handle(null);
            }
        });
    }
    private enum CongressType {
        CURRENT,
        HISTORICAL
    }
}
