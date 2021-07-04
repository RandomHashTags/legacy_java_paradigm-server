package me.randomhashtags.worldlaws;

import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

public enum Statistics implements Jsonable {
    INSTANCE;

    public void save(String serverName, HashSet<String> uniqueIdentifiers, HashMap<String, Integer> requests) {
        if(!requests.isEmpty()) {
            final FileType fileType = FileType.LOGS;
            final LocalDate nowUTC = LocalDate.now(Clock.systemUTC());
            final String nowEpoch = "" + nowUTC.toEpochDay();
            getJSONObject(fileType, nowEpoch, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    handler.handleJSONObject(new JSONObject());
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json.has("uniqueIdentifiers")) {
                        for(Object obj : json.getJSONArray("uniqueIdentifiers")) {
                            uniqueIdentifiers.add((String) obj);
                        }
                    }
                    json.put("uniqueIdentifiers", uniqueIdentifiers);
                    json.put("_uniqueUsers", uniqueIdentifiers.size());
                    final JSONObject serverValuesJSON = json.has(serverName) ? json.getJSONObject(serverName) : new JSONObject();
                    for(Map.Entry<String, Integer> value : requests.entrySet()) {
                        final String request = value.getKey();
                        final int existingRequestValue = serverValuesJSON.has(request) ? serverValuesJSON.getInt(request) : 0;
                        final int requestValue = value.getValue();
                        serverValuesJSON.put(request, existingRequestValue + requestValue);
                    }
                    json.put(serverName, serverValuesJSON);
                    setFileJSONObject(fileType, nowEpoch, json);
                }
            });
        }
    }
}
