package me.randomhashtags.worldlaws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public enum TargetServer implements DataValues, RestAPI {
    COUNTRIES("countries", WL_COUNTRIES_SERVER_IP),
    FEEDBACK("feedback", WL_FEEDBACK_SERVER_IP),
    LAWS("laws", WL_LAWS_SERVER_IP),
    NEWS("news", WL_NEWS_SERVER_IP),
    TECHNOLOGY("technology", WL_TECHNOLOGY_SERVER_IP),
    UPCOMING_EVENTS("upcomingevents", WL_UPCOMING_EVENTS_SERVER_IP),
    WEATHER("weather", WL_WEATHER_SERVER_IP),

    WHATS_NEW(null, null),
    HOME(null, null),
    STATUS(null, null),
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
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        switch (this) {
            case WHATS_NEW:
                handler.handle(WHATS_NEW_RESPONSE);
                break;
            case STATUS:
                handler.handle("0");
                break;
            case HOME:
                final HashMap<TargetServer, String> urls = new HashMap<>() {{
                    put(COUNTRIES, "countries");
                    put(UPCOMING_EVENTS, "home");
                    put(WEATHER, "alerts/all");
                }};
                final int max = urls.size();
                final StringBuilder builder = new StringBuilder("{");
                final AtomicInteger completed = new AtomicInteger(0);
                urls.keySet().parallelStream().forEach(server -> {
                    final String url = urls.get(server), serverBackendID = server.getBackendID();
                    final String targetURL = server.ipAddress + "/" + serverBackendID + "/" + url;
                    request(targetURL, method, headers, null, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final int value = completed.addAndGet(1);
                            builder.append(value == 1 ? "" : ",").append("\"").append(server.name().toLowerCase()).append("\":").append(object);
                            if(value == max) {
                                builder.append("}");
                                handler.handle(builder.toString());
                            }
                        }
                    });
                });
                break;
            default:
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
