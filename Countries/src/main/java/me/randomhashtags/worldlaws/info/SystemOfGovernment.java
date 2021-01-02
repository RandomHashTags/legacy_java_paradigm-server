package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum SystemOfGovernment implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.GOVERNMENT_SYSTEM;
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
            WLLogger.log(Level.WARNING, "SystemOfGovernment - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            load(url, tables.get(0)); // UN member states and observers
            load(url, tables.get(1)); // Partially recognized states
            load(url, tables.get(2)); // Unrecognized states
            WLLogger.log(Level.INFO, "SystemOfGovernment - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    private void load(String url, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        final EventSources sources = new EventSources(new EventSource("Wikipedia: List of national capitals", url));
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(",", "").replace(" ", "").split("people'srepublicof")[0];
            final CountryInfoValue constitutionalForm = new CountryInfoValue("Constitutional Form", tds.get(1).text(), null);
            final CountryInfoValue headOfState = new CountryInfoValue("Head of State", tds.get(2).text(), null);
            final CountryInfoKey info = new CountryInfoKey("System of Government", tds.get(3).text(), sources, constitutionalForm, headOfState);
            countries.put(country, info.toString());
        }
    }
}
