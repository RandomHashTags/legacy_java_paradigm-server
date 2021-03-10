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

public enum YouTubePremium implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_YOUTUBE_PREMIUM;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT;
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
                final String url = "https://en.wikipedia.org/wiki/YouTube_Premium";
                final Elements lis = getAvailabilityDocumentElements(url, "div.mw-parser-output table.infobox", 0).select("tbody tr td div.NavFrame ul.NavContent li");

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element li : lis) {
                    final String country = li.select("a").get(0).text().toLowerCase().replace(" ", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                final String url = "https://en.wikipedia.org/wiki/YouTube_Premium";
                final EventSource source = new EventSource("Wikipedia: YouTube Premium", url);
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
