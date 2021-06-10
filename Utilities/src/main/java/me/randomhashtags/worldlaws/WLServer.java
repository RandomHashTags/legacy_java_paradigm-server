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
        final TargetServer server = getServer();
        LocalServer.start(server.getName(), server.getPort(), new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object != null ? object.toString() : null;
                        client.sendResponse(string);
                    }
                });
            }
        });
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
                handler.handle(true);
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
            handler.handle(map.get(version));
        } else {
            refreshHome(server, version, handler);
        }
    }
    default void refreshHome(APIVersion version, CompletionHandler handler) {
        refreshHome(getServer(), version, handler);
    }
    private void refreshHome(TargetServer server, APIVersion version, CompletionHandler handler) {
        final String[] requests = getHomeRequests();
        final int max = requests.length;
        final HashSet<String> values = new HashSet<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(requests).parallelStream().forEach(request -> {
            getServerResponse(version, request, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(object != null) {
                        final String target = "\"" + request + "\":" + object.toString();
                        values.add(target);
                    }
                    if(completed.addAndGet(1) == max) {
                        String string = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirst = true;
                            for(String value : values) {
                                builder.append(isFirst ? "" : ",").append(value);
                                isFirst = false;
                            }
                            builder.append("}");
                            string = builder.toString();
                        }
                        CACHED_HOME_RESPONSES.get(server).put(version, string);
                        handler.handle(string);
                    }
                }
            });
        });
    }
}
