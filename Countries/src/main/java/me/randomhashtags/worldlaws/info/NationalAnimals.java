package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum NationalAnimals implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.NATIONAL_ANIMALS;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_national_animals";
        final String title = getInfo().getTitle();
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.select("td").size() < 2);
        final EventSource source = new EventSource("Wikipedia: List of national animals", url);
        final EventSources sources = new EventSources(source);
        final int yearOfData = WLUtilities.getTodayYear();
        String previousCountry = null;
        CountryInfoKey previousKey = null;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int size = tds.size();
            final Element firstElement = tds.get(0);
            final boolean hasRowspan = firstElement.hasAttr("rowspan");
            if(previousKey != null && hasRowspan) {
                countries.put(previousCountry, previousKey.toString());
                previousCountry = null;
                previousKey = null;
            }
            if(size == 4) {
                final CountryInfoValue value = getAnimal(firstElement);
                previousKey.addValue(value);
            } else {
                final String country = firstElement.select("a").get(0).text().toLowerCase().replace(" ", "");
                final CountryInfoValue animal = getAnimal(tds.get(1));
                final CountryInfoKey key = new CountryInfoKey(title, null, yearOfData, sources, animal);
                if(!hasRowspan) {
                    countries.put(country, key.toString());
                } else {
                    previousCountry = country;
                    previousKey = key;
                }
            }
        }
        if(previousKey != null) {
            countries.put(previousCountry, previousKey.toString());
        }
        handler.handle(null);
    }
    private CountryInfoValue getAnimal(Element nameElement) {
        final String text = nameElement.text();
        final boolean hasParenthesis = text.contains(" (");
        final String name = hasParenthesis ? text.split(" \\(")[0] : text;
        final String title = hasParenthesis ? text.split("\\(")[1].split("\\)")[0] : null;
        return new CountryInfoValue(title, name, null);
    }
}
