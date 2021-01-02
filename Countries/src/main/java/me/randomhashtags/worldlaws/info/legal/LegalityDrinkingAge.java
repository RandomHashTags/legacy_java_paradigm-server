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

public enum LegalityDrinkingAge implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_DRINKING_AGE;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityDrinkingAge - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legal_drinking_age";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String title = getInfo().getTitle();
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Legal drinking age", url));
            load(title, sources, tables.get(0), false); // Africa
            load(title, sources, tables.get(1), false); // Americas
            load(title, sources, tables.get(2), false); // Asia
            load(title, sources, tables.get(3), true); // Europe
            load(title, sources, tables.get(4), false); // Oceania
            WLLogger.log(Level.INFO, "LegalityDrinkingAge - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
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

                final CountryInfoKey info = new CountryInfoKey(title, notes, sources, drinkingAge, purchasingAge);
                countries.put(country, info.toString());
            }
        }
    }
}
