package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.hurricanes.Hurricanes;
import org.apache.logging.log4j.Level;

public final class Weather implements WLServer {

    public static void main(String[] args) {
        new Weather();
    }

    private Weather() {
        test();
        //load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.WEATHER;
    }

    private void test() {
        Earthquakes.INSTANCE.getRecent(null, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Weather;test;object=");
                WLLogger.log(Level.INFO, "" + object);
            }
        });
    }

    @Override
    public void getServerResponse(ServerVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "alerts":
                WeatherAlerts.INSTANCE.getResponse(target.substring(key.length()+1), handler);
                break;
            case "earthquakes":
                Earthquakes.INSTANCE.getResponse(values, handler);
                break;
            case "hurricanes":
                final int year = Integer.parseInt(values[1]);
                Hurricanes.INSTANCE.getAtlanticSeason(year, handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "alerts/all",
                "earthquakes/recent"
        };
    }
}
