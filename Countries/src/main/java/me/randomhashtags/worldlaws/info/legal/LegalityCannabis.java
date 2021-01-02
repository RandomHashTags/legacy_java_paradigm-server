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

public enum LegalityCannabis implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_CANNABIS;
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
        styles.put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
        styles.put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
        styles.put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityCannabis - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legality_of_cannabis";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String title = getInfo().getTitle();
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            trs.removeIf(row -> row.hasAttr("class") && row.attr("class").equals("sortbottom"));
            trs.remove(trs.size()-1);
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Legality of cannabis", url));
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
                final String notes = getNotesFromElement(tds.get(3));

                final Element recreationalElement = tds.get(1), medicalElement = tds.get(2);
                final String recreationalText = recreationalElement.text().split(" See also")[0].split("\\[")[0], medicalText = medicalElement.text().split(" See also")[0].split("\\[")[0];

                final String recreationalString = getValue(recreationalElement);
                final CountryInfoValue recreationalValue = new CountryInfoValue("Recreational Use", recreationalString, recreationalText);

                final String medicinalString = getValue(medicalElement);
                final CountryInfoValue medicinalValue = new CountryInfoValue("Medicinal Use", medicinalString, medicalText);

                final CountryInfoKey info = new CountryInfoKey(title, notes, sources, recreationalValue, medicinalValue);
                countries.put(country, info.toString());
            }
            WLLogger.log(Level.INFO, "LegalityCannabis - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
