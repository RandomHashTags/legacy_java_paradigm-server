package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum AgeStructure implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.INFO_AGE_STRUCTURE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_age_structure", title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: List of countries by age structure", url);
        final EventSources sources = new EventSources(source);
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final int yearOfData = 2017;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
            final CountryInfoValue zeroTo14 = new CountryInfoValue("0-14", tds.get(1).text().replace(" ", ""), "Children and adolescents");
            final CountryInfoValue fifteenTo64 = new CountryInfoValue("15-64", tds.get(2).text().replace(" ", ""), "Working population or population in education");
            final CountryInfoValue over65 = new CountryInfoValue("65+", tds.get(3).text().replace(" ", ""), "Retirees and elderly");

            final CountryInfoKey info = new CountryInfoKey(title, null, yearOfData, sources, zeroTo14, fifteenTo64, over65);
            countries.put(country, info.toString());
        }
        handler.handle(null);
    }
}
