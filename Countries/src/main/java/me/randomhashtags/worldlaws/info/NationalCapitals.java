package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum NationalCapitals implements CountryNationalService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.NATIONAL_CAPITAL;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_national_capitals";
        final String title = getInfo().getTitle();
        final Elements trs = getNationalDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.select("td").size() < 2);
        final EventSource source = new EventSource("Wikipedia: List of national capitals", url);
        final EventSources sources = new EventSources(source);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(1).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
            if(!country.isEmpty()) {
                final String text = tds.size() > 2 ? tds.get(2).text() : null;
                final CountrySingleValue value = new CountrySingleValue(null, tds.get(0).text(), text, -1);
                value.setTitle(title);
                value.setSources(sources);
                countries.put(country, value.toString());
            }
        }
        handler.handle(null);
    }
}
