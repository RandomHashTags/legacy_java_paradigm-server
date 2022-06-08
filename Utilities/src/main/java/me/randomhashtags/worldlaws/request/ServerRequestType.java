package me.randomhashtags.worldlaws.request;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;

public interface ServerRequestType extends Jsonable {
    String name();
    default WLHttpHandler getWLHttpHandler(LocalServer server, APIVersion version) {
        final WLHttpHandler handler = getHandler(version);
        return new WLHttpHandler() {
            @Override
            public JSONTranslatable getResponse(WLHttpExchange httpExchange) {
                server.madeRequest(httpExchange.getIdentifier(), httpExchange.getPath());
                return handler.getResponse(httpExchange);
            }

            @Override
            public String getFallbackResponse(WLHttpExchange httpExchange) {
                return handler.getFallbackResponse(httpExchange);
            }
        };
    }
    WLHttpHandler getHandler(APIVersion version);
    default boolean isConditional() {
        return false;
    }

    default APIVersion getAPIVersion() {
        return APIVersion.v1;
    }
}
