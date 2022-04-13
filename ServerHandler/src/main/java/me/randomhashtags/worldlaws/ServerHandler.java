package me.randomhashtags.worldlaws;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class ServerHandler implements UserServer {
    private static final ServerHandler INSTANCE = new ServerHandler();
    private static final HashMap<APIVersion, JSONObject> HOME_JSON = new HashMap<>();
    private static final HashMap<APIVersion, HashMap<Collection<String>, String>> HOME_JSON_QUERIES = new HashMap<>();

    private static String PING_RESPONSE, MAINTENANCE_MESSAGE;
    private static boolean MAINTENANCE_MODE = false;
    private static long MAINTENANCE_STARTED;

    private HttpServer server;
    private final HashSet<Timer> timers;

    public static void main(String[] args) {
        INSTANCE.start();
    }

    private ServerHandler() {
        timers = new HashSet<>();
    }

    @Override
    public TargetServer getTargetServer() {
        return null;
    }

    @Override
    public void start() {
        final LocalDateTime now = LocalDateTime.now();

        final long rebootFrequency = Settings.Server.getServerRebootFrequencyInDays();
        final long rebootInterval = TimeUnit.DAYS.toMillis(rebootFrequency);
        final LocalDateTime rebootStartingDate = now.plusDays(rebootFrequency).withHour(0).withMinute(0).withSecond(5);
        final Timer rebootTimer = WLUtilities.getTimer(rebootStartingDate, rebootInterval, ServerStatuses::rebootServers);
        timers.add(rebootTimer);

        final long updateServersInterval = TimeUnit.DAYS.toMillis(1);
        final LocalDateTime updateServersStartingDay = now.plusDays(1).withHour(0).withMinute(0).withSecond(1);
        final Timer updateServersTimer = WLUtilities.getTimer(updateServersStartingDay, updateServersInterval, ServerStatuses::tryUpdatingServersIfAvailable);
        timers.add(updateServersTimer);

        setupServer();
    }

    @Override
    public void stop() {
        for(Timer timer : timers) {
            timer.cancel();
        }
        stopListeningForUserInput();
        server.stop(0);
        WLLogger.logInfo("ServerHandler - stopped listening for clients");
    }

    private void setupServer() {
        listenForUserInput();

        final int port = Settings.Server.getServerHandlerPort();
        final boolean https = Settings.DataValues.isProductionMode() && Settings.Server.isHttpsEnabled();
        try {
            if(https) {
                setupHttpsServer(port);
            } else {
                setupHttpServer(port);
            }
            connectClients(https);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void setupHttpServer(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }
    private void setupHttpsServer(int port) throws Exception {
        final KeyStore store = KeyStore.getInstance("JKS");
        final char[] password = Settings.Server.getHttpsKeystorePassword().toCharArray();

        final String keystoreFileName = Settings.Server.getHttpsKeystoreFileName();
        final FileInputStream file = new FileInputStream(keystoreFileName);
        store.load(file, password);

        final String factoryType = "SunX509";
        final KeyManagerFactory factory = KeyManagerFactory.getInstance(factoryType);
        factory.init(store, password);

        final TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(factoryType);
        trustFactory.init(store);

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(factory.getKeyManagers(), trustFactory.getTrustManagers(), null);

        final HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(context) {
            @Override
            public void configure(HttpsParameters params) {
                try {
                    final SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(true);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    final SSLParameters sslParameters = context.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
            }
        });
    }

    private void connectClients(boolean https) {
        WLLogger.logInfo("ServerHandler - Listening for http" + (https ? "s" : "") + " clients on port " + server.getAddress().getPort() + "...");
        server.createContext("/", new WLHttpHandler() {
            @Override
            public JSONTranslatable getResponse(WLHttpExchange httpExchange) {
                return null;
            }

            @Override
            public String getFallbackResponse(WLHttpExchange httpExchange) {
                final String path = httpExchange.getPath(), key = path.split("/")[1];
                switch (key) {
                    case "ping":
                        return getPingResponse();
                    case "home":
                        final LinkedHashMap<String, String> query = httpExchange.getQuery();
                        final Collection<String> queryCollection = query != null && query.containsKey("q") ? Arrays.asList(query.get("q").split("&")) : new HashSet<>();
                        return getHomeResponse(httpExchange.getAPIVersion(), queryCollection);
                    default:
                        if(MAINTENANCE_MODE) {
                            return DataValues.HTTP_MAINTENANCE_MODE;
                        } else {
                            return TargetServer.PROXY.sendResponse(httpExchange);
                        }
                }
            }
        });
        server.setExecutor(null);
        server.start();
    }

    @Override
    public HashMap<String, Runnable> getCustomUserCommands() {
        final HashMap<String, Runnable> map = new HashMap<>();
        map.put("startmaintenance", () -> setMaintenanceMode(true, "Manual updates in progress, please wait a few minutes :)"));
        map.put("endmaintenance", () -> setMaintenanceMode(false, null));
        map.put("shutdown", ServerStatuses::shutdownServers);
        map.put("spinup", ServerStatuses::spinUpServers);
        map.put("reboot", ServerStatuses::rebootServers);
        map.put("restart", ServerStatuses::rebootServers);
        map.put("update", ServerStatuses::tryUpdatingServersIfAvailable);
        return map;
    }

    private String getPingResponse() {
        if(PING_RESPONSE == null) {
            final long interval = UpdateIntervals.ServerHandler.PING;
            final Timer timer = WLUtilities.getTimer(null, interval, ServerHandler::updatePingResponse);
            timers.add(timer);

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
                final String string = server.request(url, null, null);
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

    public static void setMaintenanceMode(boolean active, String reason) {
        if(MAINTENANCE_MODE == active) {
            return;
        }
        final long now = System.currentTimeMillis();
        WLLogger.logInfo("ServerHandler - " + (active ? "started" : "ended") + " maintenance mode" + (active ? "" : " (active for " + (now- MAINTENANCE_STARTED) + "ms)"));
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

    private String getHomeResponse(APIVersion version, Collection<String> query) {
        if(!HOME_JSON.containsKey(version)) {
            final String string = updateHomeResponse(version, false);
        }
        if(HOME_JSON_QUERIES.containsKey(version)) {
            return HOME_JSON_QUERIES.get(version).containsKey(query) ? HOME_JSON_QUERIES.get(version).get(query) : loadQueryJSON(version, query);
        } else {
            HOME_JSON_QUERIES.put(version, new HashMap<>());
            return loadQueryJSON(version, query);
        }
    }
    private String loadQueryJSON(APIVersion version, Collection<String> query) {
        final String target = getQueryJSON(version, query);
        HOME_JSON_QUERIES.get(version).put(query, target);
        return target;
    }
    private String getQueryJSON(APIVersion version, Collection<String> query) {
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

    public static String updateHomeResponse() {
        return updateHomeResponse(APIVersion.getLatest(), true);
    }
    private static String updateHomeResponse(APIVersion version, boolean isUpdate) {
        final long started = System.currentTimeMillis();

        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", Settings.Server.getUUID());
        headers.put("***REMOVED***", "***REMOVED***");
        headers.put("***REMOVED***", version.name());
        if(!isUpdate) {
            final long interval = UpdateIntervals.ServerHandler.HOME;
            final Timer timer = WLUtilities.getTimer(null, interval, () -> updateHomeResponse(version, true));
            INSTANCE.timers.add(timer);
        }

        final String versionName = version.name();
        final TargetServer[] servers = {
                TargetServer.COUNTRIES,
                TargetServer.SERVICES,
                TargetServer.UPCOMING_EVENTS,
                TargetServer.WEATHER
        };
        final HashMap<String, String> requests = new HashMap<>();
        requests.put("trending", null);
        for(TargetServer server : servers) {
            requests.put(server.name().toLowerCase(), server.getIpAddress() + "/" + versionName + "/home");
        }

        final ConcurrentHashMap<String, JSONObject> values = new ConcurrentHashMap<>();
        new CompletableFutures<Map.Entry<String, String>>().stream(requests.entrySet(), entry -> {
            final String key = entry.getKey(), serverIP = entry.getValue();
            final JSONObject value;
            switch (key) {
                case "trending":
                    value = Statistics.INSTANCE.getTrendingJSON();
                    break;
                default:
                    value = RestAPI.requestStaticJSONObject(serverIP, headers, null);
                    break;
            }
            if(value != null && !value.isEmpty()) {
                values.put(key, value);
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
        return json.toString();
    }
}
