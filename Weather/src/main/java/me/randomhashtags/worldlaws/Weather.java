package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.tracker.NASA_EONET;

public final class Weather implements WLServer {

    public static void main(String[] args) {
        new Weather();
    }

    private Weather() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.WEATHER;
    }

    private void test() {
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "alerts":
                WeatherAlerts.INSTANCE.getResponse(target.substring(key.length()+1), handler);
                break;
            case "earthquakes":
                Earthquakes.INSTANCE.getResponse(values, handler);
                break;
            case "natural_events":
                NASA_EONET.INSTANCE.getCurrent(version, handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "alerts/all",
                "earthquakes/recent",
                "natural_events"
        };
    }
}
