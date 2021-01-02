package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum LegalityMaritalRape implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_MARITAL_RAPE;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            loadStyles();
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    private void loadStyles() {
        styles = new HashMap<>();
        styles.put("background: #ececec; color: #2C2C2C; font-size: smaller; vertical-align: middle; text-align: center;", "Unclear");
        styles.put("background:#9F9;vertical-align:middle;text-align:center;", "Illegal");
        styles.put("background:#F99;vertical-align:middle;text-align:center;", "Legal");
        styles.put("background: #FFD; color: black; vertical-align: middle; text-align: center;", "Legal and Illegal");
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityMaritalRape - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Marital_rape_laws_by_country";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String title = getInfo().getTitle();
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Marital rape laws by country", url));
            load(title, sources, tables.get(0)); // A
            load(title, sources, tables.get(1)); // B
            load(title, sources, tables.get(2)); // C
            load(title, sources, tables.get(3)); // D
            load(title, sources, tables.get(4)); // E
            load(title, sources, tables.get(5)); // F
            load(title, sources, tables.get(6)); // G
            load(title, sources, tables.get(7)); // H
            load(title, sources, tables.get(8)); // I
            load(title, sources, tables.get(9)); // J
            load(title, sources, tables.get(10)); // K
            load(title, sources, tables.get(11)); // L
            load(title, sources, tables.get(12)); // M
            load(title, sources, tables.get(13)); // N
            load(title, sources, tables.get(14)); // O
            load(title, sources, tables.get(15)); // P
            load(title, sources, tables.get(16)); // Q
            load(title, sources, tables.get(17)); // R
            load(title, sources, tables.get(18)); // S
            load(title, sources, tables.get(19)); // T
            load(title, sources, tables.get(20)); // U
            load(title, sources, tables.get(21)); // V
            load(title, sources, tables.get(22)); // Y
            load(title, sources, tables.get(23)); // Z
            WLLogger.log(Level.INFO, "LegalityMaritalRape - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
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
            final CountryInfoKey info = new CountryInfoKey(title, notes, sources, value);
            countries.put(country, info.toString());
        }
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
