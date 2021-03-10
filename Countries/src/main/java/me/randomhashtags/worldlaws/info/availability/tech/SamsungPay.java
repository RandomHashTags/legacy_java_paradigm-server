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

public enum SamsungPay implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_SAMSUNG_PAY;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.PAYMENT_METHOD;
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
                final String url = "https://en.wikipedia.org/wiki/Samsung_Pay";
                final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
                trs.remove(0);
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final Elements tds = element.select("td");
                    final int size = tds.size();
                    final String country = tds.get(1-(size == 1 ? 1 : 0)).select("a").get(1).text().toLowerCase().replace(" ", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                final String url = "https://en.wikipedia.org/wiki/Samsung_Pay";
                final EventSource source = new EventSource("Wikipedia: Samsung Pay", url);
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
