package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum MinimumDrivingAge implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_MINIMUM_DRIVING_AGE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_minimum_driving_ages";
        final Elements tables = getDocumentElements(url, "div.mw-parser-output table.wikitable");
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: List of minimum driving ages", url);
        final EventSources sources = new EventSources(source);
        for(Element table : tables) {
            load(title, table, sources);
        }
        handler.handle(null);
    }
    private void load(String title, Element table, EventSources sources) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        for(Element tr : trs) {
            final Elements tds = tr.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final String age = removeReferences(tds.get(1).text()), notes = getNotesFromElement(tds.get(2));
            final CountrySingleValue value = new CountrySingleValue(title, notes, age, null, -1, sources);
            countries.put(country, value.toString());
        }
    }
}
