package me.randomhashtags.worldlaws.request;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface WLHttpHandler extends HttpHandler {

    boolean PRODUCTION_MODE = Settings.DataValues.isProductionMode();

    @Override
    default void handle(HttpExchange httpExchange) {
        final WLHttpExchange exchange = new WLHttpExchange(httpExchange);
        final String host = exchange.getHeader("Host", "null"), domain = "***REMOVED***";
        final boolean isLocal = host.startsWith("localhost:") && exchange.getIPAddress(false).equals("/127.0.0.1");
        final boolean isWebsite = host.equals(domain);
        final boolean isAPI = host.equals("api." + domain);
        final boolean isSandbox = host.equals("sandbox." + domain);
        CompletableFuture.runAsync(() -> {
            if(isLocal || isAPI) {
                handleAPIExchange(exchange);
            } else if(isWebsite) {
                handleHTMLExchange(exchange);
            } else {
                handleErrorExchange(exchange);
            }
        }).orTimeout(1, TimeUnit.MINUTES).exceptionally(throwable -> {
            WLUtilities.saveException(throwable);
            return null;
        }).whenComplete((result, exception) -> {
            closeExchange(exchange);
        });
    }

    private void handleAPIExchange(WLHttpExchange exchange) {
        final boolean validRequest = exchange.isValidRequest();
        int status = HttpURLConnection.HTTP_FORBIDDEN;
        String string;
        if(!PRODUCTION_MODE || validRequest) {
            status = HttpURLConnection.HTTP_OK;
            final JSONTranslatable json = getResponse(exchange);
            if(json instanceof JSONObjectTranslatable || json instanceof JSONArrayTranslatable) {
                final JSONObject test = WLUtilities.translateJSON(json, exchange.getLanguageType(), exchange.getLanguage());
                string = test.toString();
            } else {
                string = getFallbackResponse(exchange);
            }
        } else {
            string = null;
        }

        if(string == null) {
            string = WLUtilities.SERVER_EMPTY_JSON_RESPONSE;
        }
        write(exchange, status, string, "application/json");
    }
    private void handleHTMLExchange(WLHttpExchange exchange) {
        write(exchange, HttpURLConnection.HTTP_OK, "<html><body><p>test Paradigm html response</p></body></html>", "text/html");
    }
    private void handleErrorExchange(WLHttpExchange exchange) {
        write(exchange, HttpURLConnection.HTTP_FORBIDDEN, "<html></html>", "text/html");
    }
    private void write(WLHttpExchange exchange, int status, String value, String contentType) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        try {
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(status, bytes.length);
        } catch (Exception ignored) {
        }
        try {
            exchange.getResponseBody().write(bytes);
        } catch (Exception ignored) {
        }
    }

    private void closeExchange(WLHttpExchange exchange) {
        try {
            final OutputStream out = exchange.getResponseBody();
            out.close();
        } catch (Exception ignored) {
        }
        exchange.close();
    }

    JSONTranslatable getResponse(WLHttpExchange httpExchange);
    default String getFallbackResponse(WLHttpExchange httpExchange) {
        return null;
    }
}
