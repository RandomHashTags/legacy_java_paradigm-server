package me.randomhashtags.worldlaws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface WLServer extends DataValues, Jsoupable, Jsonable {
    ConcurrentHashMap<TargetServer, HashMap<APIVersion, String>> CACHED_HOME_RESPONSES = new ConcurrentHashMap<>();
    HashMap<TargetServer, LocalServer> LOCAL_SERVERS = new HashMap<>();
    TargetServer getServer();

    default LocalServer getLocalServer() {
        final TargetServer server = getServer();
        if(LOCAL_SERVERS.containsKey(server)) {
            return LOCAL_SERVERS.get(server);
        } else {
            final LocalServer localServer = LocalServer.get(getServer());
            LOCAL_SERVERS.put(server, localServer);
            return localServer;
        }
    }
    default void registerFixedTimer(long interval, CompletionHandler handler) {
        getLocalServer().registerFixedTimer(interval, handler);
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
                }
                if(target.startsWith("favicon")) {
                    client.sendResponse(HTTP_ERROR_404);
                    return;
                }
                String identifier = client.getIdentifier();
                if(identifier == null) {
                    identifier = "null";
                }
                getResponse(localServer, identifier, target, new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        client.sendResponse(string);
                    }
                });
            }
        };
        localServer.setCompletionHandler(handler);
    }

    private void getResponse(LocalServer localServer, String identifier, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String versionString = values[0];
        final APIVersion version = APIVersion.valueOfInput(versionString);
        switch (values[1]) {
            case "home":
                getHomeResponse(version, handler);
                break;
            case "ping":
                handler.handleString("1");
                break;
            default:
                localServer.madeRequest(identifier, target);
                getServerResponse(version, target.substring(versionString.length() + 1), new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        if(string == null) {
                            string = WLUtilities.SERVER_EMPTY_JSON_RESPONSE;
                        }
                        handler.handleString(string);
                    }
                });
                break;
        }
    }
    void getServerResponse(APIVersion version, String target, CompletionHandler handler);
    AutoUpdateSettings getAutoUpdateSettings();
    String[] getHomeRequests();
    default void getHomeResponse(APIVersion version, CompletionHandler handler) {
        final TargetServer server = getServer();
        CACHED_HOME_RESPONSES.putIfAbsent(server, new HashMap<>());
        final HashMap<APIVersion, String> map = CACHED_HOME_RESPONSES.get(server);
        if(map.containsKey(version)) {
            handler.handleString(map.get(version));
        } else {
            refreshHome(server, version, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    tryStartingAutoUpdates(server, version);
                    handler.handleString(string);
                }
            });
        }
    }
    private void tryStartingAutoUpdates(TargetServer server, APIVersion version) {
        final AutoUpdateSettings settings = getAutoUpdateSettings();
        if(settings != null) {
            final String serverName = server.getName(), simpleName = getClass().getSimpleName();
            final long interval = settings.interval;
            final CompletionHandler autoUpdateHandler = settings.handler;
            registerFixedTimer(interval, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    final long started = System.currentTimeMillis();
                    if(autoUpdateHandler != null) {
                        autoUpdateHandler.handleCompletionHandler(new CompletionHandler() {
                            @Override
                            public void handleObject(Object object) {
                                refreshHome(simpleName, started, serverName, server, version);
                            }
                        });
                    } else {
                        refreshHome(simpleName, started, serverName, server, version);
                    }
                }
            });
        }
    }
    private void refreshHome(String simpleName, long started, String serverName, TargetServer server, APIVersion version) {
        refreshHome(server, version, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                WLLogger.logInfo(simpleName + " - auto updated \"" + serverName + "\"'s home response (took " + (System.currentTimeMillis()-started) + "ms)");
            }
        });
    }
    private void refreshHome(TargetServer server, APIVersion version, CompletionHandler handler) {
        final String[] requests = getHomeRequests();
        if(requests == null) {
            CACHED_HOME_RESPONSES.get(server).put(version, null);
            handler.handleString(null);
        } else {
            final int max = requests.length;
            final HashSet<String> values = new HashSet<>();
            final AtomicInteger completed = new AtomicInteger(0);
            Arrays.asList(requests).parallelStream().forEach(request -> {
                getServerResponse(version, request, new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        if(string != null) {
                            final String target = "\"" + request + "\":" + string;
                            values.add(target);
                        }
                        if(completed.addAndGet(1) == max) {
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
                            handler.handleString(value);
                        }
                    }
                });
            });
        }
    }
}
