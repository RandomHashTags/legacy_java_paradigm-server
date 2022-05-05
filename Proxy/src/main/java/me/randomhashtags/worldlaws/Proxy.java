package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.settings.Settings;

public final class Proxy implements WLServer {

    public static void main(String[] args) {
        new Proxy();
    }

    private Proxy() {
        //test();
        load();
    }

    private void test() {
        //final String uuid = "***REMOVED***";
        //WLLogger.logInfo("Proxy;test;uuid=" + uuid + ";identifierIsValid=" + ProxyHeaders.identifierIsValid(uuid));
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.PROXY;
    }

    /*@Override
    public void startServer() {
        final LocalServer localServer = getLocalServer();
        final CompletionHandler handler = new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final ClientHeaders headers = client.getHeaders();
                final String fullRequest = headers.getFullRequest();
                if(fullRequest == null) {
                    return;
                } else if(fullRequest.equals("ping")) {
                    client.sendResponse("1");
                    return;
                }
                if(fullRequest.startsWith("favicon")) {
                    client.sendResponse(null);
                    return;
                }
                final String string = getProxyResponse(headers, localServer, headers.getTotalRequest());
                client.sendResponse(string);
            }
        };
        localServer.setCompletionHandler(handler);
    }

    private String getProxyResponse(ClientHeaders headers, LocalServer localServer, String totalRequest) {
        final String[] values = totalRequest.split("/");
        if(values.length >= 2) {
            switch (values[1]) {
                case "stop":
                    final String identifier = headers.getIdentifier();
                    if(identifier.equals(Settings.Server.getUUID())) {
                        localServer.stop();
                        return "1";
                    } else {
                        return null;
                    }
                default:
                    break;
            }
        }
        return getProxyClientResponse(headers);
    }*/
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
    public WLHttpHandler getDefaultHandler() {
        final LocalServer localServer = getLocalServer();
        return new WLHttpHandler() {
            @Override
            public JSONTranslatable getResponse(WLHttpExchange httpExchange) {
                return null;
            }

            @Override
            public String getFallbackResponse(WLHttpExchange httpExchange) {
                final String[] values = httpExchange.getPathValues();
                if(values.length >= 2) {
                    switch (values[1]) {
                        case "ping":
                            return "1";
                        case "stop":
                            final String identifier = httpExchange.getIdentifier();
                            if(identifier.equals(Settings.Server.getUUID())) {
                                localServer.stop();
                                return "1";
                            } else {
                                return null;
                            }
                        default:
                            break;
                    }
                }
                return getProxyClientResponse(httpExchange);
            }
        };
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return null;
    }
}
