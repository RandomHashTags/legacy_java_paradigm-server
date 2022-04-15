package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.globalwarming.GlobalWarming;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

public enum ServerRequestTypeEnvironment implements ServerRequestType {
    GLOBAL_WARMING,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (version) {
            case v1: return getV1Handler();
            default: return null;
        }
    }

    private WLHttpHandler getV1Handler() {
        switch (this) {
            case GLOBAL_WARMING:
                return httpExchange -> {
                    final APIVersion version = httpExchange.getAPIVersion();
                    final String[] values = httpExchange.getPathValues();
                    return GlobalWarming.INSTANCE.getResponse(version, values);
                };
            default:
                return null;
        }
    }
}
