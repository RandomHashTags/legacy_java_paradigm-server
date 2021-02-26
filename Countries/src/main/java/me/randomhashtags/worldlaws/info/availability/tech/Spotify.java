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

public enum Spotify implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_SPOTIFY;
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
        final String url = "https://support.spotify.com/us/article/full-list-of-territories-where-spotify-is-available/";
        final Elements trs = getAvailabilityDocumentElements(url, "body.type-normal div div.mainContainer div.container div.Layout_main__3m1yK div.raw-content div.RawContent_tableWrapper__3mA43 table tbody tr");
        final EventSource source = new EventSource("Spotify", url);
        final EventSources sources = new EventSources(source);
        final String availability = getAvailability(true);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String[] countries = tds.get(1).text().split("\\.")[0].split(", ");
            for(String country : countries) {
                country = country.toLowerCase().replace(" ", "");
                this.countries.put(country, availability);
            }
        }
        handler.handle(null);
    }
}
