package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface LegalityDrug extends CountryService {

    HashMap<String, String> getStyles();

    default CountryInfoKey getInfoKey(String title, Elements tds, EventSources sources, String cultivationTitle) {
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

        return new CountryInfoKey(title, notes, sources, possession, sale, transport, cultivation);
    }
    private String getValue(Element element) {
        final String style = element.attr("style");
        return getStyles().getOrDefault(style, "Unknown");
    }
}
