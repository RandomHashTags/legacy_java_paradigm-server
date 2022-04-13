package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.tracker.NASA_EONET;

public enum ServerRequestTypeWeather implements ServerRequestType {
    ALERTS,
    EARTHQUAKES,
    NATURAL_EVENTS,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (this) {
            case ALERTS:
                return httpExchange -> {
                    final String target = httpExchange.getShortPath();
                    return WeatherAlerts.INSTANCE.getResponse(target);
                };
            case EARTHQUAKES:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return Earthquakes.INSTANCE.getResponse(values);
                };

            case NATURAL_EVENTS:
                return httpExchange -> {
                    return NASA_EONET.INSTANCE.getCurrent(version);
                };
            default:
                break;
        }

        switch (version) {
            case v1:
                return null;
            default:
                return null;
        }
    }
}
