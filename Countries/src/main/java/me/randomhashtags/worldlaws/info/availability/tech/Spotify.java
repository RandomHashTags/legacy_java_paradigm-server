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

public enum Spotify implements CountryService {
    INSTANCE;

    private HashMap<String, String> availabilities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_SPOTIFY;
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
        final String url = "https://support.spotify.com/us/article/full-list-of-territories-where-spotify-is-available/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("body.type-normal div div.mainContainer div.container div.Layout_main__3m1yK div.raw-content div.RawContent_tableWrapper__3mA43 table tbody tr");
            final EventSources sources = new EventSources(new EventSource("Spotify", url));
            final String title = getInfo().getTitle();
            final CountryAvailabilityCategory category = CountryAvailabilityCategory.ENTERTAINMENT_MUSIC;
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String[] countries = tds.get(1).text().split("\\.")[0].split(", ");
                for(String country : countries) {
                    country = country.toLowerCase().replace(" ", "");
                    final CountryAvailability availability = new CountryAvailability(title, true, category);
                    availabilities.put(country, availability.toString());
                }
            }
            WLLogger.log(Level.INFO, "Spotify - loaded " + availabilities.size() + " countries/territories (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
