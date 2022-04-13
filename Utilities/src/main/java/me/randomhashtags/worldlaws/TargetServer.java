package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public String sendResponseFromProxy(WLHttpExchange headers) {
        return sendResponse(headers, true);
    }
    public String sendResponse(WLHttpExchange headers) {
        return sendResponse(headers, false);
    }
    private String sendResponse(WLHttpExchange headers, boolean fromProxy) {
        return sendResponse(headers.getIdentifier(), headers.getPlatform(), headers.getVersion(), headers.getAPIVersion(), fromProxy ? headers.getShortPath() : headers.getPath(), fromProxy);
    }
    public String sendResponse(String identifier, String platform, String version, APIVersion apiVersion, String path, boolean fromProxy) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", identifier);
        headers.put("***REMOVED***", platform);
        headers.put("***REMOVED***", version);
        if(fromProxy) {
            path = apiVersion.name() + "/" + path;
        }
        switch (this) {
            case COMBINE:
                return getCombinedResponse(null);
            default:
                return handleProxyResponse(path, headers);
        }
    }

    private String getCombinedResponse(WLHttpExchange headers) {
        final String[] values = headers.getShortPath().split("&&");
        final ConcurrentHashMap<String, String> responses = new ConcurrentHashMap<>();
        new CompletableFutures<String>().stream(Arrays.asList(values), value -> {
            final String[] target = value.split("/");
            final String serverBackendID = target[1];
            final TargetServer server = TargetServer.valueOfBackendID(serverBackendID);
            final String string = server != null ? server.sendResponse(headers) : null;
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
    private String handleProxyResponse(String url, LinkedHashMap<String, String> headers) {
        final String targetURL = getIpAddress() + "/" + url;
        return request(targetURL, headers, null);
    }

    public static TargetServer valueOfBackendID(String backendID) {
        return BACKEND_IDS.get(backendID);
    }
}
