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
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public enum LegalityPornography implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_PORNOGRAPHY;
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
        styles.put("background:#FFFFFF;vertical-align:middle;text-align:center;", "Unknown");
        styles.put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
        styles.put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
        styles.put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityPornography - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Pornography_laws_by_region";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String title = getInfo().getTitle();
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Pornography laws by region", url));
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final Element saleElement = tds.get(1), possessionElement = tds.get(2), internetElement = tds.get(3);
                final String saleString = getValue(saleElement), possessionString = getValue(possessionElement), internetString = getValue(internetElement);

                if(!saleString.equals("Unknown") && !possessionString.equals("Unknown") && !internetString.equals("Unknown")) {
                    final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
                    final String notes = "null";

                    final String saleText = removeReferences(saleElement.text());
                    final CountryInfoValue saleValue = new CountryInfoValue("Sale", saleString, saleText);

                    final String possessionText = removeReferences(possessionElement.text());
                    final CountryInfoValue possessionValue = new CountryInfoValue("Possession", possessionString, possessionText);

                    final String internetText = removeReferences(internetElement.text());
                    final CountryInfoValue internetValue = new CountryInfoValue("Internet Pornography", internetString, internetText);

                    final List<TextNode> textNodes = tds.get(4).textNodes();
                    final String penaltyText = textNodes.isEmpty() || textNodes.get(0).text().equals(" ") ? "Unknown" : textNodes.get(0).text();
                    final CountryInfoValue penaltyValue = new CountryInfoValue("Penalty", penaltyText, "");

                    final CountryInfoKey info = new CountryInfoKey(title, notes, sources, saleValue, possessionValue, internetValue, penaltyValue);
                    countries.put(country, info.toString());
                }
            }
            WLLogger.log(Level.INFO, "LegalityPornography - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
