package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum CIAServices implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_CIA_VALUES;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return null;
    }

    @Override
    public void refresh(CompletionHandler handler) {
    }

    @Override
    public void getValue(String shortName, CompletionHandler handler) {
        if(countries == null) {
            countries = new HashMap<>();
        }
        if(countries.containsKey(shortName)) {
            handler.handle(countries.get(shortName));
        } else {
            String summaryURL = null, travelFactsURL = null;

            final String prefix = "https://www.cia.gov/";
            final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-") + "/";
            final Elements elements = getDocumentElements(url, "div.thee-link-container a");
            for(Element element : elements) {
                final String href = element.attr("href");
                if(href.endsWith("summary.pdf")) {
                    summaryURL = prefix + href;
                } else if(href.endsWith("travel-facts.pdf")) {
                    travelFactsURL = prefix + href;
                }
            }
            final CIAValues values = new CIAValues(summaryURL, travelFactsURL);
            final String string = values.toString();
            countries.put(shortName, string);
            handler.handle(string);
        }
    }

    private final class CIAValues {
        private final String summaryURL, travelFactsURL;

        CIAValues(String summaryURL, String travelFactsURL) {
            this.summaryURL = summaryURL;
            this.travelFactsURL = travelFactsURL;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"summaryURL\":\"" + summaryURL + "\"," +
                    "\"travelFactsURL\":\"" + travelFactsURL + "\"" +
                    "}";
        }
    }
}
