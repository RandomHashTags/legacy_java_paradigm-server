package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

// https://en.wikipedia.org/wiki/Lists_of_cities_by_country
public enum Cities implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;
    private HashMap<String, HashMap<String, String>> cities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.CITIES;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
    }

    public void getCitiesFrom(String countryBackendID, String territory, CompletionHandler handler) {
        if(countries == null) {
            refresh(null);
        }
        if(cities.containsKey(countryBackendID)) {
            final HashMap<String, String> map = cities.get(countryBackendID);
            if(map.containsKey(territory)) {
                handler.handle(map.get(territory));
            } else {
            }
        } else {
            loadCities(countryBackendID, handler);
        }
    }

    @Override
    public void refresh(CompletionHandler handler) {
        cities = new HashMap<>();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Lists_of_cities_by_country";
        final Elements elements = getDocumentElements(url, "div.mw-parser-output ul li");
        elements.removeIf(element -> element.select("b").isEmpty());
        for(Element element : elements) {
            final Element link = element.select("b").get(0).select("a").get(0);
            final String targetURL = "https://en.wikipedia.org" + link.attr("href"), text = link.text().toLowerCase();
            final boolean isInThe = text.contains(" in the ");
            final String target = text.contains("in") ? " in " + (isInThe ? "the " : "") : "";
            final String country = (!target.isEmpty() ? text.split(target)[1] : text).replace(" ", "").split("\\(")[0];
            countries.put(country, targetURL);
        }
        handler.handle(null);
    }

    private void loadCities(String country, CompletionHandler handler) {
        final String url = countries.get(country);
        WLLogger.log(Level.INFO, "Cities;country=" + country + ";url=" + url);
    }
}
