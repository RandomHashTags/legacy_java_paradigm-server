package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum TargetServer implements RestAPI, DataValues {
    COUNTRIES,
    ENVIRONMENT,
    FEEDBACK,
    LAWS,
    NEWS,
    PREMIUM,
    REMOTE_NOTIFICATIONS,
    SCIENCE,
    SERVICES,
    TECHNOLOGY,
    UPCOMING_EVENTS,
    WEATHER,

    HOME,
    PING,
    COMBINE,
    ;

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
        for(TargetServer server : TargetServer.values()) {
            server.updateAddressDetails();
        }
    }

    private String ipAddressCache;
    private int port;
    private APIVersion apiVersion;

    private String getIpAddress() {
        if(ipAddressCache == null && isRealServer()) {
            updateAddressDetails();
        }
        return ipAddressCache;
    }
    public int getPort() {
        if(port == 0) {
            updateAddressDetails();
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
            case SCIENCE: return 0;
            case SERVICES: return 0;
            case TECHNOLOGY: return 0;
            case UPCOMING_EVENTS: return 0;
            case WEATHER: return 0;
            case PREMIUM: return 0;
            case REMOTE_NOTIFICATIONS: return 0;
            default:
                WLLogger.logError("TargetServer", "failed to get default port for server \"" + name() + "\"!");
                return -1;
        }
    }
    private void updateAddressDetails() {
        if(isRealServer()) {
            port = Settings.Server.getPort(this);
            final String ip = Settings.Server.getAddress(this);
            ipAddressCache = ip + ":" + port;
        }
    }
    public boolean isRealServer() {
        switch (this) {
            case HOME:
            case PING:
            case COMBINE:
                return false;
            default:
                return true;
        }
    }
    public boolean recordsStatistics() {
        if(isRealServer()) {
            switch (this) {
                case REMOTE_NOTIFICATIONS:
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

    public String getBackendID() {
        return name().toLowerCase().replace("_", "");
    }
    public String getName() {
        return LocalServer.toCorrectCapitalization(name()).replace(" ", "");
    }
    public APIVersion getAPIVersion() {
        if(apiVersion == null) {
            apiVersion = APIVersion.valueOfVersion(Settings.Server.getAPIVersion(this));
        }
        return apiVersion;
    }

    public String sendResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query) {
        switch (this) {
            case PING:
                return getPingResponse();
            default:
                return tryHandlingResponse(version, identifier, method, request, query);
        }
    }

    private String tryHandlingResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query) {
        return MAINTENANCE_MODE ? null : handleResponse(version, identifier, method, request, query);
    }
    public String handleResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", identifier);
        switch (this) {
            case HOME:
                return getHomeResponse(version, method, headers, query);
            case COMBINE:
                return getCombinedResponse(version, identifier, method, request);
            default:
                return handleProxyResponse(version, method, request, headers);
        }
    }

    private String getCombinedResponse(APIVersion version, String identifier, RequestMethod method, String request) {
        final String[] values = request.split("&&");
        final ConcurrentHashMap<String, String> responses = new ConcurrentHashMap<>();
        new ParallelStream<String>().stream(Arrays.asList(values), value -> {
            final String[] target = value.split("/");
            final String apiVersionString = target[0], serverBackendID = target[1];
            final APIVersion apiVersion = APIVersion.valueOfInput(apiVersionString);
            final TargetServer server = TargetServer.valueOfBackendID(serverBackendID);
            final String string = server != null ? server.sendResponse(apiVersion, identifier, method, value, null) : null;
            if(string != null) {
                responses.put(value, string);
            }
        });

        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<String, String> map : responses.entrySet()) {
            builder.append(isFirst ? "" : ",").append("\"").append(map.getKey()).append("\"").append(":").append(map.getValue());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
    private String handleProxyResponse(APIVersion version, RequestMethod method, String request, HashMap<String, String> headers) {
        final String url = getIpAddress() + "/" + version.name() + "/" + request;
        return handleProxyResponse(url, method, headers);
    }
    private String handleProxyResponse(String url, RequestMethod method, HashMap<String, String> headers) {
        return request(url, method, headers, null);
    }

    public static String getPingResponse() {
        if(PING_RESPONSE == null) {
            final long interval = UpdateIntervals.Proxy.PING;
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
            if(responseVersion.isClientSide()) {
                responseVersions.put(responseVersion.getKey(), responseVersion.getValue());
            }
        }
        json.put("response_versions", responseVersions);
        final JSONArray offlineServers = new JSONArray();
        for(TargetServer server : TargetServer.values()) {
            if(server.isRealServer()) {
                final String url = server.getIpAddress() + "/ping";
                final String string = server.request(url, RequestMethod.GET, null, null);
                if(string == null) {
                    offlineServers.put(server.getBackendID());
                }
            }
        }
        if(!offlineServers.isEmpty()) {
            json.put("offline_servers", offlineServers);
        }
        PING_RESPONSE = json.toString();
    }

    private String getHomeResponse(APIVersion version, RequestMethod method, HashMap<String, String> headers, HashSet<String> query) {
        if(!HOME_JSON.containsKey(version)) {
            final String string = updateHomeResponse(version, false, method, headers);
        }
        return getHomeResponse(version, query);
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

    public String updateHomeResponse() {
        final APIVersion version = APIVersion.getLatest();
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", Settings.Server.getUUID());
        return updateHomeResponse(version, true, RequestMethod.GET, headers);
    }
    private String updateHomeResponse(APIVersion version, boolean isUpdate, RequestMethod method, HashMap<String, String> headers) {
        final long started = System.currentTimeMillis();
        if(!isUpdate) {
            final long interval = UpdateIntervals.Proxy.HOME;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateHomeResponse(version, true, method, headers);
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
        new ParallelStream<Map.Entry<String, String>>().stream(requests.entrySet(), entry -> {
            final String key = entry.getKey(), serverIP = entry.getValue();
            final String value;
            switch (key) {
                case "trending":
                    final JSONObject json = Statistics.INSTANCE.getTrendingJSON();
                    value = json == null ? null : json.toString();
                    break;
                default:
                    value = request(serverIP, method, headers, null);
                    break;
            }
            if(value != null) {
                try {
                    final JSONObject json = new JSONObject(value);
                    values.put(key, json);
                } catch (Exception e) {
                    final String details = "isUpdate=" + isUpdate + ";string!=null;key=" + key + ";server=" + serverIP + "\n\nvalue=" + value + "\n\n" + WLUtilities.getExceptionStackTrace(e);
                    WLUtilities.saveLoggedError("TargetServer", "failed to parse string to JSONObject! " + details);
                }
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
        WLLogger.logInfo("TargetServer - " + (isUpdate ? "auto-" : "") + "updated " + versionName + " home responses (took " + WLUtilities.getElapsedTime(started) + ")");
        return json.toString();
    }

    public static TargetServer valueOfBackendID(String backendID) {
        final TargetServer server = BACKEND_IDS.get(backendID);
        if(server == null) {
            WLLogger.logError("TargetServer", "failed to find server with backendID \"" + backendID + "\"!");
        }
        return server;
    }
}
