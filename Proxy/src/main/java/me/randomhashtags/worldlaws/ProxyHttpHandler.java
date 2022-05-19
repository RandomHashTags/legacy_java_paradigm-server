package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.WLContentType;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

import java.io.File;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface ProxyHttpHandler extends WLHttpHandler {

    String DOMAIN = "***REMOVED***";

    @Override
    default void handleWLHttpExchange(WLHttpExchange exchange) {
        final String host = exchange.getHeader("Host", "null"), localIP = exchange.getIPAddress(false);
        final String pathKey = exchange.getPathValues()[0];
        final boolean isLocal = localIP.equals("/0:0:0:0:0:0:0:1");
        final boolean isWebsite = host.equals(DOMAIN) || isLocal && pathKey.equals("html");
        final boolean isAPI = host.equals("api." + DOMAIN) || isLocal && pathKey.matches("v[0-9]+");
        //final boolean isSandbox = host.equals("sandbox." + DOMAIN) || isLocal && pathKey.equals("sandbox");
        if(isAPI) {
            handleAPIExchange(exchange);
        } else if(isWebsite) {
            handleHTMLExchange(exchange, exchange.getPath());
        } else {
            handleErrorExchange(exchange);
        }
    }

    private void handleHTMLExchange(WLHttpExchange exchange, String path) {
        final String response = getHTMLResponse(path);
        write(exchange, HttpURLConnection.HTTP_OK, response, WLContentType.HTML);
    }
    private void handleErrorExchange(WLHttpExchange exchange) {
        write(exchange, HttpURLConnection.HTTP_FORBIDDEN, "<!DOCTYPE html><html><body><h1>Error 403. Forbidden.</h1></body></html>", WLContentType.HTML);
    }

    private String getHTMLResponse(String targetPath) {
        if(targetPath.startsWith("/")) {
            targetPath = targetPath.substring(1);
        }
        if(targetPath.isEmpty()) {
            targetPath = "index";
        } else {
            switch (targetPath) {
                case "index":
                    targetPath = null;
                    break;
                default:
                    break;
            }
        }
        String string = null;
        if(targetPath != null) {
            final String target = Jsonable.CURRENT_FOLDER + "_html" + File.separator + targetPath.replace("/", File.separator) + ".html";
            final Path path = Paths.get(target);
            if(Files.exists(path)) {
                try {
                    string = Files.readString(path);
                } catch (Exception ignored) {
                    string = getHTMLErrorMsg(HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            }
        }
        if(string == null) {
            string = getHTMLErrorMsg(HttpURLConnection.HTTP_NOT_FOUND);
        }
        return string;
    }
    private String getHTMLErrorMsg(int status) {
        final String msg = getHTMLErrorMessage(status);
        return "<html><body><h1>" + msg + "</h1></body></html>";
    }
    private String getHTMLErrorMessage(int status) {
        switch (status) {
            case HttpURLConnection.HTTP_NOT_FOUND: return "Error 404. Destination not found.";
            case HttpURLConnection.HTTP_INTERNAL_ERROR: return "Error 500. Something went wrong server-side.";
            default: return "Error " + status + " occurred, no prepared error message to display.";
        }
    }
}
