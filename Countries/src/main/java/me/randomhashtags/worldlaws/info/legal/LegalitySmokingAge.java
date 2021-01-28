package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum LegalitySmokingAge implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_SMOKING_AGE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Smoking_age";
        final Elements tables = getDocumentElements(url, "div.mw-parser-output div + table.wikitable");
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: Smoking age", url);
        final EventSources sources = new EventSources(source);
        final int yearOfData = WLUtilities.getTodayYear();
        for(Element table : tables) {
            load(yearOfData, title, sources, table);
        }
        handler.handle(null);
    }
    private void load(int yearOfData, String title, EventSources sources, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.children();
            if(tds.size() >= 3) {
                final Elements links = tds.get(0).select("a");
                if(links.size() > 0) {
                    final String country = links.get(0).text().toLowerCase().split("\\(")[0].replace(".", "").replace(" ", "").replace(",", "").replace("federalgovernment", "canada");
                    final Element secondElement = tds.get(1);
                    final boolean isSameAge = secondElement.hasAttr("colspan") && Integer.parseInt(secondElement.attr("colspan")) == 2;

                    final String smokingAgeText = removeReferences(secondElement.text());
                    final CountryInfoValue smokingAge = new CountryInfoValue("Smoking Age", smokingAgeText, null);

                    final Element targetNoteElement = tds.get(isSameAge ? 2 : 3);
                    final String notes = getNotesFromElement(targetNoteElement);
                    final String purchasingAgeText;
                    if(isSameAge) {
                        purchasingAgeText = smokingAgeText;
                    } else {
                        final Element purchaseAge = tds.get(2);
                        purchasingAgeText = removeReferences(purchaseAge.text());
                    }
                    final CountryInfoValue purchasingAge = new CountryInfoValue("Purchasing Age", purchasingAgeText, null);

                    final CountryInfoKey info = new CountryInfoKey(title, notes, yearOfData, sources, smokingAge, purchasingAge);
                    final String string = info.toString();
                    countries.put(country, string);
                }
            }
        }
    }
}
