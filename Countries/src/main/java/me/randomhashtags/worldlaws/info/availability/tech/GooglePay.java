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

public enum GooglePay implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_GOOGLE_PAY;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.PAYMENT_METHOD;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Google_Pay";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable").get(0).select("tbody tr");
        trs.remove(0);
        final EventSource source = new EventSource("Wikipedia: Google Pay", url);
        final EventSources sources = new EventSources(source);
        final String availability = getAvailability(true);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int size = tds.size();
            final String country = tds.get(1-(size == 3 || size == 4 ? 1 : 0)).select("a").get(0).text().toLowerCase().replace(" ", "");
            countries.put(country, availability);
        }
        handler.handle(null);
    }
}
