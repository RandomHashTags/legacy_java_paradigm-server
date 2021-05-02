package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    private HashMap<String, String> homeResponses;
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

    public void sendResponse(RequestMethod method, String request, HashSet<String> query, CompletionHandler handler) {
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
                final String targetHomeResponse = "[" + (query.contains("withCountries") ? "withCountries" : "") + "]";
                getHomeResponse(method, headers, targetHomeResponse, handler);
                break;
            default:
                final String url = ipAddress + "/" + request;
                try {
                    request(url, method, headers, null, handler);
                } catch (Exception ignored) {
                    WLLogger.log(Level.WARN, "TargetServer - failed to sendResponse to \"" + url + "\"!");
                }
                break;
        }
    }

    private void getHomeResponse(RequestMethod method, HashMap<String, String> headers, String targetHomeResponse, CompletionHandler handler) {
        if(homeResponses != null) {
            handler.handle(homeResponses.getOrDefault(targetHomeResponse, "{}"));
        } else {
            homeResponses = new HashMap<>();
            updateHomeResponse(false, method, headers, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(homeResponses.get(targetHomeResponse));
                }
            });
        }
    }
    private void updateHomeResponse(boolean isUpdate, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        if(!isUpdate) {
            final long interval = WLUtilities.PROXY_UPDATE_INTERVAL;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final long started = System.currentTimeMillis();
                    updateHomeResponse(true, method, headers, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            WLLogger.log(Level.INFO, "Proxy - TargetServer - updated Home Responses (took " + (System.currentTimeMillis()-started) + "ms)");
                        }
                    });
                }
            }, interval, interval);
        }

        final HashMap<TargetServer, String> urls = new HashMap<>() {{
            put(COUNTRIES, "home");
            put(UPCOMING_EVENTS, "home");
            put(WEATHER, "home");
        }};
        final int max = urls.size();
        final HashMap<String, Object> values = new HashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        urls.keySet().parallelStream().forEach(server -> {
            final String url = urls.get(server), serverBackendID = server.getBackendID();
            final String targetURL = server.ipAddress + "/" + serverBackendID + "/" + url;
            final String serverName = server.name().toLowerCase();
            request(targetURL, method, headers, null, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final int value = completed.addAndGet(1);
                    if(object != null) {
                        values.put(serverName, object);
                    }
                    if(value == max) {
                        loadHomeResponses(values);
                        WLLogger.log(Level.INFO, "TargetServer - updated home responses (took " + (System.currentTimeMillis()-started) + "ms)");
                        if(handler != null) {
                            handler.handle(null);
                        }
                    }
                }
            });
        });
    }

    private void loadHomeResponses(HashMap<String, Object> hashmap) {
        final HashMap<String, StringBuilder> builders = new HashMap<>();
        final String[] keys = { "[]", "[withCountries]" };
        final Set<Map.Entry<String, Object>> set = hashmap.entrySet();
        for(String queryKey : keys) {
            builders.put(queryKey, new StringBuilder("{"));
            for(Map.Entry<String, Object> map : set) {
                final String serverName = map.getKey();
                final Object keyValue = map.getValue();
                final boolean isFirst = builders.get(queryKey).toString().equals("{");
                if(!queryKey.equals("[]") || !serverName.equals("countries")) {
                    builders.get(queryKey).append(isFirst ? "" : ",").append("\"").append(serverName).append("\":").append(keyValue);
                }
            }
            builders.get(queryKey).append("}");
            final String string = builders.get(queryKey).toString();
            homeResponses.put(queryKey, string);
        }
    }

    public static TargetServer valueOfBackendID(String backendID) {
        final Optional<TargetServer> targetServer = Arrays.stream(values()).filter(server -> backendID.equalsIgnoreCase(server.getBackendID())).findFirst();
        if(targetServer.isPresent()) {
            return targetServer.get();
        }
        WLLogger.log(Level.WARN, "TargetServer - failed to find a server with backendID \"" + backendID + "\"!");
        return null;
    }
}
