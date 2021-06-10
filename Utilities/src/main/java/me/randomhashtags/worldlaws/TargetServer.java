package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum TargetServer implements DataValues, RestAPI {
    COUNTRIES(WL_COUNTRIES_SERVER_IP),
    FEEDBACK(WL_FEEDBACK_SERVER_IP),
    LAWS(WL_LAWS_SERVER_IP),
    NEWS(WL_NEWS_SERVER_IP),
    SERVICES(WL_SERVICES_SERVER_IP),
    TECHNOLOGY(WL_TECHNOLOGY_SERVER_IP),
    UPCOMING_EVENTS(WL_UPCOMING_EVENTS_SERVER_IP),
    WEATHER(WL_WEATHER_SERVER_IP),

    WHATS_NEW,
    HOME,
    STATUS,
    ;

    private HashMap<String, String> homeResponses;
    private static final String WHATS_NEW_RESPONSE;

    static {
        WHATS_NEW_RESPONSE = "";
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
    public String getIPAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }

    public void sendResponse(APIVersion version, RequestMethod method, String request, HashSet<String> query, CompletionHandler handler) {
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
                final String targetHomeResponse = "[" + (query != null && query.contains("withCountries") ? "withCountries" : "") + "]";
                getHomeResponse(version, method, headers, targetHomeResponse, handler);
                break;
            default:
                final String versionName = version.name(), serverName = getBackendID();
                request = request.substring(versionName.length() + serverName.length() + 2);
                final String url = ipAddress + "/" + version.name() + "/" + request;
                try {
                    request(url, method, headers, null, handler);
                } catch (Exception ignored) {
                    WLLogger.log(Level.WARN, "TargetServer - failed to sendResponse to \"" + url + "\"!");
                    handler.handle(null);
                }
                break;
        }
    }

    private void getHomeResponse(APIVersion version, RequestMethod method, HashMap<String, String> headers, String targetHomeResponse, CompletionHandler handler) {
        if(homeResponses != null) {
            handler.handle(homeResponses.getOrDefault(targetHomeResponse, "{}"));
        } else {
            homeResponses = new HashMap<>();
            updateHomeResponse(version, false, method, headers, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(homeResponses.get(targetHomeResponse));
                }
            });
        }
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
                public void handle(Object object) {
                    if(object != null) {
                        values.put(serverName, object);
                    }
                    if(completed.addAndGet(1) == max) {
                        loadHomeResponses(values);
                        WLLogger.log(Level.INFO, "TargetServer - " + (isUpdate ? "auto-" : "") + "updated home responses (took " + (System.currentTimeMillis()-started) + "ms)");
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
            boolean isFirst = true;
            for(Map.Entry<String, Object> map : set) {
                final String serverName = map.getKey();
                final Object keyValue = map.getValue();
                if(!queryKey.equals("[]") || !serverName.equals("countries")) {
                    builders.get(queryKey).append(isFirst ? "" : ",").append("\"").append(serverName).append("\":").append(keyValue);
                }
                isFirst = false;
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
