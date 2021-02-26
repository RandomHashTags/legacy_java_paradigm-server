package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;

public enum DisneyPlus implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_DISNEY_PLUS;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_STREAMING;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Disney%2B";
        final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable").get(0).select("tbody tr");
        trs.remove(0);
        final EventSource source = new EventSource("Wikipedia: Disney+", url);
        final EventSources sources = new EventSources(source);
        final String availability = getAvailability(true);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String targetCountry = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final HashSet<String> countries = getCountriesFromText(targetCountry);
            for(String country : countries) {
                this.countries.put(country, availability);
            }
        }
        handler.handle(null);
    }
}
