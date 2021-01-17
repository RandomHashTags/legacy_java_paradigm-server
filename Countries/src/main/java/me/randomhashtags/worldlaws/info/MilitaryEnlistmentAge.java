package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum MilitaryEnlistmentAge implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_MILITARY_ENLISTMENT_AGE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_enlistment_age_by_country";
        final Elements lists = getDocumentElements(url, "h2 + ul li");
        lists.remove(lists.size()-1);
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: List of enlistment age by country", url);
        final EventSources sources = new EventSources(source);
        final int yearOfData = 2020;
        for(Element element : lists) {
            final String country = element.select("a").get(0).text().toLowerCase().replace(" ", "");
            final int substring = country.length() + 3;
            final String text = removeReferences(element.text().substring(substring));
            final CountrySingleValue value = new CountrySingleValue(title, null, text, null, yearOfData, sources);
            countries.put(country, value.toString());
        }
        handler.handle(null);
    }
}
