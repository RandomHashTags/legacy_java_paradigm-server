package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.location.CountryBackendID;
import me.randomhashtags.worldlaws.weather.WeatherController;
import org.jsoup.nodes.Document;

import java.util.HashMap;

public enum WeatherAU implements WeatherController {
    INSTANCE;

    private String alertEvents;
    private HashMap<String, String> eventAlerts;
    private HashMap<String, HashMap<String, String>> territoryEventAlerts;
    private HashMap<String, String> territoryAlerts;

    @Override
    public CountryBackendID getCountryBackendID() {
        return CountryBackendID.AUSTRALIA;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("Australian Government", "https://weather.news.com.au");
    }

    @Override
    public void getAlertEvents(CompletionHandler handler) {
        if(alertEvents != null) {
            handler.handle(alertEvents);
        } else {
            refreshAlerts(handler);
        }
    }

    @Override
    public HashMap<String, String> getEventAlerts() {
        return eventAlerts;
    }
    @Override
    public HashMap<String, HashMap<String, String>> getTerritoryEventAlerts() {
        return territoryEventAlerts;
    }
    @Override
    public HashMap<String, String> getTerritoryAlerts() {
        return territoryAlerts;
    }

    @Override
    public void refreshAlerts(CompletionHandler handler) {
        eventAlerts = new HashMap<>();
        territoryAlerts = new HashMap<>();
        final String url = "https://weather.news.com.au";
        final Document doc = getDocument(url);
        if(doc != null) {
            final StringBuilder builder = new StringBuilder("[");
            final String string = builder.append("]").toString();
            handler.handle(string);
        }
    }
}
