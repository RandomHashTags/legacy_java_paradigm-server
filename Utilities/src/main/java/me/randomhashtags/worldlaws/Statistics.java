package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.QuotaHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum Statistics implements Jsonable, QuotaHandler {
    INSTANCE;

    private static final Folder FOLDER = Folder.LOGS;
    private LocalDateTime now;
    private String fileName;

    private String getFolderPath(String serverName) {
        return FOLDER.getFolderName().replace("%year%", Integer.toString(now.getYear())).replace("%month%", now.getMonth().name()).replace("%day%", Integer.toString(now.getDayOfMonth())).replace("%type%", "statistics").replace("%server%", serverName);
    }
    private JSONObject getLatestLogJSON(String serverName) {
        now = LocalDateTime.now();
        final String zoneID = ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        final String folderPath = getFolderPath(serverName);
        fileName = now.getHour() + "_" + zoneID;
        FOLDER.setCustomFolderName(fileName, folderPath);
        return getJSONObject(FOLDER, fileName, null);
    }
    public void save(long started, TargetServer targetServer, HashSet<String> totalUniqueIdentifiers, ConcurrentHashMap<String, HashSet<String>> uniqueRequests, ConcurrentHashMap<String, Integer> totalRequests) {
        if(!QUOTA_REQUESTS.isEmpty()) {
            saveQuota();
        }
        if(totalRequests.isEmpty()) {
            return;
        }
        final String serverName = targetServer.getName();
        JSONObject json = getLatestLogJSON(serverName);
        if(json == null) {
            json = new JSONObject();
        }

        final JSONObject uniqueJSON = json.has("unique") ? json.getJSONObject("unique") : new JSONObject();
        final JSONObject uniqueServerValuesJSON = uniqueJSON.has(serverName) ? uniqueJSON.getJSONObject(serverName) : new JSONObject();
        int totalUniqueRequests = 0;
        for(Map.Entry<String, HashSet<String>> value : uniqueRequests.entrySet()) {
            final String identifier = value.getKey();
            final HashSet<String> requestsMadeByIdentifier = value.getValue();
            final String[] strings = identifier.split("/");
            final String version = strings[0], request = identifier.substring(version.length()+1);
            final JSONObject versionJSON = uniqueServerValuesJSON.has(version) ? uniqueServerValuesJSON.getJSONObject(version) : new JSONObject();
            final int existingIdentifiers = versionJSON.has(request) ? versionJSON.getInt(request) : 0;
            final int amount = existingIdentifiers + requestsMadeByIdentifier.size();
            totalUniqueRequests += amount;
            versionJSON.put(request, amount);
            uniqueServerValuesJSON.put(version, versionJSON);
        }
        uniqueJSON.put(serverName, uniqueServerValuesJSON);
        json.put("unique", uniqueJSON);

        final JSONObject totalJSON = json.has("total") ? json.getJSONObject("total") : new JSONObject();
        final JSONObject totalServerValuesJSON = totalJSON.has(serverName) ? totalJSON.getJSONObject(serverName) : new JSONObject();
        if(totalJSON.has("uniqueIdentifiers")) {
            final JSONArray array = totalJSON.getJSONArray("uniqueIdentifiers");
            final List<Object> list = array.toList();
            for(Object obj : list) {
                totalUniqueIdentifiers.add((String) obj);
            }
        }
        totalJSON.put("uniqueIdentifiers", totalUniqueIdentifiers);
        int totalTotalRequests = 0;
        for(Map.Entry<String, Integer> value : totalRequests.entrySet()) {
            final String request = value.getKey();
            final int existingRequests = totalServerValuesJSON.has(request) ? totalServerValuesJSON.getInt(request) : 0;
            final int total = existingRequests + value.getValue();
            totalTotalRequests += total;
            totalServerValuesJSON.put(request, total);
        }
        totalJSON.put(serverName, totalServerValuesJSON);
        json.put("total", totalJSON);

        json.put("_totalRequests", totalTotalRequests);
        json.put("_totalUniqueRequests", totalUniqueRequests);
        json.put("_totalUniqueIdentifiers", totalUniqueIdentifiers.size());
        FOLDER.setCustomFolderName(fileName, getFolderPath(serverName));
        Jsonable.setFileJSONObject(FOLDER, fileName, json);
        WLLogger.logInfo(serverName + " - Saved statistics (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public JSONObject getTrendingJSON() {
        final JSONObject trendingJSON = new JSONObject();
        for(TargetServer server : TargetServer.values()) {
            if(server.isRealServer()) {
                final String serverName = server.getName();
                final JSONObject json = getLatestLogJSON(serverName);
                if(json != null && json.has("unique")) {
                    final JSONObject uniqueJSON = json.getJSONObject("unique");
                    final String versionString = APIVersion.getLatest().name();
                    if(uniqueJSON.has(versionString)) {
                        final List<TrendingRequest> trendingRequests = new ArrayList<>(), trendingRequestsList = new ArrayList<>();

                        final JSONObject requestsJSON = uniqueJSON.getJSONObject(versionString);
                        final Set<String> set = requestsJSON.keySet();
                        set.removeIf(string -> string.equals("home"));
                        for(String request : set) {
                            final int requests = requestsJSON.getJSONObject(request).getInt("requests");
                            trendingRequestsList.add(new TrendingRequest(request, requests));
                        }
                        trendingRequestsList.sort(Comparator.comparingInt(request -> request.requests));
                        final int max = Math.min(trendingRequestsList.size(), 10);
                        for(int i = 0; i < max; i++) {
                            trendingRequests.add(trendingRequestsList.get(i));
                        }

                        for(TrendingRequest request : trendingRequests) {
                            trendingJSON.put(request.request, request.requests);
                        }
                    }
                }
                FOLDER.removeCustomFolderName(fileName);
            }
        }
        return trendingJSON.isEmpty() ? null : trendingJSON;
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
