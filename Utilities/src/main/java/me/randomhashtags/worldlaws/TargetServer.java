package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.iap.InAppPurchases;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum TargetServer implements RestAPI, DataValues {
    COUNTRIES,
    ENVIRONMENT,
    FEEDBACK,
    LAWS,
    NEWS,
    SERVICES,
    SPACE,
    TECHNOLOGY,
    UPCOMING_EVENTS,
    WEATHER,

    HOME,
    PING,
    INAPPPURCHASES,
    COMBINE,
    ;

    private static JSONObject SERVER_JSON = Jsonable.getSettingsJSON().getJSONObject("server");
    private static final HashMap<APIVersion, JSONObject> HOME_JSON;
    private static final HashMap<APIVersion, HashMap<HashSet<String>, String>> HOME_JSON_QUERIES;
    private static final HashMap<String, TargetServer> BACKEND_IDS;

    private static String PING_RESPONSE, MAINTENANCE_MESSAGE;
    private static boolean MAINTENANCE_MODE = false;
    private static long MAINTENANCE_STARTED;

    static {
        HOME_JSON = new HashMap<>();
        HOME_JSON_QUERIES = new HashMap<>();
        BACKEND_IDS = new HashMap<>();
        for(TargetServer server : TargetServer.values()) {
            BACKEND_IDS.put(server.getBackendID(), server);
        }
    }

    public static boolean isMaintenanceMode() {
        return MAINTENANCE_MODE;
    }
    public static void setMaintenanceMode(boolean active, String reason) {
        if(MAINTENANCE_MODE == active) {
            return;
        }
        final long now = System.currentTimeMillis();
        WLLogger.logInfo("TargetServer - " + (active ? "started" : "ended") + " maintenance mode" + (active ? "" : " (active for " + (now-MAINTENANCE_STARTED) + "ms)"));
        MAINTENANCE_MODE = active;
        MAINTENANCE_MESSAGE = reason;
        if(active) {
            MAINTENANCE_STARTED = now;
        }
        updatePingResponse();

        if(!active) {
            updateDetails();
        }
    }
    private static void updateDetails() {
        HOME_JSON.clear();
        HOME_JSON_QUERIES.clear();
        SERVER_JSON = Jsonable.getSettingsJSON().getJSONObject("server");
        final JSONObject serversJSON = SERVER_JSON.getJSONObject("servers");
        for(TargetServer server : TargetServer.values()) {
            server.updateAddressDetails(serversJSON);
        }
    }

    private String ipAddressCache;
    private int port;
    private APIVersion apiVersion;

    private String getIpAddress() {
        if(ipAddressCache == null && isRealServer()) {
            updateAddressDetails(SERVER_JSON.getJSONObject("servers"));
        }
        return ipAddressCache;
    }
    public int getPort() {
        if(port == 0) {
            updateAddressDetails(SERVER_JSON.getJSONObject("servers"));
        }
        return port;
    }
    public int getDefaultPort() {
        switch (this) {
            case COUNTRIES: return 0;
            case ENVIRONMENT: return 0;
            case FEEDBACK: return 0;
            case LAWS: return 0;
            case NEWS: return 0;
            case SERVICES: return 0;
            case SPACE: return 0;
            case TECHNOLOGY: return 0;
            case UPCOMING_EVENTS: return 0;
            case WEATHER: return 0;
            default: return -1;
        }
    }
    private void updateAddressDetails(JSONObject serversJSON) {
        if(isRealServer()) {
            final JSONObject target = serversJSON.getJSONObject(getNameLowercase());
            port = target.has("port") ? target.getInt("port") : getDefaultPort();
            final String addressKey = "address";
            final String ip = target.has(addressKey) ? target.getString(addressKey) : SERVER_JSON.getString("default_address");
            ipAddressCache = ip + ":" + port;
        }
    }
    public boolean isRealServer() {
        switch (this) {
            case HOME:
            case PING:
            case INAPPPURCHASES:
            case COMBINE:
                return false;
            default:
                return true;
        }
    }

    public String getBackendID() {
        return name().toLowerCase().replace("_", "");
    }
    public String getName() {
        return LocalServer.toCorrectCapitalization(name()).replace(" ", "");
    }
    public String getNameLowercase() {
        return name().toLowerCase();
    }
    public APIVersion getAPIVersion() {
        if(apiVersion == null) {
            final String key = "api_version", defaultKey = "default_" + key;
            final JSONObject serverJSON = getServerJSON();
            final int target = serverJSON != null && serverJSON.has(key) ? serverJSON.getInt(key) : SERVER_JSON.has(defaultKey) ? SERVER_JSON.getInt(defaultKey) : 1;
            apiVersion = APIVersion.valueOfVersion(target);
        }
        return apiVersion;
    }

    private JSONObject getServerJSON() {
        final String name = getNameLowercase();
        final JSONObject serversJSON = SERVER_JSON.getJSONObject("servers");
        return serversJSON.has(name) ? serversJSON.getJSONObject(name) : null;
    }

    public void sendResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query, CompletionHandler handler) {
        switch (this) {
            case PING:
                handler.handleString(getPingResponse());
                break;
            case INAPPPURCHASES:
                handler.handleString(InAppPurchases.getProductIDs(version));
                break;
            default:
                tryHandlingResponse(version, identifier, method, request, query, handler);
                break;
        }
    }

    private void tryHandlingResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query, CompletionHandler handler) {
        if(MAINTENANCE_MODE) {
            handler.handleString(null);
        } else {
            final HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Charset", DataValues.ENCODING.name());
            headers.put("***REMOVED***", identifier);
            switch (this) {
                case HOME:
                    getHomeResponse(version, method, headers, query, handler);
                    break;
                case COMBINE:
                    getCombinedResponse(version, identifier, method, request, handler);
                    break;
                default:
                    handleProxyResponse(version, method, request, headers, handler);
                    break;
            }
        }
    }

    private void getCombinedResponse(APIVersion version, String identifier, RequestMethod method, String request, CompletionHandler handler) {
        final String[] values = request.split("&&");
        final ConcurrentHashMap<String, String> responses = new ConcurrentHashMap<>();
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleStringValue(String key, String value) {
                if(value != null) {
                    responses.put(key, value);
                }
            }
        };
        ParallelStream.stream(Arrays.asList(values), valueObj -> {
            final String value = (String) valueObj;
            final String[] target = value.split("/");
            final String apiVersionString = target[0], serverBackendID = target[1];
            final APIVersion apiVersion = APIVersion.valueOfInput(apiVersionString);
            final TargetServer server = TargetServer.valueOfBackendID(serverBackendID);
            if(server != null) {
                server.sendResponse(apiVersion, identifier, method, value, null, new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        completionHandler.handleStringValue(value, string);
                    }
                });
            } else {
                completionHandler.handleStringValue(value, null);
            }
        });

        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<String, String> map : responses.entrySet()) {
            builder.append(isFirst ? "" : ",").append("\"").append(map.getKey()).append("\"").append(":").append(map.getValue());
            isFirst = false;
        }
        builder.append("}");
        handler.handleString(builder.toString());
    }
    private void handleProxyResponse(APIVersion version, RequestMethod method, String request, HashMap<String, String> headers, CompletionHandler handler) {
        final String url = getIpAddress() + "/" + version.name() + "/" + request;
        handleProxyResponse(url, method, headers, handler);
    }
    private void handleProxyResponse(String url, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        request(url, method, headers, null, handler);
    }

    public static String getPingResponse() {
        if(PING_RESPONSE == null) {
            final long interval = WLUtilities.PROXY_PING_RESPONSE_UPDATE_INTERVAL;
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updatePingResponse();
                }
            }, interval, interval);

            updatePingResponse();
        }
        return PING_RESPONSE;
    }
    private static void updatePingResponse() {
        final JSONObject json = new JSONObject();

        if(MAINTENANCE_MODE) {
            final JSONObject maintenanceJSON = new JSONObject();
            maintenanceJSON.put("msg", MAINTENANCE_MESSAGE);
            maintenanceJSON.put("started", MAINTENANCE_STARTED);
            json.put("maintenance", maintenanceJSON);
        }

        final JSONObject responseVersions = new JSONObject();
        for(ResponseVersions responseVersion : ResponseVersions.values()) {
            responseVersions.put(responseVersion.getKey(), responseVersion.getValue());
        }
        json.put("response_versions", responseVersions);
        PING_RESPONSE = json.toString();
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
                    final String[] preValues = string.split("/");
                    final String lastValue = preValues[preValues.length-1];
                    final String[] values = string.substring(0, string.length()-lastValue.length()-1).split("/");
                    JSONObject lastJSON = json;
                    for(String key : values) {
                        if(lastJSON.has(key)) {
                            lastJSON = lastJSON.getJSONObject(key);
                        }
                    }
                    lastJSON.remove(lastValue);
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
            SERVICES,
            UPCOMING_EVENTS,
            WEATHER
        };
        final HashMap<String, String> requests = new HashMap<>();
        requests.put("trending", null);
        for(TargetServer server : servers) {
            requests.put(server.name().toLowerCase(), server.getIpAddress() + "/" + versionName + "/home");
        }

        final ConcurrentHashMap<String, JSONObject> values = new ConcurrentHashMap<>();
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleStringValue(String key, String value) {
                if(value != null) {
                    try {
                        final JSONObject json = new JSONObject(value);
                        values.put(key, json);
                    } catch (Exception e) {
                        WLLogger.logInfo("TargetServer - ERROR - updateHomeResponse - isUpdate=" + isUpdate + ";key=" + key + ";value=" + value);
                        WLUtilities.saveException(e);
                    }
                }
            }
        };
        ParallelStream.stream(requests.entrySet(), entryObj -> {
            @SuppressWarnings({ "unchecked" })
            final Map.Entry<String, String> entry = (Map.Entry<String, String>) entryObj;
            final String key = entry.getKey(), value = entry.getValue();
            switch (key) {
                case "trending":
                    Statistics.INSTANCE.getTrendingJSON(new CompletionHandler() {
                        @Override
                        public void handleJSONObject(JSONObject json) {
                            completionHandler.handleStringValue(key, json.isEmpty() ? null : json.toString());
                        }
                    });
                    break;
                default:
                    request(value, method, headers, null, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            completionHandler.handleStringValue(key, string);
                        }
                    });
                    break;
            }
        });
        final JSONObject json = new JSONObject();
        json.put("request_epoch", started);
        for(Map.Entry<String, JSONObject> map : values.entrySet()) {
            final String serverName = map.getKey();
            final JSONObject keyValue = map.getValue();
            json.put(serverName, keyValue);
        }
        HOME_JSON.put(version, json);
        HOME_JSON_QUERIES.remove(version);
        WLLogger.logInfo("TargetServer - " + (isUpdate ? "auto-" : "") + "updated " + versionName + " home responses (took " + (System.currentTimeMillis()-started) + "ms)");
        if(handler != null) {
            final String string = json.toString();
            handler.handleString(string);
        }
    }

    public static TargetServer valueOfBackendID(String backendID) {
        final TargetServer server = BACKEND_IDS.get(backendID);
        if(server == null) {
            WLLogger.logError("TargetServer", "failed to find server with backendID \"" + backendID + "\"!");
        }
        return server;
    }
}
