package me.randomhashtags.worldlaws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public interface WLServer extends DataValues, Jsoupable, Jsonable {
    HashMap<TargetServer, HashMap<APIVersion, String>> CACHED_HOME_RESPONSES = new HashMap<>();
    TargetServer getServer();

    default void load() {
        startServer();
    }
    default void startServer() {
        final LocalServer localServer = LocalServer.get(getServer());
        final CompletionHandler handler = new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                if(target.startsWith("favicon")) {
                    client.sendResponse(HTTP_ERROR_404);
                    return;
                }
                String identifier = client.getIdentifier();
                if(identifier == null) {
                    identifier = "null";
                }
                localServer.madeRequest(identifier, target);
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        client.sendResponse(string);
                    }
                });
            }
        };
        localServer.setCompletionHandler(handler);
    }

    private void getResponse(String target, CompletionHandler handler) {
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
                getServerResponse(version, target.substring(versionString.length()+1), handler);
                break;
        }
    }
    void getServerResponse(APIVersion version, String target, CompletionHandler handler);
    String[] getHomeRequests();
    private void getHomeResponse(APIVersion version, CompletionHandler handler) { // TODO: auto update home responses
        final TargetServer server = getServer();
        CACHED_HOME_RESPONSES.putIfAbsent(server, new HashMap<>());
        final HashMap<APIVersion, String> map = CACHED_HOME_RESPONSES.get(server);
        if(map.containsKey(version)) {
            handler.handleString(map.get(version));
        } else {
            refreshHome(server, version, handler);
        }
    }
    default void refreshHome(APIVersion version, CompletionHandler handler) {
        refreshHome(getServer(), version, handler);
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
