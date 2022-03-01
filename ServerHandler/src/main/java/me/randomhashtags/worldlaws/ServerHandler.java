package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.proxy.ProxyHeaders;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class ServerHandler implements UserServer {
    private static final ServerHandler INSTANCE = new ServerHandler();
    private static final HashMap<APIVersion, JSONObject> HOME_JSON = new HashMap<>();
    private static final HashMap<APIVersion, HashMap<HashSet<String>, String>> HOME_JSON_QUERIES = new HashMap<>();

    private static String PING_RESPONSE, MAINTENANCE_MESSAGE;
    private static boolean MAINTENANCE_MODE = false;
    private static long MAINTENANCE_STARTED;

    private ServerSocket server;
    private final boolean productionMode;
    private final HashSet<Timer> timers;

    public static void main(String[] args) {
        INSTANCE.start();
    }

    private ServerHandler() {
        productionMode = Settings.DataValues.isProductionMode();
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

        setupServer(false);
    }

    @Override
    public void stop() {
        for(Timer timer : timers) {
            timer.cancel();
        }
        stopListeningForUserInput();
        try {
            server.close();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }

    private void setupServer(boolean https) {
        listenForUserInput();

        final int port = Settings.Server.getServerHandlerPort();
        if(https) {
            setupHttpsServer(port);
        } else {
            setupHttpServer(port);
        }
    }
    private void connectClients(boolean https) {
        WLLogger.logInfo("ServerHandler - Listening for http" + (https ? "s" : "") + " clients on port " + server.getLocalPort() + "...");
        try {
            while (!server.isClosed()) {
                final Socket client = server.accept();
                handleClient(client);
            }
        } catch (Exception e) {
            WLLogger.logInfo("ServerHandler - stopped listening for clients");
        }
    }
    private void handleClient(Socket client) {
        final ProxyHeaders clientHeaders = ProxyHeaders.getFrom(client);
        if(productionMode == clientHeaders.isValidRequest()) {
            final String identifier = clientHeaders.getIdentifier(), totalRequest = clientHeaders.getTotalRequest();
            switch (totalRequest) {
                case "ping":
                    final String pingResponse = getPingResponse();
                    WLUtilities.writeClientOutput(client, DataValues.HTTP_SUCCESS_200 + pingResponse);
                    break;
                case "home":
                    final HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Charset", DataValues.ENCODING.name());
                    headers.put("***REMOVED***", identifier);
                    final String homeResponse = getHomeResponse(clientHeaders.getAPIVersion(), RequestMethod.GET, headers, clientHeaders.getQuery());
                    WLUtilities.writeClientOutput(client, DataValues.HTTP_SUCCESS_200 + homeResponse);
                    break;
                default:
                    if(MAINTENANCE_MODE) {
                        WLUtilities.writeClientOutput(client, DataValues.HTTP_MAINTENANCE_MODE);
                    } else {
                        new Thread(() -> {
                            final String string = TargetServer.PROXY.sendResponse(clientHeaders.getAPIVersion(), identifier, RequestMethod.GET, totalRequest, clientHeaders.getQuery());
                            final String response = string == null ? WLUtilities.SERVER_EMPTY_JSON_RESPONSE : string;
                            WLUtilities.writeClientOutput(client, DataValues.HTTP_SUCCESS_200 + response);
                        }).start();
                    }
                    break;
            }
        } else {
            WLUtilities.writeClientOutput(client, DataValues.HTTP_ERROR_404);
        }
    }

    private void setupHttpServer(int port) {
        try {
            server = new ServerSocket(port);
            connectClients(false);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void setupHttpsServer(int port) {
        try {
            final SSLContext context = getHttpsContext();
            final SSLServerSocketFactory socketFactory = context.getServerSocketFactory();
            server = socketFactory.createServerSocket(port);
            connectClients(true);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private SSLContext getHttpsContext() {
        try {
            final KeyStore store = KeyStore.getInstance("JKS");
            final char[] password = {};//DataValues.HTTPS_KEYSTORE_PASSWORD.toCharArray();
            final String factoryType = "SunX509";
            store.load(new FileInputStream("eli5.keystore"), password);

            final KeyManagerFactory factory = KeyManagerFactory.getInstance(factoryType);
            factory.init(store, password);
            final KeyManager[] keyManagers = factory.getKeyManagers();

            final TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(factoryType);
            trustFactory.init(store);
            final TrustManager[] trustManagers = trustFactory.getTrustManagers();

            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(keyManagers, trustManagers, null);
            return context;
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        return null;
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

    public static String updateHomeResponse() {
        final APIVersion version = APIVersion.getLatest();
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", Settings.Server.getUUID());
        return updateHomeResponse(version, true, RequestMethod.GET, headers);
    }
    private static String updateHomeResponse(APIVersion version, boolean isUpdate, RequestMethod method, HashMap<String, String> headers) {
        final long started = System.currentTimeMillis();
        if(!isUpdate) {
            final long interval = UpdateIntervals.ServerHandler.HOME;
            final Timer timer = WLUtilities.getTimer(null, interval, () -> updateHomeResponse(version, true, method, headers));
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
            final String value;
            switch (key) {
                case "trending":
                    final JSONObject json = Statistics.INSTANCE.getTrendingJSON();
                    value = json == null ? null : json.toString();
                    break;
                default:
                    value = RestAPI.requestStatic(serverIP, method, headers, null);
                    break;
            }
            if(value != null) {
                try {
                    final JSONObject json = new JSONObject(value);
                    values.put(key, json);
                } catch (Exception e) {
                    final String details = "isUpdate=" + isUpdate + ";string!=null;key=" + key + ";server=" + serverIP + "\n\nvalue=" + value + "\n\n" + WLUtilities.getExceptionStackTrace(e);
                    WLUtilities.saveLoggedError("ServerHandler", "failed to parse string to JSONObject! " + details);
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
        WLLogger.logInfo("ServerHandler - " + (isUpdate ? "auto-" : "") + "updated " + versionName + " home responses (took " + WLUtilities.getElapsedTime(started) + ")");
        return json.toString();
    }
}
