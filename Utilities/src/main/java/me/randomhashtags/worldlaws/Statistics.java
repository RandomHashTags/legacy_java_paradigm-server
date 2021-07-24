package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.QuotaHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

public enum Statistics implements Jsonable, QuotaHandler {
    INSTANCE;

    public void save(String serverName, HashSet<String> totalUniqueIdentifiers, HashMap<String, HashSet<String>> uniqueRequests, HashMap<String, Integer> totalRequests, CompletionHandler handler) {
        if(!QUOTA_REQUESTS.isEmpty()) {
            saveQuota();
        }
        if(!totalRequests.isEmpty()) {
            final Folder folder = Folder.LOGS;
            final LocalDate nowUTC = LocalDate.now(Clock.systemUTC());
            final String nowEpoch = "" + nowUTC.toEpochDay();
            getJSONObject(folder, nowEpoch, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    handler.handleJSONObject(new JSONObject());
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(!json.has("unique")) {
                        json.put("unique", new JSONObject());
                    }
                    if(!json.has("total")) {
                        json.put("total", new JSONObject());
                    }

                    final JSONObject uniqueJSON = json.getJSONObject("unique");
                    int totalUniqueRequests = 0;
                    final JSONObject uniqueServerValuesJSON = uniqueJSON.has(serverName) ? uniqueJSON.getJSONObject(serverName) : new JSONObject();
                    for(Map.Entry<String, HashSet<String>> value : uniqueRequests.entrySet()) {
                        final String key = value.getKey();
                        final String[] strings = key.split("/");
                        final String version = strings[0], request = key.substring(version.length()+1);
                        final JSONObject versionJSON = uniqueServerValuesJSON.has(version) ? uniqueServerValuesJSON.getJSONObject(version) : new JSONObject();
                        final JSONObject targetJSON = versionJSON.has(request) ? versionJSON.getJSONObject(request) : new JSONObject();
                        final JSONArray existingRequestValue = targetJSON.has("identifiers") ? targetJSON.getJSONArray("identifiers") : new JSONArray();
                        for(String string : value.getValue()) {
                            existingRequestValue.put(string);
                        }
                        final int uniqueRequests = existingRequestValue.length();
                        totalUniqueRequests += uniqueRequests;
                        targetJSON.put("requests", uniqueRequests);
                        targetJSON.put("identifiers", existingRequestValue);
                        versionJSON.put(request, targetJSON);
                        uniqueServerValuesJSON.put(version, versionJSON);
                    }
                    json.put("unique", uniqueServerValuesJSON);

                    final JSONObject totalJSON = json.getJSONObject("total");
                    if(totalJSON.has("uniqueIdentifiers")) {
                        for(Object obj : totalJSON.getJSONArray("uniqueIdentifiers")) {
                            totalUniqueIdentifiers.add((String) obj);
                        }
                    }
                    totalJSON.put("uniqueIdentifiers", totalUniqueIdentifiers);
                    int totalTotalRequests = 0;
                    final JSONObject totalServerValuesJSON = totalJSON.has(serverName) ? totalJSON.getJSONObject(serverName) : new JSONObject();
                    for(Map.Entry<String, Integer> value : totalRequests.entrySet()) {
                        final String request = value.getKey();
                        final int existingRequestValue = totalServerValuesJSON.has(request) ? totalServerValuesJSON.getInt(request) : 0;
                        final int requestValue = value.getValue(), total = existingRequestValue + requestValue;
                        totalTotalRequests += total;
                        totalServerValuesJSON.put(request, total);
                    }
                    totalJSON.put(serverName, totalServerValuesJSON);
                    json.put("total", totalJSON);

                    json.put("_totalRequests", totalTotalRequests);
                    json.put("_totalUniqueRequests", totalUniqueRequests);
                    json.put("_totalUniqueIdentifiers", totalUniqueIdentifiers.size());
                    setFileJSONObject(folder, nowEpoch, json);
                    if(handler != null) {
                        handler.handleObject(null);
                    }
                }
            });
        }
    }
    public void getTrendingJSON(CompletionHandler handler) {
        final Folder folder = Folder.LOGS;
        final LocalDate nowUTC = LocalDate.now(Clock.systemUTC());
        final String nowEpoch = "" + nowUTC.toEpochDay();
        getJSONObject(folder, nowEpoch, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handler.handleJSONObject(new JSONObject());
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONObject trendingJSON = new JSONObject();
                final List<TrendingRequest> trendingRequests = new ArrayList<>();
                if(json.has("unique")) {
                    final JSONObject uniqueJSON = json.getJSONObject("unique");
                    final String versionString = APIVersion.getCurrent().getName();
                    if(uniqueJSON.has(versionString)) {
                        final List<TrendingRequest> trendingRequestsList = new ArrayList<>();
                        final JSONObject requestsJSON = uniqueJSON.getJSONObject(versionString);
                        final Set<String> set = requestsJSON.keySet();
                        set.removeIf(string -> string.equals("home"));
                        for(String request : set) {
                            final int requests = requestsJSON.getJSONObject(request).getInt("requests");
                            trendingRequestsList.add(new TrendingRequest(request, requests));
                        }
                        trendingRequestsList.sort(Comparator.comparingInt(request -> request.requests));
                        final int max = Math.max(trendingRequestsList.size(), 10);
                        for(int i = 0; i < max; i++) {
                            trendingRequests.add(trendingRequestsList.get(i));
                        }
                    }
                }
                for(TrendingRequest request : trendingRequests) {
                    trendingJSON.put(request.request, request.requests);
                }
                handler.handleJSONObject(trendingJSON);
            }
        });
    }

    private final class TrendingRequest {
        private final String request;
        private final int requests;

        public TrendingRequest(String request, int requests) {
            this.request = request;
            this.requests = requests;
        }
    }
}
