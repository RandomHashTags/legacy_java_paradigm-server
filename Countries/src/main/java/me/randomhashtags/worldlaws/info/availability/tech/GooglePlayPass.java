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

public enum GooglePlayPass implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_GOOGLE_PLAY_PASS;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_GAMING;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://play.google.com/about/pass-availability/";
        final Elements elements = getAvailabilityDocumentElements(url, "body main.h-c-page ul li");
        final EventSource source = new EventSource("Google", url);
        final EventSources sources = new EventSources(source);
        final String availability = getAvailability(true);
        for(Element element : elements) {
            final String country = element.text().toLowerCase().replace(" ", "");
            countries.put(country, availability);
        }
        handler.handle(null);
    }
}
