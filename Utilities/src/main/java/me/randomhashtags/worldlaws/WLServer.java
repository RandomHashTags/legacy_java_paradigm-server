package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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

    default long getHomeResponseUpdateInterval() {
        return 0;
    }
    default ServerRequest[] getHomeRequests() {
        return null;
    }


    default JSONObjectTranslatable autoRefreshHome(String simpleName, long started, String serverName, APIVersion version) {
        final String serverUUID = Settings.Server.getUUID();
        final JSONObjectTranslatable string = refreshHome(version, serverUUID, "***REMOVED***" + serverUUID, version.name());
        WLLogger.logInfo(simpleName + " - auto updated \"" + serverName + "\"'s home response (took " + WLUtilities.getElapsedTime(started) + ")");
        return string;
    }

    default JSONTranslatable makeLocalRequest(APIVersion version, ServerRequest request) {
        return makeLocalRequest(version, getServer().getIpAddress() + "/" + version.name() + "/", request);
    }
    private JSONTranslatable makeLocalRequest(APIVersion version, String prefix, ServerRequest request) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("***REMOVED***", Settings.Server.getUUID());
        headers.put("***REMOVED***", "***REMOVED***");
        headers.put("***REMOVED***", version.name());
        return makeLocalRequest(headers, prefix, request);
    }
    private JSONTranslatable makeLocalRequest(LinkedHashMap<String, String> headers, String prefix, ServerRequest request) {
        final String totalPath = request.getTotalPath();
        final String targetURL = prefix + totalPath;
        final String test = RestAPI.requestStatic(targetURL, headers, null);
        if(test != null) {
            if(test.startsWith("[") && test.endsWith("]")) {
                return new JSONArrayTranslatable(test);
            } else if(test.startsWith("{") && test.endsWith("}")) {
                final JSONObject json = new JSONObject(test);
                return JSONObjectTranslatable.copy(json, true);
            }
        }
        return null;
    }
    default JSONObjectTranslatable refreshHome(APIVersion apiVersion, String identifier, String platform, String version) {
        final ServerRequest[] types = getHomeRequests();
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("***REMOVED***", identifier);
        headers.put("***REMOVED***", platform);
        headers.put("***REMOVED***", version);

        final String prefix = getServer().getIpAddress() + "/" + apiVersion.name() + "/";
        final ConcurrentHashMap<String, JSONTranslatable> map = new ConcurrentHashMap<>();
        new CompletableFutures<ServerRequest>().stream(Arrays.asList(types), type -> {
            final String totalPath = type.getTotalPath();
            final JSONTranslatable test = makeLocalRequest(headers, prefix, type);
            if(test instanceof JSONArrayTranslatable || test instanceof JSONObjectTranslatable) {
                map.put(totalPath, test);
            }
        });

        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(Map.Entry<String, JSONTranslatable> entry : map.entrySet()) {
            final String totalPath = entry.getKey();
            translatable.put(totalPath, entry.getValue());
            translatable.addTranslatedKey(totalPath);
        }
        CACHED_HOME_RESPONSES.put(apiVersion, translatable);
        return translatable;
    }
    default void insertInHomeResponse(APIVersion version, String totalPath, JSONTranslatable response) {
        CACHED_HOME_RESPONSES.putIfAbsent(version, new JSONObjectTranslatable());
        CACHED_HOME_RESPONSES.get(version).put(totalPath, response);
    }
}
