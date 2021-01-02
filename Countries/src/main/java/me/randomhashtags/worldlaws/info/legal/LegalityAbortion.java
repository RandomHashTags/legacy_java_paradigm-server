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

public enum LegalityAbortion implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_ABORTION;
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
        styles.put("background: #D2FFD2; color: black; vertical-align: middle; text-align: center;", "Legal, with complex legality or practice");
        styles.put("background: #FFB; color: black; vertical-align: middle; text-align: center;", "Varies by subdivision");
        styles.put("background: #FFD2D2; color:black; vertical-align: middle; text-align: center;", "Illegal, with complex legality or practice");
        styles.put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityAbortion - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Abortion_law";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(1).select("tbody tr");
            trs.remove(0);
            trs.removeIf(row -> row.hasAttr("class") && row.attr("class").equals("sortbottom"));
            //trs.remove(trs.size()-1);
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Abortion law", url));
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().split("\\[")[0].replace(" ", "");
                final Element saveLifeElement = tds.get(1), preserveHealthElement = tds.get(2), rapeElement = tds.get(3), fetalImpairmentElement = tds.get(4), economicOrSocialElement = tds.get(5), onRequestElement = tds.get(6);

                final CountryInfoValue saveLifeValue = new CountryInfoValue("Save Life", getValue(saveLifeElement), null);
                final CountryInfoValue preserveHealthValue = new CountryInfoValue("Preserve Health", getValue(preserveHealthElement), null);
                final CountryInfoValue rapeValue = new CountryInfoValue("Rape", getValue(rapeElement), null);
                final CountryInfoValue fetalImpairmentValue = new CountryInfoValue("Fetal Impairment", getValue(fetalImpairmentElement), null);
                final CountryInfoValue economicOrSocialValue = new CountryInfoValue("Economic or Social", getValue(economicOrSocialElement), null);
                final CountryInfoValue onRequestValue = new CountryInfoValue("On Request", getValue(onRequestElement), null);

                final CountryInfoKey info = new CountryInfoKey("Abortion", null, sources, saveLifeValue, preserveHealthValue, rapeValue, fetalImpairmentValue, economicOrSocialValue, onRequestValue);
                countries.put(country, info.toString());
            }
            WLLogger.log(Level.INFO, "LegalityAbortion - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
