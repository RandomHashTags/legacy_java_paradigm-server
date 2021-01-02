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
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum Tidal implements CountryService {
    INSTANCE;

    private HashMap<String, String> availabilities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_TIDAL;
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
            availabilities.put(countryBackendID, new CountryAvailability(getInfo().getTitle(), false, CountryAvailabilityCategory.ENTERTAINMENT_MUSIC).toString());
        }
        return availabilities.get(countryBackendID);
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        availabilities = new HashMap<>();
        final String url = "https://support.tidal.com/hc/en-us/articles/202453191-Which-countries-is-TIDAL-available-";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("section.categories-wrapper div ul li.txt-content p");
            trs.remove(trs.size()-1);
            trs.remove(trs.size()-1);
            final EventSources sources = new EventSources(new EventSource("Tidal", url));
            final String title = getInfo().getTitle();
            final CountryAvailabilityCategory category = CountryAvailabilityCategory.ENTERTAINMENT_MUSIC;
            for(Element element : trs) {
                final String country = element.text().toLowerCase().replace(" ", "").replace("ofamerica", "");
                final CountryAvailability availability = new CountryAvailability(title, true, category);
                availabilities.put(country, availability.toString());
            }
            WLLogger.log(Level.INFO, "Tidal - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
