package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.iap.InAppPurchases;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum TargetServer implements RestAPI, DataValues {
    COUNTRIES(WL_COUNTRIES_SERVER_IP),
    ENVIRONMENT(WL_ENVIRONMENT_SERVER_IP),
    FEEDBACK(WL_FEEDBACK_SERVER_IP),
    LAWS(WL_LAWS_SERVER_IP),
    NEWS(WL_NEWS_SERVER_IP),
    SERVICES(WL_SERVICES_SERVER_IP),
    SPACE(WL_SPACE_SERVER_IP),
    TECHNOLOGY(WL_TECHNOLOGY_SERVER_IP),
    UPCOMING_EVENTS(WL_UPCOMING_EVENTS_SERVER_IP),
    WEATHER(WL_WEATHER_SERVER_IP),

    HOME,
    PING,
    INAPPPURCHASES
    ;

    private static final HashMap<APIVersion, JSONObject> HOME_JSON;
    private static final HashMap<APIVersion, HashMap<HashSet<String>, String>> HOME_JSON_QUERIES;

    static {
        HOME_JSON = new HashMap<>();
        HOME_JSON_QUERIES = new HashMap<>();
    }

    private String ipAddress;
    private int port;
    private String pingResponseCache;

    TargetServer() {
    }
    TargetServer(String ipAddress) {
        this.ipAddress = ipAddress;
        final String[] values = ipAddress.split(":");
        this.port = Integer.parseInt(values[values.length-1]);
    }

    public String getBackendID() {
        return name().toLowerCase().replace("_", "");
    }
    public String getName() {
        return LocalServer.toCorrectCapitalization(name()).replace(" ", "");
    }
    public int getPort() {
        return port;
    }
    public APIVersion getAPIVersion() {
        switch (this) {
            default: return APIVersion.v1;
        }
    }
    public int getResponseVersion() { // Only used if the server doesn't auto update its content
        switch (this) {
            case COUNTRIES:
                return 1;
            case ENVIRONMENT:
                return 1;
            case SPACE:
                return 1;
            default:
                return -1;
        }
    }

    public void sendResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query, CompletionHandler handler) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", identifier);
        switch (this) {
            case PING:
                handler.handleString(getPingResponse());
                break;
            case INAPPPURCHASES:
                handler.handleString(InAppPurchases.getProductIDs(version));
                break;
            case HOME:
                getHomeResponse(version, method, headers, query, handler);
                break;
            default:
                final String versionName = version.name(), serverName = getBackendID();
                request = request.substring(versionName.length() + serverName.length() + 2);
                final String url = ipAddress + "/" + version.name() + "/" + request;
                request(url, method, headers, null, handler);
                break;
        }
    }

    private String getPingResponse() {
        if(pingResponseCache == null) {
            final JSONObject json = new JSONObject(), responseVersions = new JSONObject();

            for(TargetServer server : values()) {
                if(server.port > 0) {
                    responseVersions.put(server.getBackendID(), server.getResponseVersion());
                }
            }
            json.put("response_versions", responseVersions);
            pingResponseCache = json.toString();
        }
        return pingResponseCache;
    }

    private void getHomeResponse(APIVersion version, RequestMethod method, HashMap<String, String> headers, HashSet<String> query, CompletionHandler handler) {
        if(HOME_JSON.containsKey(version)) {
            handler.handleString(getHomeResponse(version, query));
        } else {
            updateHomeResponse(version, false, method, headers, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(getHomeResponse(version, query));
                }
            });
        }
    }
    private String getHomeResponse(APIVersion version, HashSet<String> query) {
        if(HOME_JSON_QUERIES.containsKey(version)) {
            return HOME_JSON_QUERIES.get(version).containsKey(query) ? HOME_JSON_QUERIES.get(version).get(query) : loadQueryJSON(version, query);
        } else {
            HOME_JSON_QUERIES.put(version, new HashMap<>());
            return loadQueryJSON(version, query);
        }
    }
    private String loadQueryJSON(APIVersion version, HashSet<String> query) {
        final String target = getQueryJSON(version, query);
        HOME_JSON_QUERIES.get(version).put(query, target);
        return target;
    }
    private String getQueryJSON(APIVersion version, HashSet<String> query) {
        final JSONObject homeJSON = HOME_JSON.get(version);
        final JSONObject json = new JSONObject(homeJSON.toMap());
        if(!query.isEmpty()) {
            for(String string : query) {
                if(string.contains("/")) {
                    final String[] values = string.split("/");
                    final String key = values[0];
                    if(json.has(key)) {
                        json.getJSONObject(key).remove(values[1]);
                    }
                } else {
                    json.remove(string);
                }
            }
        }
        return json.toString();
    }

    private void updateHomeResponse(APIVersion version, boolean isUpdate, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        if(!isUpdate) {
            final long interval = WLUtilities.PROXY_HOME_RESPONSE_UPDATE_INTERVAL;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateHomeResponse(version, true, method, headers, null);
                }
            }, interval, interval);
        }

        final String versionName = version.name();
        final TargetServer[] servers = {
            COUNTRIES,
            LAWS,
            SERVICES,
            UPCOMING_EVENTS,
            WEATHER
        };
        final HashMap<String, String> requests = new HashMap<>();
        requests.put("trending", null);
        for(TargetServer server : servers) {
            requests.put(server.name().toLowerCase(), server.ipAddress + "/" + versionName + "/home");
        }

        final int max = requests.size();
        final ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleStringValue(String key, String value) {
                if(value != null) {
                    values.put(key, value);
                }
                if(completed.addAndGet(1) == max) {
                    final StringBuilder builder = new StringBuilder("{");
                    builder.append("\"request_epoch\":").append(started);
                    for(Map.Entry<String, String> map : values.entrySet()) {
                        final String serverName = map.getKey();
                        final String keyValue = map.getValue();
                        builder.append(",").append("\"").append(serverName).append("\":").append(keyValue);
                    }
                    builder.append("}");
                    final String string = builder.toString();
                    final JSONObject json = new JSONObject(string);
                    HOME_JSON.put(version, json);
                    HOME_JSON_QUERIES.remove(version);
                    WLLogger.log(Level.INFO, "TargetServer - " + (isUpdate ? "auto-" : "") + "updated " + versionName + " home responses (took " + (System.currentTimeMillis()-started) + "ms)");
                    if(handler != null) {
                        handler.handleString(string);
                    }
                }
            }
        };
        requests.entrySet().parallelStream().forEach(entry -> {
            final String key = entry.getKey();
            switch (key) {
                case "trending":
                    Statistics.INSTANCE.getTrendingJSON(new CompletionHandler() {
                        @Override
                        public void handleJSONObject(JSONObject json) {
                            completionHandler.handleStringValue("trending", json.isEmpty() ? null : json.toString());
                        }
                    });
                    break;
                default:
                    request(entry.getValue(), method, headers, null, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            completionHandler.handleStringValue(key, string);
                        }
                    });
                    break;
            }
        });
    }

    public static TargetServer valueOfBackendID(String backendID) {
        for(TargetServer server : TargetServer.values()) {
            if(backendID.equals(server.getBackendID())) {
                return server;
            }
        }
        WLLogger.log(Level.WARN, "TargetServer - failed to find a server with backendID \"" + backendID + "\"!");
        return null;
    }
}
