package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum LegalityMaritalRape implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_MARITAL_RAPE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    private void loadStyles() {
        styles = new HashMap<>() {{
            put("background: #ececec; color: #2C2C2C; font-size: smaller; vertical-align: middle; text-align: center;", "Unclear");
            put("background:#9F9;vertical-align:middle;text-align:center;", "Illegal");
            put("background:#F99;vertical-align:middle;text-align:center;", "Legal");
            put("background: #FFD; color: black; vertical-align: middle; text-align: center;", "Legal and Illegal");
        }};
    }

    public void refresh(CompletionHandler handler) {
        loadStyles();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Marital_rape_laws_by_country";
        final Elements tables = getDocumentElements(url, "div.mw-parser-output table.wikitable");
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: Marital rape laws by country", url);
        final EventSources sources = new EventSources(source);
        for(int i = 0; i < 24; i++) {
            load(title, sources, tables.get(i));
        }
        handler.handle(null);
    }

    private void load(String title, EventSources sources, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = removeReferences(tds.get(0).text().toLowerCase().replace(" ", "").replace(",", ""));

            final Element legalElement = tds.get(1);
            final String notes = getNotesFromElement(tds.get(2));

            final CountryInfoValue value = new CountryInfoValue(title, getValue(legalElement), null);
            final CountryInfoKey info = new CountryInfoKey(title, notes, -1, sources, value);
            countries.put(country, info.toString());
        }
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
