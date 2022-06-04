package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.WLContentType;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentHashMap;

public interface ProxyHttpHandler extends WLHttpHandler {

    String DOMAIN = "***REMOVED***";
    ConcurrentHashMap<String, WebsiteResponse> WEBSITE_CACHE = new ConcurrentHashMap<>();

    @Override
    default void handleWLHttpExchange(WLHttpExchange exchange) {
        final String host = exchange.getHeader("Host", "null"), localIP = exchange.getIPAddress(false);
        final String pathKey = exchange.getPathValues()[0];
        final boolean isLocal = localIP.equals("/0:0:0:0:0:0:0:1");
        final boolean isWebsite = host.equals(DOMAIN) || isLocal && pathKey.equals("html");
        final boolean isAPI = host.equals("api." + DOMAIN) || isLocal && pathKey.matches("v[0-9]+");
        final boolean isSandbox = host.equals("sandbox." + DOMAIN) || isLocal && pathKey.equals("sandbox");
        if(isAPI) {
            handleAPIExchange(exchange);
        } else if(isWebsite) {
            handleHTMLExchange(exchange, exchange.getPath());
        } else {
            handleErrorExchange(exchange);
        }
    }

    private void handleHTMLExchange(WLHttpExchange exchange, String path) {
        final WebsiteResponse response;
        if(WEBSITE_CACHE.containsKey(path)) {
            response = WEBSITE_CACHE.get(path);
        } else {
            response = new WebsiteResponse(path);
            WEBSITE_CACHE.put(path, response);
        }
        write(exchange, HttpURLConnection.HTTP_OK, response.getResponse(), response.getContentType());
    }
    private void handleErrorExchange(WLHttpExchange exchange) {
        write(exchange, HttpURLConnection.HTTP_FORBIDDEN, "<!DOCTYPE html><html><body><h1>Error 403. Forbidden.</h1></body></html>", WLContentType.HTML);
    }
    static String getHTMLErrorMsg(int status) {
        final String msg = getHTMLErrorMessage(status);
        return "<html><body><h1>" + msg + "</h1></body></html>";
    }
    private static String getHTMLErrorMessage(int status) {
        switch (status) {
            case HttpURLConnection.HTTP_NOT_FOUND: return "Error 404. Destination not found.";
            case HttpURLConnection.HTTP_INTERNAL_ERROR: return "Error 500. Something went wrong server-side.";
            default: return "Error " + status + " occurred, no prepared error message to display.";
        }
    }
}
