package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.server.*;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public interface WLServer extends DataValues, Jsoupable, Jsonable {
    ConcurrentHashMap<TargetServer, HashMap<APIVersion, String>> CACHED_HOME_RESPONSES = new ConcurrentHashMap<>();
    HashMap<TargetServer, LocalServer> LOCAL_SERVERS = new HashMap<>();
    TargetServer getServer();
    default ServerRequestType[] getRequestTypes() {
        final TargetServer server = getServer();
        if(server != null) {
            switch (server) {
                case COUNTRIES: return ServerRequestTypeCountries.values();
                case FEEDBACK: return ServerRequestTypeFeedback.values();
                case LAWS: return ServerRequestTypeLaws.values();
                case PREMIUM: return ServerRequestTypePremium.values();
                case REMOTE_NOTIFICATIONS: return ServerRequestTypeRemoteNotifications.values();
                case SERVICES: return ServerRequestTypeServices.values();
                case SCIENCE: return ServerRequestTypeScience.values();
                case UPCOMING_EVENTS: return ServerRequestTypeUpcomingEvents.values();
                case WEATHER: return ServerRequestTypeWeather.values();
                default: return null;
            }
        }
        return null;
    }

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
        final CompletionHandler handler = new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                if(target == null) {
                    return;
                } else if(target.equals("ping")) {
                    client.sendResponse("1");
                    return;
                }
                if(target.startsWith("favicon")) {
                    client.sendResponse(null);
                    return;
                }
                final String identifier = client.getIdentifier();
                final String string = getResponse(localServer, identifier, target);
                client.sendResponse(string);
            }
        };
        localServer.setCompletionHandler(handler);
    }
    default void stop() {
    }

    private String getResponse(LocalServer localServer, String identifier, String target) {
        final String[] values = target.split("/");
        final String versionString = values[0];
        final APIVersion version = APIVersion.valueOfInput(versionString);
        switch (values[1]) {
            case "home":
                return getHomeResponse(version);
            case "stop":
                if(identifier.equals(Settings.Server.getUUID())) {
                    localServer.stop();
                    return "1";
                } else {
                    return null;
                }
            default:
                localServer.madeRequest(identifier, target);
                String requestTarget = target.substring(versionString.length() + 1);
                final String targetType = requestTarget.split("/")[0];
                final ServerRequestType type = localServer.parseRequestType(targetType);
                if(type != null) {
                    requestTarget = requestTarget.substring(targetType.length() + (requestTarget.contains("/") ? 1 : 0));
                }
                final ServerRequest request = new ServerRequest(type, requestTarget);
                String string = getServerResponse(version, identifier, request);
                if(string == null || string.isEmpty()) {
                    string = null;
                }
                return string;
        }
    }
    String getServerResponse(APIVersion version, String identifier, ServerRequest request);
    default long getHomeResponseUpdateInterval() {
        return 0;
    }
    default ServerRequest[] getHomeRequests() {
        return null;
    }
    default String getHomeResponse(APIVersion version) {
        final TargetServer server = getServer();
        CACHED_HOME_RESPONSES.putIfAbsent(server, new HashMap<>());
        final HashMap<APIVersion, String> map = CACHED_HOME_RESPONSES.get(server);
        if(map.containsKey(version)) {
            return map.get(version);
        } else {
            final String string = refreshHome(server, version);
            tryStartingAutoUpdates(server, version);
            return string;
        }
    }
    private void tryStartingAutoUpdates(TargetServer server, APIVersion version) {
        final long updateInterval = getHomeResponseUpdateInterval();
        if(updateInterval > 0) {
            final String serverName = server.getName(), simpleName = getClass().getSimpleName();
            registerFixedTimer(updateInterval, () -> {
                final long started = System.currentTimeMillis();
                autoRefreshHome(simpleName, started, serverName, server, version);
            });
        }
    }
    default String autoRefreshHome(String simpleName, long started, String serverName, TargetServer server, APIVersion version) {
        final String string = refreshHome(server, version);
        WLLogger.logInfo(simpleName + " - auto updated \"" + serverName + "\"'s home response (took " + WLUtilities.getElapsedTime(started) + ")");
        return string;
    }
    private String refreshHome(TargetServer server, APIVersion version) {
        final ServerRequest[] requests = getHomeRequests();
        if(requests == null) {
            CACHED_HOME_RESPONSES.get(server).put(version, null);
            return null;
        } else {
            final HashSet<String> values = new HashSet<>();
            final String serverUUID = Settings.Server.getUUID();
            new CompletableFutures<ServerRequest>().stream(Arrays.asList(requests), request -> {
                final String string = getServerResponse(version, serverUUID, request);
                if(string != null) {
                    final String target = "\"" + request.getTotalPath() + "\":" + string;
                    values.add(target);
                }
            });

            String value = null;
            if(!values.isEmpty()) {
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(String stringValue : values) {
                    builder.append(isFirst ? "" : ",").append(stringValue);
                    isFirst = false;
                }
                builder.append("}");
                value = builder.toString();
            }
            CACHED_HOME_RESPONSES.get(server).put(version, value);
            return value;
        }
    }
}
