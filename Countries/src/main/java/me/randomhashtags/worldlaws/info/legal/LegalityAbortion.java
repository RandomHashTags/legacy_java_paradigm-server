package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum LegalityAbortion implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_ABORTION;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    private void loadStyles() {
        styles = new HashMap<>() {{
            put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
            put("background: #D2FFD2; color: black; vertical-align: middle; text-align: center;", "Legal, with complex legality or practice");
            put("background: #FFB; color: black; vertical-align: middle; text-align: center;", "Varies by subdivision");
            put("background: #FFD2D2; color:black; vertical-align: middle; text-align: center;", "Illegal, with complex legality or practice");
            put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
        }};
    }

    @Override
    public void refresh(CompletionHandler handler) {
        loadStyles();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Abortion_law";
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 2).select("tbody tr");
        trs.removeIf(row -> row.select("td").size() == 0);
        final EventSource source = new EventSource("Wikipedia: Abortion law", url);
        final EventSources sources = new EventSources(source);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].replace(" ", "");
            final Element saveLifeElement = tds.get(1), preserveHealthElement = tds.get(2), rapeElement = tds.get(3), fetalImpairmentElement = tds.get(4), economicOrSocialElement = tds.get(5), onRequestElement = tds.get(6);

            final CountryInfoValue saveLifeValue = new CountryInfoValue("Risk to Life", getValue(saveLifeElement), null);
            final CountryInfoValue preserveHealthValue = new CountryInfoValue("Risk to Health", getValue(preserveHealthElement), null);
            final CountryInfoValue rapeValue = new CountryInfoValue("Rape", getValue(rapeElement), null);
            final CountryInfoValue fetalImpairmentValue = new CountryInfoValue("Fetal Impairment", getValue(fetalImpairmentElement), null);
            final CountryInfoValue economicOrSocialValue = new CountryInfoValue("Economic or Social", getValue(economicOrSocialElement), null);
            final CountryInfoValue onRequestValue = new CountryInfoValue("On Request", getValue(onRequestElement), null);

            final CountryInfoKey info = new CountryInfoKey("Abortion", null, -1, sources, saveLifeValue, preserveHealthValue, rapeValue, fetalImpairmentValue, economicOrSocialValue, onRequestValue);
            countries.put(country, info.toString());
        }
        handler.handle(null);
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
