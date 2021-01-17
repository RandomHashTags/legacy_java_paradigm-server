package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum SystemOfGovernment implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries, styles, styleDescriptions;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_SYSTEM_OF_GOVERNMENT;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    private void loadStyles() {
        styles = new HashMap<>();
        styles.put("background:#CCDDEE", "Presidential Republic");
        styles.put("background:#FFFF66", "Semi-Presidential Republic");
        styles.put("background:#99FF99", "Republic with an executive presidency nominated by or elected by the legislature");
        styles.put("background:#FFD383", "Parliamentary Republic with a Ceremonial Presidency");
        styles.put("background:#FFD0EE", "Constitutional Monarchy");
        styles.put("background:#FFC0AA", "Constitutional Parliamentary Monarchy");
        styles.put("background:#DDAADD", "Absolute Monarchy");
        styles.put("background:#DDAA77", "One-party state");
        styles.put("background:#EEDDC3", "Unknown");

        styleDescriptions = new HashMap<>();
        styleDescriptions.put("Presidential Republic", "Head of state is also head of government and is independent of legislature");
        styleDescriptions.put("Semi-Presidential Republic", "Head of state has some executive powers and is independent of legislature; remaining executive power is vested in ministry that is subject to parliamentary confidence");
        styleDescriptions.put("Republic with an executive presidency nominated by or elected by the legislature", "President is both head of state and government; ministry, including the president, may or may not be subject to parliamentary confidence");
        styleDescriptions.put("Parliamentary Republic with a Ceremonial Presidency", "Head of state is ceremonial; ministry is subject to parliamentary confidence");
        styleDescriptions.put("Constitutional Monarchy", "Head of state is executive; Monarch personally exercises power in concert with other institutions");
        styleDescriptions.put("Constitutional Parliamentary Monarchy", "Head of state is ceremonial; ministry is subject to parliamentary confidence");
        styleDescriptions.put("Absolute Monarchy", "Head of state is executive; all authority vested in absolute monarch");
        styleDescriptions.put("One-party state", "Head of state is executive or ceremonial; power constitutionally linked to a single political movement");
    }

    @Override
    public void refresh(CompletionHandler handler) {
        loadStyles();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government";
        final Elements tables = getDocumentElements(url, "div.mw-parser-output table.wikitable");
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: List of countries by system of government", url);
        final EventSources sources = new EventSources(source);
        load(title, tables.get(0), sources); // UN member states and observers
        load(title, tables.get(1), sources); // Partially recognized states
        load(title, tables.get(2), sources); // Unrecognized states
        handler.handle(null);
    }

    private void load(String title, Element table, EventSources sources) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);

        for(Element element : trs) {
            final String style = getStyle(element), styleDescription = getStyleDescription(style);
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(",", "").replace(" ", "").split("people'srepublicof")[0];
            final String notes = getNotesFromElement(tds.get(3));
            final CountrySingleValue value = new CountrySingleValue(title, notes, style, styleDescription, -1, sources);
            countries.put(country, value.toString());
        }
    }

    private String getStyle(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }
    private String getStyleDescription(String string) {
        return styleDescriptions.getOrDefault(string, "Unknown");
    }
}
