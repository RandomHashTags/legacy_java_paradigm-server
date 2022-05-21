package me.randomhashtags.worldlaws;

import com.sun.net.httpserver.HttpServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class Proxy implements UserServer {
    private static final Proxy INSTANCE = new Proxy();
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

    private Proxy() {
        timers = new HashSet<>();
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
        final Timer updateServersTimer = WLUtilities.getTimer(updateServersStartingDay, updateServersInterval, () -> {
            updateServers(true);
        });
        timers.add(updateServersTimer);

        setupServer();
    }

    @Override
    public void stop() {
        for(Timer timer : timers) {
            timer.cancel();
        }
        timers.clear();
        HOME_JSON.clear();
        HOME_JSON_QUERIES.clear();
        server.stop(0);
        stopListeningForUserInput();
        WLLogger.logInfo("Proxy - stopped listening for clients");
    }

    private void setupServer() {
        listenForUserInput();
        final int port = Settings.Server.getProxyPort();
        final boolean https = Settings.DataValues.isProductionMode() && Settings.Server.Https.isEnabled();
        if(https) {
            setupHttpsServer(port);
        } else {
            setupHttpServer(port);
        }
        connectClients(https);
    }
    private void setupHttpServer(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void setupHttpsServer(int port) {
        server = WLUtilities.getHttpsServer(port);
    }

    private void connectClients(boolean https) {
        WLLogger.logInfo("Proxy - Listening for http" + (https ? "s" : "") + " clients on port " + server.getAddress().getPort() + "...");

        server.createContext("/", new ProxyHttpHandler() {
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
                        final Collection<String> queryCollection = query != null && query.containsKey("q") ? Arrays.asList(query.get("q").split(",")) : new HashSet<>();
                        return getHomeResponse(httpExchange.getAPIVersion(), queryCollection);
                    default:
                        if(MAINTENANCE_MODE) {
                            return DataValues.HTTP_MAINTENANCE_MODE;
                        } else {
                            return getProxyClientResponse(httpExchange);
                        }
                }
            }
        });
        server.start();
    }

    private String getProxyClientResponse(WLHttpExchange headers) {
        final long started = System.currentTimeMillis();
        final String ip = headers.getIPAddress(true), platform = headers.getPlatform(), identifier = headers.getIdentifier();
        final String prefix = "[" + platform + ", " + identifier + "] " + ip + " - ";
        final String path = headers.getPath();
        final String targetServer = path.split("/")[1];
        final TargetServer server = TargetServer.valueOfBackendID(targetServer);
        if(server != null) {
            final String string = server.sendResponseFromProxy(headers);
            if(string != null && !string.equals("null")) {
                return string;
            }
            WLLogger.logWarning(prefix + "Failed to connect to \"" + headers.getShortPath() + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
            return null;
        }
        WLLogger.logWarning(prefix + "INVALID - path=\"" + path + "\", server=\"" + targetServer + "\"");
        return null;
    }

    @Override
    public HashMap<String, Runnable> getCustomUserCommands() {
        final HashMap<String, Runnable> map = new HashMap<>();
        map.put("startmaintenance", () -> startMaintenanceMode("Manual updates in progress, please wait a few minutes :)"));
        map.put("endmaintenance", Proxy::endMaintenanceMode);
        map.put("shutdown", ServerStatuses::shutdownServers);
        map.put("spinup", ServerStatuses::spinUpServers);
        map.put("rebootservers", ServerStatuses::rebootServers);
        map.put("update", () -> updateServers(false));
        map.put("applyupdate", ServerStatuses::applyUpdate);
        map.put("generatecertificates", CertbotHandler::generateCertificates);
        map.put("importcertificates", CertbotHandler::importCertificates);
        map.put("renewcertificates", CertbotHandler::renewCertificates);
        return map;
    }

    private void updateServers(boolean cancelTimers) {
        final long started = System.currentTimeMillis();
        final HashSet<Path> updateFiles = ServerStatuses.getUpdateFiles();
        if(!updateFiles.isEmpty()) {
            if(cancelTimers) {
                for(Timer timer : timers) {
                    timer.cancel();
                }
            }
            final boolean updated = ServerStatuses.updateServers(started, updateFiles);
            if(updated) {
                rebootProxy();
            }
        }
    }

    private String getPingResponse() {
        if(PING_RESPONSE == null) {
            final long interval = UpdateIntervals.Proxy.PING;
            final Timer timer = WLUtilities.getTimer(null, interval, Proxy::updatePingResponse);
            timers.add(timer);

            updatePingResponse();
        }
        return PING_RESPONSE;
    }
    private static void updatePingResponse() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("maintenance");

        if(MAINTENANCE_MODE) {
            final JSONObjectTranslatable maintenanceJSON = new JSONObjectTranslatable("msg");
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
        final JSONArray onlineServers = new JSONArray();
        for(TargetServer server : TargetServer.values()) {
            if(server.isRealServer()) {
                final String url = server.getIpAddress() + "/ping";
                final String string = server.request(url, null, null);
                if(string != null) {
                    onlineServers.put(server.getBackendID());
                }
            }
        }
        if(!onlineServers.isEmpty()) {
            json.put("online_servers", onlineServers);
        }
        PING_RESPONSE = json.toString();
    }

    public static void startMaintenanceMode(String reason) {
        if(MAINTENANCE_MODE) {
            return;
        }
        MAINTENANCE_STARTED = System.currentTimeMillis();
        MAINTENANCE_MODE = true;
        MAINTENANCE_MESSAGE = reason;
        WLLogger.logInfo("Proxy - started maintenance mode");
    }
    public static void endMaintenanceMode() {
        if(!MAINTENANCE_MODE) {
            return;
        }
        MAINTENANCE_MODE = false;
        MAINTENANCE_MESSAGE = null;
        updatePingResponse();
        updateDetails();
        WLLogger.logInfo("Proxy - ended maintenance mode (active for " + WLUtilities.getElapsedTime(MAINTENANCE_STARTED) + ")");

        MAINTENANCE_STARTED = 0;
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
    private static String updateHomeResponse(APIVersion apiVersion, boolean isUpdate) {
        final long started = System.currentTimeMillis();
        final String versionName = apiVersion.name(), serverUUID = Settings.Server.getUUID();

        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Charset", DataValues.ENCODING.name());
        headers.put("***REMOVED***", serverUUID);
        headers.put("***REMOVED***", "***REMOVED***" + serverUUID);
        headers.put("***REMOVED***", versionName);
        if(!isUpdate) {
            final long interval = UpdateIntervals.Proxy.HOME;
            final Timer timer = WLUtilities.getTimer(null, interval, () -> updateHomeResponse(apiVersion, true));
            Proxy.INSTANCE.timers.add(timer);
        }

        final HashMap<String, String> requests = new HashMap<>();
        requests.put("trending", null);
        final List<String> supportedServers = Settings.Server.getSupportedHomeRequestServers();
        for(String serverID : supportedServers) {
            final TargetServer server = TargetServer.valueOfInput(serverID);
            if(server != null) {
                requests.put(server.name().toLowerCase(), server.getIpAddress() + "/" + versionName + "/home");
            }
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
        HOME_JSON.put(apiVersion, json);
        HOME_JSON_QUERIES.remove(apiVersion);
        return json.toString();
    }
}
