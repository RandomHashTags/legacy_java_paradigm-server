package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

public enum LegalityPornography implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_PORNOGRAPHY;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    private void loadStyles() {
        styles = new HashMap<>() {{
            put("background:#FFFFFF;vertical-align:middle;text-align:center;", "Unknown");
            put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
            put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
            put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
        }};
    }

    public void refresh(CompletionHandler handler) {
        loadStyles();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Pornography_laws_by_region";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final String title = getInfo().getTitle();
        trs.remove(0);
        final EventSource source = new EventSource("Wikipedia: Pornography laws by region", url);
        final EventSources sources = new EventSources(source);
        final int yearOfData = WLUtilities.getTodayYear();
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

                final CountryInfoKey info = new CountryInfoKey(title, notes, yearOfData, sources, saleValue, possessionValue, internetValue, penaltyValue);
                countries.put(country, info.toString());
            }
        }
        handler.handle(null);
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
