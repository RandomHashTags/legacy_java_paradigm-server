package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.logging.Level;

public enum GooglePlayPass implements CountryService {
    INSTANCE;

    private HashMap<String, String> availabilities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_GOOGLE_PLAY_PASS;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(availabilities != null) {
            handler.handle(getValue(countryBackendID));
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(getValue(countryBackendID));
                }
            });
        }
    }

    private String getValue(String countryBackendID) {
        if(!availabilities.containsKey(countryBackendID)) {
            availabilities.put(countryBackendID, new CountryAvailability(getInfo().getTitle(), false, CountryAvailabilityCategory.ENTERTAINMENT_GAMING).toString());
        }
        return availabilities.get(countryBackendID);
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        availabilities = new HashMap<>();
        final String url = "https://play.google.com/about/pass-availability/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSources sources = new EventSources(new EventSource("Google", url));
            final String title = getInfo().getTitle();
            final CountryAvailabilityCategory category = CountryAvailabilityCategory.ENTERTAINMENT_GAMING;
            for(Element element : doc.select("body main.h-c-page ul li")) {
                final String country = element.text().toLowerCase().replace(" ", "");
                final CountryAvailability availability = new CountryAvailability(title, true, category);
                availabilities.put(country, availability.toString());
            }
            WLLogger.log(Level.INFO, "GooglePlayPass - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
