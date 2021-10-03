package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.CompletionHandler;
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
    public String getEvents() {
        return null;
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
    public void refresh(CompletionHandler handler) {
        final String url = "https://weather.news.com.au";
        final Document doc = getDocument(url);
        if(doc != null) {
            final StringBuilder builder = new StringBuilder("[");
            final String string = builder.append("]").toString();
            handler.handleString(string);
        }
    }

    @Override
    public void getZones(String[] zones, CompletionHandler handler) {
    }

    @Override
    public void getZone(String zoneID, CompletionHandler handler) {
    }

    @Override
    public void getAlert(String id, CompletionHandler handler) {

    }
}
