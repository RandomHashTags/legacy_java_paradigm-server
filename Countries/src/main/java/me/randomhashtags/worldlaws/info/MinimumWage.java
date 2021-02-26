package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum MinimumWage implements CountryInfoService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_MINIMUM_WAGE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_minimum_wage";
        final EventSource source = new EventSource("Wikipedia: List of countries by minimum wage", url);
        final EventSources sources = new EventSources(source);
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final String title = getInfo().getTitle();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
            final String notes = getNotesFromElement(tds.get(1)), notesLowercase = notes.toLowerCase();
            final boolean isNone = notesLowercase.startsWith("none") || notesLowercase.contains("has no minimum wage");
            final CountryInfoValue workweekHours = new CountryInfoValue("Workweek hours", tds.get(4).text(), null);

            final String annualValue = tds.get(2).text();
            final CountryInfoValue annual = new CountryInfoValue("Annual (USD)", annualValue.isEmpty() ? "Unknown" : isNone ? "None" : "$" + annualValue, null);

            final String hourlyValue = tds.get(5).text();
            final CountryInfoValue hourly = new CountryInfoValue("Hourly (USD)", hourlyValue.isEmpty() ? "Unknown" : isNone ? "None" : "$" + hourlyValue, null);

            final String effectivePer = tds.get(8).text();
            final String[] effectiveValues = effectivePer.split(" ");
            final int yearOfData = Integer.parseInt(effectiveValues[effectiveValues.length-1]);
            final CountryInfoKey info = new CountryInfoKey(title, notes, yearOfData, sources, workweekHours, annual, hourly);
            countries.put(country, info.toString());
        }
        handler.handle(null);
    }
}
