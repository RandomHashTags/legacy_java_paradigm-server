package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.space.constellation.Constellations;

public enum ServerRequestTypeScience implements ServerRequestType {
    SPACE,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (this) {
            case SPACE:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return getSpaceResponse(version, values);
                };
            default:
                break;
        }
        switch (version) {
            case v1: return getV1Handler(version);
            default: return null;
        }
    }

    private WLHttpHandler getV1Handler(APIVersion version) {
        switch (this) {
            default:
                return null;
        }
    }

    private JSONObjectTranslatable getSpaceResponse(APIVersion version, String[] values) {
        final String key = values[0];
        switch (key) {
            case "constellation": return getConstellationResponse(version, values);
            case "planet": return getPlanetResponse(version, values);
            default: return null;
        }
    }
    private JSONObjectTranslatable getConstellationResponse(APIVersion version, String[] values) {
        final String key = values[1];
        switch (key) {
            default:
                return Constellations.INSTANCE.getByName(values[2]);
        }
    }
    private JSONObjectTranslatable getPlanetResponse(APIVersion version, String[] values) {
        final String key = values[1];
        switch (key) {
            default:
                return null;
        }
    }
}
