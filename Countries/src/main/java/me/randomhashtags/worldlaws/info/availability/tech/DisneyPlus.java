package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.json.JSONArray;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;

public enum DisneyPlus implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_DISNEY_PLUS;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_STREAMING;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();

        getJSONArray(this, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://en.wikipedia.org/wiki/Disney%2B";
                final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable").get(0).select("tbody tr");
                trs.remove(0);

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final Elements tds = element.select("td");
                    final String targetCountry = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
                    final HashSet<String> countries = getCountriesFromText(targetCountry);
                    for(String country : countries) {
                        builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                        isFirst = false;
                    }
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                final String url = "https://en.wikipedia.org/wiki/Disney%2B";
                final EventSource source = new EventSource("Wikipedia: Disney+", url);
                final EventSources sources = new EventSources(source);
                final String value = getAvailability(true).toString();
                for(Object obj : array) {
                    final String country = (String) obj;
                    countries.put(country, value);
                }
                handler.handle(null);
            }
        });
    }
}
