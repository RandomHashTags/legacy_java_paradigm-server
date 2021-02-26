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

public enum LegalityCannabis implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries, styles;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_CANNABIS;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    private void loadStyles() {
        styles = new HashMap<>() {{
            put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
            put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
            put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
        }};
    }

    @Override
    public void refresh(CompletionHandler handler) {
        loadStyles();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legality_of_cannabis";
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final String title = getInfo().getTitle();
        trs.remove(0);
        trs.removeIf(row -> row.hasAttr("class") && row.attr("class").equals("sortbottom"));
        trs.remove(trs.size()-1);
        final EventSource source = new EventSource("Wikipedia: Legality of cannabis", url);
        final EventSources sources = new EventSources(source);
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

            final CountryInfoKey info = new CountryInfoKey(title, notes, -1, sources, recreationalValue, medicinalValue);
            countries.put(country, info.toString());
        }
        handler.handle(null);
    }
    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
}
