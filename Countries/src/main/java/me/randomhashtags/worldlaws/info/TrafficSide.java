package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum TrafficSide implements CountryValueService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_TRAFFIC_SIDE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Left-_and_right-hand_traffic", title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: Left- and right-hand traffic", url);
        final EventSources sources = new EventSources(source);
        final Elements trs = getValueDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final int yearOfData = WLUtilities.getTodayYear();
        String previousCountry = null;
        int rowspanCount = 0;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final Element firstElement = tds.get(0), notesElement = tds.get(tds.size()-1);
            final String notes = getNotesFromElement(notesElement), secondElementText = tds.get(1).text();
            final Elements links = firstElement.select("a");
            if(rowspanCount != 0) {
                rowspanCount -= 1;
                final boolean isMainland = links.size() == 0 && firstElement.text().equalsIgnoreCase("Mainland");
                final String country = isMainland ? previousCountry : links.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
                addCountry(country, secondElementText, title, notes, yearOfData, sources);
            } else {
                final String country = links.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "").replace(".", "");
                if(firstElement.hasAttr("rowspan")) {
                    rowspanCount = Integer.parseInt(firstElement.attr("rowspan"));
                    if(secondElementText.equalsIgnoreCase("Mainland")) {
                        previousCountry = country;
                    }
                    addCountry(country, tds.get(2).text(), title, notes, yearOfData, sources);
                } else {
                    addCountry(country, secondElementText, title, notes, yearOfData, sources);
                }
            }
        }
        handler.handle(null);
    }
    private void addCountry(String country, String value, String title, String notes, int yearOfData, EventSources sources) {
        final String realValue = value.startsWith("RHT") ? "Right" : "Left";
        final CountrySingleValue singleValue = new CountrySingleValue(title, notes, realValue, null, yearOfData, sources);
        countries.put(country, singleValue.toString());
    }
}
