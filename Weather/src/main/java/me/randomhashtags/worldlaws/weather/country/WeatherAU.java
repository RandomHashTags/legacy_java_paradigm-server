package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.weather.WeatherController;
import org.jsoup.nodes.Document;

import java.util.HashMap;

public enum WeatherAU implements WeatherController {
    INSTANCE;

    @Override
    public WLCountry getCountry() {
        return WLCountry.AUSTRALIA;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("Australian Government", "https://weather.news.com.au");
    }

    @Override
    public HashMap<String, String> getEventPreAlerts() {
        return null;
    }

    @Override
    public HashMap<String, String> getSubdivisionEvents() {
        return null;
    }

    @Override
    public HashMap<String, HashMap<String, String>> getSubdivisionPreAlerts() {
        return null;
    }

    @Override
    public String refresh() {
        final String url = "https://weather.news.com.au";
        final Document doc = getDocument(url);
        String string = null;
        if(doc != null) {
            final StringBuilder builder = new StringBuilder("[");
            string = builder.append("]").toString();
        }
        return string;
    }

    @Override
    public String getZones(String[] zones) {
        return null;
    }

    @Override
    public String getZone(String zoneID) {
        return null;
    }

    @Override
    public String getAlert(String id) {
        return null;
    }
}
