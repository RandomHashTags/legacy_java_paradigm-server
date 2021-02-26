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

public enum LegalityDrinkingAge implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_DRINKING_AGE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legal_drinking_age";
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: Legal drinking age", url);
        final EventSources sources = new EventSources(source);
        for(int i = 0; i < 5; i++) {
            load(title, sources, tables.get(i), i == 3);
        }
        handler.handle(null);
    }
    private void load(String title, EventSources sources, Element table, boolean propertyBased) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        if(propertyBased) {
            trs.remove(0);
            trs.remove(trs.size()-1);
        }
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final Element firstElement = tds.get(0);
            final String country = firstElement.text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
            if(!countries.containsKey(country)) {
                final boolean hasStateRegionProvince = firstElement.hasAttr("colspan") && Integer.parseInt(firstElement.attr("colspan")) == 1;

                final Element targetElement = tds.get(hasStateRegionProvince ? 2 : 1);
                final boolean isSameAge = targetElement.hasAttr("colspan") && Integer.parseInt(targetElement.attr("colspan")) > 1;

                final String drinkingAgeText = removeReferences(targetElement.text());
                final CountryInfoValue drinkingAge = new CountryInfoValue("Drinking Age", drinkingAgeText, null);

                final String notes = getNotesFromElement(tds.get((hasStateRegionProvince ? 4 : 3)-(isSameAge ? 1 : 0)));
                final String purchasingAgeText;
                if(isSameAge) {
                    purchasingAgeText = drinkingAgeText;
                } else {
                    final Element purchaseAge = tds.get(hasStateRegionProvince ? 3 : 2);
                    purchasingAgeText = removeReferences(purchaseAge.text());
                }
                final CountryInfoValue purchasingAge = new CountryInfoValue("Purchasing Age", purchasingAgeText, null);

                final CountryInfoKey info = new CountryInfoKey(title, notes, -1, sources, drinkingAge, purchasingAge);
                countries.put(country, info.toString());
            }
        }
    }
}
