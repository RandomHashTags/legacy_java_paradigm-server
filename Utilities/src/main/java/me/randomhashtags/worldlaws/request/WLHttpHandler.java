package me.randomhashtags.worldlaws.request;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;

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
        CompletableFuture.runAsync(() -> {
            handleWLHttpExchange(exchange);
        }).orTimeout(30, TimeUnit.SECONDS).exceptionally(throwable -> {
            WLUtilities.saveException(throwable);
            return null;
        }).whenComplete((result, error) -> {
            exchange.close();
        });
    }

    default void handleWLHttpExchange(WLHttpExchange exchange) {
        handleAPIExchange(exchange);
    }

    default void handleAPIExchange(WLHttpExchange exchange) {
        final boolean validRequest = exchange.isValidRequest();
        int status = HttpURLConnection.HTTP_FORBIDDEN;
        String string;
        if(!PRODUCTION_MODE || validRequest) {
            status = HttpURLConnection.HTTP_OK;
            final JSONTranslatable json = getResponse(exchange);
            if(json instanceof JSONObjectTranslatable || json instanceof JSONArrayTranslatable) {
                if(json instanceof JSONObjectTranslatable) {
                    string = WLUtilities.translateJSON(json, exchange.getLanguageType(), exchange.getLanguage()).toString();
                } else {
                    string = json.toString(); // TODO: translate JSONArray?
                }
            } else {
                string = getFallbackResponse(exchange);
            }
        } else {
            string = null;
        }

        if(string == null) {
            string = WLUtilities.SERVER_EMPTY_JSON_RESPONSE;
        }
        write(exchange, status, string, WLContentType.JSON);
    }
    default void write(WLHttpExchange exchange, int status, String value, WLContentType contentType) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType.getIdentifier());
        headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        headers.set("Connection", "close");
        switch (contentType) {
            case HTML:
                headers.set("Cache-Control", "public");
                break;
            case CSS:
                headers.set("Cache-Control", "max-age=86400");
                break;
            default:
                break;
        }
        exchange.sendResponseHeaders(status, bytes.length);
        final OutputStream out = exchange.getResponseBody();
        try {
            out.write(bytes);
        } catch (Exception ignored) {
        }
        try {
            out.close();
        } catch (Exception ignored) {
        }
    }

    JSONTranslatable getResponse(WLHttpExchange httpExchange);
    default String getFallbackResponse(WLHttpExchange httpExchange) {
        return null;
    }
}
