package me.randomhashtags.worldlaws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;

public enum TargetServer implements DataValues, RestAPI {
    COUNTRIES("countries", WL_COUNTRIES_SERVER_IP),
    FEEDBACK("feedback", WL_FEEDBACK_SERVER_IP),
    LAWS("laws", WL_LAWS_SERVER_IP),
    NEWS("news", WL_NEWS_SERVER_IP),
    STATUS("status", WL_STATUS_SERVER_IP),
    TECHNOLOGY("technology", WL_TECHNOLOGY_SERVER_IP),
    UPCOMING_EVENTS("upcomingevents", WL_UPCOMING_EVENTS_SERVER_IP),
    WEATHER("weather", WL_WEATHER_SERVER_IP),
    WHATS_NEW(null, null),
    ;

    private static final String WHATS_NEW_RESPONSE;

    static {
        WHATS_NEW_RESPONSE = "";
    }

    private final String backendID, ipAddress;
    private int port;

    TargetServer(String backendID, String ipAddress) {
        this.backendID = backendID == null ? name() : backendID;
        this.ipAddress = ipAddress;
        if(ipAddress != null) {
            final String[] values = ipAddress.split(":");
            this.port = Integer.parseInt(values[values.length-1]);
        }
    }

    public void isOnline(CompletionHandler handler) {
        sendResponse(RequestMethod.GET, "ping", handler);
    }
    public String getBackendID() {
        return backendID;
    }
    public String getIPAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }

    public void sendResponse(RequestMethod method, String request, CompletionHandler handler) {
        switch (this) {
            case WHATS_NEW:
                handler.handle(WHATS_NEW_RESPONSE);
                break;
            default:
                final HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Charset", DataValues.ENCODING.name());
                try {
                    request(ipAddress + "/" + request, method, headers, null, handler);
                } catch (Exception ignored) {
                }
                break;
        }
    }

    public static TargetServer valueOfBackendID(String backendID) {
        final Optional<TargetServer> targetServer = Arrays.stream(values()).filter(server -> backendID.equalsIgnoreCase(server.getBackendID())).findFirst();
        if(targetServer.isPresent()) {
            return targetServer.get();
        }
        WLLogger.log(Level.WARNING, "TargetServer - failed to find a server with backendID \"" + backendID + "\"!");
        return null;
    }
}
