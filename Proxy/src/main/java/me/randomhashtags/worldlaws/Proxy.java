package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.proxy.ProxyHeaders;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.settings.Settings;

import java.net.Socket;
import java.util.HashSet;

public final class Proxy implements WLServer {

    public static void main(String[] args) {
        new Proxy();
    }

    private Proxy() {
        //test();
        load();
    }

    private void test() {
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.PROXY;
    }

    @Override
    public void startServer() {
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
                final String string = getProxyResponse(client, localServer, identifier, target);
                client.sendResponse(string);
            }
        };
        localServer.setCompletionHandler(handler);
    }

    private String getProxyResponse(WLClient client, LocalServer localServer, String identifier, String target) {
        final String[] values = target.split("/");
        switch (values[1]) {
            case "stop":
                if(identifier.equals(Settings.Server.getUUID())) {
                    localServer.stop();
                    return "1";
                } else {
                    return null;
                }
            default:
                return getProxyClientResponse(client.getClient());
        }
    }
    private String getProxyClientResponse(Socket client) {
        final long started = System.currentTimeMillis();
        final ProxyHeaders headers = ProxyHeaders.getFrom(client);
        final String identifier = headers.getIdentifier(), platform = headers.getPlatform();
        final String ip = headers.getIPAddress();
        final String prefix = "[" + platform + ", " + identifier + "] " + ip + " - ";
        final TargetServer server = headers.getServer();
        if(server != null) {
            final APIVersion version = headers.getAPIVersion();
            final HashSet<String> query = headers.getQuery();
            final String request = headers.getRequest();
            final String string = server.sendResponse(version, identifier, RequestMethod.GET, request, query);
            if(string != null && !string.equals("null")) {
                return string;
            }
            WLLogger.logWarning(prefix + "Failed to connect to \"" + request + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
            return null;
        }
        WLLogger.logWarning(prefix + "INVALID");
        return null;
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        return null;
    }
}
