package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
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
    public HashMap<String, JSONObjectTranslatable> getEventPreAlerts() {
        return null;
    }

    @Override
    public HashMap<String, JSONObjectTranslatable> getSubdivisionEvents() {
        return null;
    }

    @Override
    public HashMap<String, HashMap<String, JSONObjectTranslatable>> getSubdivisionPreAlerts() {
        return null;
    }

    @Override
    public JSONArrayTranslatable getEventTypes() {
        return null;
    }

    @Override
    public JSONObjectTranslatable refresh() {
        final String url = "https://weather.news.com.au";
        final Document doc = getDocument(url);
        JSONObjectTranslatable string = null;
        if(doc != null) {
            //final StringBuilder builder = new StringBuilder("[");
            //string = builder.append("]").toString();
        }
        return string;
    }

    @Override
    public JSONObjectTranslatable getAlert(String id) {
        return null;
    }
}
