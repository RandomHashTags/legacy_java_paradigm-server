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

public enum Tidal implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_TIDAL;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_MUSIC;
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
                final String url = "https://support.tidal.com/hc/en-us/articles/202453191-Which-countries-is-TIDAL-available-";
                final Elements trs = getAvailabilityDocumentElements(url, "section.categories-wrapper div ul li.txt-content", 0).select("p");
                for(int i = 0; i < 2; i++) {
                    trs.remove(0);
                }
                trs.remove(trs.size()-1);
                trs.remove(trs.size()-1);

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final String country = element.text().toLowerCase().replace(" ", "").replace("ofamerica", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                final String url = "https://support.tidal.com/hc/en-us/articles/202453191-Which-countries-is-TIDAL-available-";
                final EventSource source = new EventSource("Tidal", url);
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
