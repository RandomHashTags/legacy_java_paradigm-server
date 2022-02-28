package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum TargetServer implements RestAPI, DataValues {
    COUNTRIES,
    ENVIRONMENT,
    FEEDBACK,
    LAWS,
    NEWS,
    PREMIUM,
    PROXY,
    REMOTE_NOTIFICATIONS,
    SCIENCE,
    SERVICES,
    TECHNOLOGY,
    UPCOMING_EVENTS,
    WEATHER,

    COMBINE,
    ;


    private static final HashMap<String, TargetServer> BACKEND_IDS;

    static {
        BACKEND_IDS = new HashMap<>();
        for(TargetServer server : TargetServer.values()) {
            BACKEND_IDS.put(server.getBackendID(), server);
        }
    }

    private String ipAddressCache;
    private int port;
    private APIVersion apiVersion;

    public String getIpAddress() {
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
            case PROXY: return 0;
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
    public void updateAddressDetails() {
        if(isRealServer()) {
            port = Settings.Server.getPort(this);
            final String ip = Settings.Server.getAddress(this);
            ipAddressCache = ip + ":" + port;
        }
    }
    public boolean isRealServer() {
        switch (this) {
            case COMBINE:
                return false;
            default:
                return true;
        }
    }
    public boolean recordsStatistics() {
        if(isRealServer()) {
            switch (this) {
                case PROXY:
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
            default:
                return handleResponse(version, identifier, method, request, query);
        }
    }

    public String handleResponse(APIVersion version, String identifier, RequestMethod method, String request, HashSet<String> query) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", identifier);
        switch (this) {
            case COMBINE:
                return getCombinedResponse(version, identifier, method, request);
            default:
                return handleProxyResponse(version, method, request, headers);
        }
    }

    private String getCombinedResponse(APIVersion version, String identifier, RequestMethod method, String request) {
        final String[] values = request.split("&&");
        final ConcurrentHashMap<String, String> responses = new ConcurrentHashMap<>();
        new CompletableFutures<String>().stream(Arrays.asList(values), value -> {
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

    public static TargetServer valueOfBackendID(String backendID) {
        return BACKEND_IDS.get(backendID);
    }
}
