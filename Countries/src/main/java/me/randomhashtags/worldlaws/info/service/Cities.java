package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

// https://en.wikipedia.org/wiki/Lists_of_cities_by_country
public enum Cities implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;
    private HashMap<String, HashMap<String, String>> cities;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.CITIES;
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.LISTS;
    }

    @Override
    public void getCountryValue(String countryBackendID, CompletionHandler handler) {
    }

    public void getCitiesFrom(String countryBackendID, String territory, CompletionHandler handler) {
        if(countries == null) {
            loadData(null);
        }
        if(cities.containsKey(countryBackendID)) {
            final HashMap<String, String> map = cities.get(countryBackendID);
            if(map.containsKey(territory)) {
                handler.handleString(map.get(territory));
            } else {
            }
        } else {
            loadCities(countryBackendID, handler);
        }
    }

    @Override
    public void loadData(CompletionHandler handler) {
        cities = new HashMap<>();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Lists_of_cities_by_country";
        final Elements elements = getDocumentElements(FileType.COUNTRIES, url, "div.mw-parser-output ul li");
        elements.removeIf(element -> element.select("b").isEmpty());
        for(Element element : elements) {
            final Element link = element.select("b").get(0).select("a").get(0);
            final String targetURL = "https://en.wikipedia.org" + link.attr("href"), text = link.text().toLowerCase();
            final boolean isInThe = text.contains(" in the ");
            final String target = text.contains("in") ? " in " + (isInThe ? "the " : "") : "";
            final String country = (!target.isEmpty() ? text.split(target)[1] : text).replace(" ", "").split("\\(")[0];
            countries.put(country, targetURL);
        }
        if(handler != null) {
            handler.handleString(null);
        }
    }

    private void loadCities(String country, CompletionHandler handler) {
        final String url = countries.get(country);
        WLLogger.log(Level.INFO, "Cities;country=" + country + ";url=" + url);
    }
}
