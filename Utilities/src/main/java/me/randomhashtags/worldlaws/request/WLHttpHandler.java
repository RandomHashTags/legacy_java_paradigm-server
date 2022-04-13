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

public interface WLHttpHandler extends HttpHandler {

    @Override
    default void handle(HttpExchange httpExchange) {
        CompletableFuture.runAsync(() -> {
            final WLHttpExchange exchange = new WLHttpExchange(httpExchange);
            final boolean validRequest = exchange.isValidRequest(), productionMode = Settings.DataValues.isProductionMode();
            int status = HttpURLConnection.HTTP_FORBIDDEN;
            String string;
            if(!productionMode || validRequest) {
                status = HttpURLConnection.HTTP_OK;
                final JSONTranslatable json = getResponse(exchange);
                if(json instanceof JSONObjectTranslatable || json instanceof JSONArrayTranslatable) {
                    final JSONObject test = WLUtilities.translateJSON(json, exchange.getLanguageType(), exchange.getLanguage());
                    string = test.toString();
                } else {
                    string = getFallbackResponse(exchange);
                }
            } else {
                string = "stop";
            }

            if(string == null) {
                string = WLUtilities.SERVER_EMPTY_JSON_RESPONSE;
            }
            final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
            try {
                exchange.sendResponseHeaders(status, bytes.length);
                final OutputStream out = exchange.getResponseBody();
                out.write(bytes);
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
            exchange.close();
        });
    }

    JSONTranslatable getResponse(WLHttpExchange httpExchange);
    default String getFallbackResponse(WLHttpExchange httpExchange) {
        return null;
    }

    default boolean isOnlyAccessibleFromServer() {
        return false;
    }
}
