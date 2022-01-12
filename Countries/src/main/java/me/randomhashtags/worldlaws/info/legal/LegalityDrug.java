package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface LegalityDrug extends CountryLegalityService {
    HashMap<String, String> STYLES = new HashMap<>();

    String getURL();
    String getCultivationTitle();
    boolean doesRemoveLastElement();

    @Override
    default int getYearOfData() {
        return WLUtilities.getTodayYear();
    }

    @Override
    default String loadData() {
        final String url = getURL(), cultivationTitle = getCultivationTitle(), title = getInfo().getTitle();
        final EventSources sources = getSources();
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        if(doesRemoveLastElement()) {
            trs.remove(trs.size() - 1);
        }

        final JSONObject json = new JSONObject();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
            final CountryInfoKey info = getInfoKey(tds, cultivationTitle, title, sources);
            json.put(country, info.toJSONObject());
        }
        return json.toString();
    }

    private HashMap<String, String> getStyles() {
        if(STYLES.isEmpty()) {
            STYLES.put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
            STYLES.put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
            STYLES.put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
        }
        return STYLES;
    }

    private CountryInfoKey getInfoKey(Elements tds, String cultivationTitle, String title, EventSources sources) {
        final Element possessionElement = tds.get(1), saleElement = tds.get(2), transportElement = tds.get(3), cultivationElement = tds.get(4);
        final String possessionText = possessionElement.text();
        final String saleText = saleElement.text();
        final String transportText = transportElement.text();
        final String cultivationText = cultivationElement.text();
        final String notes = getNotesFromElement(tds.get(5));

        final CountryInfoValue possession = new CountryInfoValue("Possession", getValue(possessionElement), possessionText);
        final CountryInfoValue sale = new CountryInfoValue("Sale", getValue(saleElement), saleText);
        final CountryInfoValue transport = new CountryInfoValue("Transport", getValue(transportElement), transportText);
        final CountryInfoValue cultivation = new CountryInfoValue(cultivationTitle, getValue(cultivationElement), cultivationText);

        return new CountryInfoKey(title, notes, getYearOfData(), sources, possession, sale, transport, cultivation);
    }
    private String getValue(Element element) {
        final String style = element.attr("style");
        return getStyles().getOrDefault(style, "Unknown");
    }
}
