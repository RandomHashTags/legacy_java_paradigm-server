package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.util.*;
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

    WHATS_NEW,
    HOME,
    STATUS,
    ;

    private static final String WHATS_NEW_RESPONSE;
    private static final HashMap<APIVersion, JSONObject> HOME_JSON;
    private static final HashMap<APIVersion, HashMap<HashSet<String>, String>> HOME_JSONS;

    static {
        WHATS_NEW_RESPONSE = "";
        HOME_JSON = new HashMap<>();
        HOME_JSONS = new HashMap<>();
    }

    private String ipAddress;
    private int port;

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

    public void sendResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query, CompletionHandler handler) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", identifier);
        switch (this) {
            case WHATS_NEW:
                handler.handleString(WHATS_NEW_RESPONSE);
                break;
            case STATUS:
                handler.handleString("0");
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
        if(HOME_JSONS.containsKey(version)) {
            return HOME_JSONS.get(version).containsKey(query) ? HOME_JSONS.get(version).get(query) : loadQueryJSON(version, query);
        } else {
            HOME_JSONS.put(version, new HashMap<>());
            return loadQueryJSON(version, query);
        }
    }
    private String loadQueryJSON(APIVersion version, HashSet<String> query) {
        final String target = getQueryJSON(version, query);
        HOME_JSONS.get(version).put(query, target);
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
            final long interval = WLUtilities.PROXY_UPDATE_INTERVAL;
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
        final int max = servers.length;
        final HashMap<String, Object> values = new HashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(servers).parallelStream().forEach(server -> {
            final String targetURL = server.ipAddress + "/" + versionName + "/home";
            final String serverName = server.name().toLowerCase();
            request(targetURL, method, headers, null, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        values.put(serverName, string);
                    }
                    if(completed.addAndGet(1) == max) {
                        final StringBuilder builder = new StringBuilder("{");
                        builder.append("\"request_epoch\":").append(System.currentTimeMillis());
                        for(Map.Entry<String, Object> map : values.entrySet()) {
                            final String serverName = map.getKey();
                            final String keyValue = map.getValue().toString();
                            builder.append(",").append("\"").append(serverName).append("\":").append(keyValue);
                        }
                        builder.append("}");
                        final String value = builder.toString();
                        HOME_JSON.put(version, new JSONObject(value));
                        WLLogger.log(Level.INFO, "TargetServer - " + (isUpdate ? "auto-" : "") + "updated " + versionName + " home responses (took " + (System.currentTimeMillis()-started) + "ms)");
                        if(handler != null) {
                            handler.handleString(value);
                        }
                    }
                }
            });
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
