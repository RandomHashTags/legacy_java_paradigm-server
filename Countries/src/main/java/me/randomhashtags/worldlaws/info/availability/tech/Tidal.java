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

public enum Tidal implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_TIDAL;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_MUSIC;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://support.tidal.com/hc/en-us/articles/202453191-Which-countries-is-TIDAL-available-";
        final Elements trs = getDocumentElements(url, "section.categories-wrapper div ul li.txt-content p");
        trs.remove(trs.size()-1);
        trs.remove(trs.size()-1);
        final EventSource source = new EventSource("Tidal", url);
        final EventSources sources = new EventSources(source);
        final String availability = getAvailability(true);
        for(Element element : trs) {
            final String country = element.text().toLowerCase().replace(" ", "").replace("ofamerica", "");
            countries.put(country, availability);
        }
        handler.handle(null);
    }
}
