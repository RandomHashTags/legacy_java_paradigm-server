package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;
import me.randomhashtags.worldlaws.proxy.ClientHeaders;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface WLServer extends DataValues, Jsoupable, Jsonable {
    ConcurrentHashMap<APIVersion, JSONObjectTranslatable> CACHED_HOME_RESPONSES = new ConcurrentHashMap<>();
    HashMap<TargetServer, LocalServer> LOCAL_SERVERS = new HashMap<>();
    TargetServer getServer();
    ServerRequestType[] getRequestTypes();
    default WLHttpHandler getDefaultHandler() {
        return null;
    }
    /*default WLHttpHandler getServerHandler() {
        return httpExchange -> {
            final String fullRequest = httpExchange.getPath(), identifier = httpExchange.getIdentifier();
            final String[] values = fullRequest.split("/");
            final APIVersion version = httpExchange.getAPIVersion();
            final Language clientLanguage = httpExchange.getLanguage();
            final LanguageTranslator translator = httpExchange.getLanguageType();
            final JSONObject json;
            if(values.length >= 2) {
                switch (values[1]) {
                    case "home":
                        final JSONObjectTranslatable response = getHomeResponse(version);
                        json = WLUtilities.translateJSON(response, translator, clientLanguage);
                        return json;
                    case "stop":
                        if(identifier.equals(Settings.Server.getUUID())) {
                            getLocalServer().stop();
                            return "1";
                        } else {
                            return null;
                        }
                    default:
                        final LocalServer localServer = getLocalServer();
                        localServer.madeRequest(identifier, fullRequest);
                        final String targetType = httpExchange.getTargetRequestType();
                        final ServerRequestType type = localServer.parseRequestType(targetType);
                        final ServerRequest request = new ServerRequest(type, httpExchange.getShortPath());
                        request.setHeaders(headers);
                        final JSONTranslatable serverJSON = getServerResponse(version, identifier, request);
                        json = WLUtilities.translateJSON(serverJSON, translator, clientLanguage);
                        return json != null ? json.toString() : null;
                }
            }
            return null;
        };
    }*/

    default LocalServer getLocalServer() {
        final TargetServer server = getServer();
        if(LOCAL_SERVERS.containsKey(server)) {
            return LOCAL_SERVERS.get(server);
        } else {
            final LocalServer localServer = new LocalServer(this);
            LOCAL_SERVERS.put(server, localServer);
            return localServer;
        }
    }
    default void registerFixedTimer(long interval, Runnable runnable) {
        getLocalServer().registerFixedTimer(interval, runnable);
    }
    default void registerFixedTimer(LocalDateTime startingDate, long interval, Runnable runnable) {
        getLocalServer().registerFixedTimerStartingAt(startingDate, interval, runnable);
    }

    default void load() {
        startServer();
    }
    default void startServer() {
        final LocalServer localServer = getLocalServer();
        localServer.start();
    }
    default void stop() {
    }

    private String getResponse(LocalServer localServer, ClientHeaders headers) {
        final String fullRequest = headers.getFullRequest(), identifier = headers.getIdentifier();
        final String[] values = fullRequest.split("/");
        final APIVersion version = headers.getAPIVersion();
        final Language clientLanguage = headers.getLanguage();
        final LanguageTranslator translator = headers.getLanguageType();
        final JSONObject json;
        if(values.length >= 2) {
            switch (values[1]) {
                case "home":
                    final JSONObjectTranslatable response = getHomeResponse(version);
                    json = WLUtilities.translateJSON(response, translator, clientLanguage);
                    return json != null ? json.toString() : null;
                case "stop":
                    if(identifier.equals(Settings.Server.getUUID())) {
                        localServer.stop();
                        return "1";
                    } else {
                        return null;
                    }
                default:
                    localServer.madeRequest(identifier, fullRequest);
                    final String targetType = headers.getTargetRequestType();
                    final ServerRequestType type = localServer.parseRequestType(targetType);
                    final ServerRequest request = new ServerRequest(type, headers.getRequest());
                    request.setHeaders(headers);
                    final JSONTranslatable serverJSON = getServerResponse(version, identifier, request);
                    json = WLUtilities.translateJSON(serverJSON, translator, clientLanguage);
                    return json != null ? json.toString() : null;
            }
        }
        return null;
    }

    JSONTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request);
    default long getHomeResponseUpdateInterval() {
        return 0;
    }
    default ServerRequest[] getHomeRequests() {
        return null;
    }
    default JSONObjectTranslatable getHomeResponse(APIVersion version) {
        if(CACHED_HOME_RESPONSES.containsKey(version)) {
            return CACHED_HOME_RESPONSES.get(version);
        } else {
            final JSONObjectTranslatable string = refreshHome(version);
            tryStartingAutoUpdates(version);
            return string;
        }
    }
    private void tryStartingAutoUpdates(APIVersion version) {
        final long updateInterval = getHomeResponseUpdateInterval();
        if(updateInterval > 0) {
            final TargetServer server = getServer();
            final String serverName = server.getName(), simpleName = getClass().getSimpleName();
            registerFixedTimer(updateInterval, () -> {
                final long started = System.currentTimeMillis();
                autoRefreshHome(simpleName, started, serverName, version);
            });
        }
    }
    default JSONObjectTranslatable autoRefreshHome(String simpleName, long started, String serverName, APIVersion version) {
        final JSONObjectTranslatable string = refreshHome(version);
        WLLogger.logInfo(simpleName + " - auto updated \"" + serverName + "\"'s home response (took " + WLUtilities.getElapsedTime(started) + ")");
        return string;
    }
    private JSONObjectTranslatable refreshHome(APIVersion version) {
        final ServerRequest[] requests = getHomeRequests();
        if(requests == null) {
            CACHED_HOME_RESPONSES.put(version, new JSONObjectTranslatable());
            return null;
        } else {
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            final String serverUUID = Settings.Server.getUUID();
            new CompletableFutures<ServerRequest>().stream(Arrays.asList(requests), request -> {
                final JSONTranslatable response = getServerResponse(version, serverUUID, request);
                if(response != null) {
                    if(response instanceof JSONObjectTranslatable) {
                        ((JSONObjectTranslatable) response).remove("locale");
                    }
                    final String path = request.getTotalPath();
                    json.put(path, response);
                    json.addTranslatedKey(path);
                }
            });
            CACHED_HOME_RESPONSES.put(version, json);
            return json;
        }
    }
    default void insertInHomeResponse(APIVersion version, String totalPath, JSONTranslatable response) {
        CACHED_HOME_RESPONSES.putIfAbsent(version, new JSONObjectTranslatable());
        CACHED_HOME_RESPONSES.get(version).put(totalPath, response);
    }
}
