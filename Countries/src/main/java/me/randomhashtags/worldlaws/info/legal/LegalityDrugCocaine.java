package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum LegalityDrugCocaine implements LegalityDrug {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_DRUG_COCAINE;
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
    @Override
    public HashMap<String, String> getStyles() {
        return styles;
    }

    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityDrugCocaine - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legal_status_of_cocaine";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            trs.remove(trs.size()-1);
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Legal status of cocaine", url));
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
                final CountryInfoKey info = getInfoKey("Drug: Cocaine", tds, sources, "Cultivation");
                countries.put(country, info.toString());
            }
            WLLogger.log(Level.INFO, "LegalityDrugCocaine - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
