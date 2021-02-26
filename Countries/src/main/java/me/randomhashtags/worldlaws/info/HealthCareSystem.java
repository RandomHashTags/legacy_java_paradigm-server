package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

public enum HealthCareSystem implements CountryValueService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_HEALTH_CARE_SYSTEM;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Health_care_systems_by_country";
        final Element output = getValueDocumentElements(url, "div.mw-parser-output").get(0);
        final Elements trs = output.select("div.div-col");
        for(int i = 0; i < trs.size()-5; i++) {
            trs.remove(5);
        }
        final EventSource source = new EventSource("Wikipedia: Health care systems by country", url);
        final EventSources sources = new EventSources(source);
        final HashMap<Integer, String> types = new HashMap<>() {{
            put(0, "Universal Government-Funded");
            put(1, "Universal Public Insurance");
            put(2, "Universal Public-Private Insurance");
            put(3, "Universal Private Insurance");
            put(4, "Non-Universal Insurance");
        }};
        final HashMap<Integer, String> notes = new HashMap<>();
        int index = 0;
        final Elements allElements = output.getAllElements();;
        for(Element tr : trs) {
            final int indexOf = allElements.indexOf(tr);
            final Element three = allElements.get(indexOf-3);
            notes.put(index, (three.tagName().equals("p") ? three : allElements.get(indexOf-2)).text().replace(":", "."));
            index += 1;
        }
        final String title = getInfo().getTitle();
        index = 0;
        final int yearOfData = 2019;
        for(Element element : trs) {
            final Elements list = element.select("ul li");
            final String type = types.get(index), note = notes.get(index);
            for(Element li : list) {
                final List<TextNode> textNodes = li.textNodes();
                String description = textNodes.size() != 1 ? null : textNodes.get(0).text().split("\\)")[0];
                if(description != null && description.contains("(")) {
                    description = description.split("\\(")[1];
                }
                final String value = new CountrySingleValue(title, note, type, description, yearOfData, sources).toString();
                for(Element href : li.select("a")) {
                    final String country = href.text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
                    countries.put(country, value);
                }
            }
            index += 1;
        }
        handler.handle(null);
    }
}
