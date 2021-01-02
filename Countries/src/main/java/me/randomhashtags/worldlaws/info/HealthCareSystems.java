package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public enum HealthCareSystems implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_HEALTH_CARE_SYSTEM;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            load(new CompletionHandler() {
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
            WLLogger.log(Level.WARNING, "HealthCareSystems - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void load(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Health_care_systems_by_country";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Element output = doc.select("div.mw-parser-output").get(0);
            final Elements trs = output.select("div.div-col");
            for(int i = 0; i < trs.size()-5; i++) {
                trs.remove(5);
            }
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Health care systems by country", url));
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
            for(Element element : trs) {
                final Elements list = element.select("ul li");
                final String type = types.get(index), note = notes.get(index);
                for(Element li : list) {
                    final List<TextNode> textNodes = li.textNodes();
                    String description = textNodes.size() != 1 ? null : textNodes.get(0).text().split("\\)")[0];
                    if(description != null && description.contains("(")) {
                        description = description.split("\\(")[1];
                    }
                    final CountryInfoValue value = new CountryInfoValue(title, type, description);
                    final CountryInfoKey key = new CountryInfoKey(null, note, sources, value);
                    for(Element a : li.select("a")) {
                        final String country = a.text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
                        countries.put(country, key.toString());
                    }
                }
                index += 1;
            }
            WLLogger.log(Level.INFO, "HealthCareSystems - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
