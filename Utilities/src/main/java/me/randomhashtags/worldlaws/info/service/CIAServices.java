package me.randomhashtags.worldlaws.info.service;

import org.apache.logging.log4j.Level;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum CIAServices implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public FileType getFileType() {
        return FileType.COUNTRIES_SERVICES_CIA;
    }

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
        final long started = System.currentTimeMillis();
        if(countries == null) {
            countries = new HashMap<>();
        }
        if(countries.containsKey(shortName)) {
            handler.handle(countries.get(shortName));
        } else {
            final FileType fileType = getFileType();
            getJSONObject(fileType, shortName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    String summaryURL = null, travelFactsURL = null;

                    final String prefix = "https://www.cia.gov/";
                    final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-") + "/";
                    final Elements elements = getDocumentElements(fileType, url, "div.thee-link-container a");
                    for(Element element : elements) {
                        final String href = element.attr("href");
                        if(href.endsWith("-summary.pdf")) {
                            summaryURL = prefix + href.substring(1);
                        } else if(href.endsWith("-travel-facts.pdf")) {
                            travelFactsURL = prefix + href.substring(1);
                        }
                    }
                    final CIAValues values = new CIAValues(summaryURL, travelFactsURL);
                    final String string = values.toString();
                    handler.handle(string);
                }

                @Override
                public void handleJSONObject(JSONObject object) {
                    final CIAValues values = new CIAValues(object);
                    final String string = values.toString();
                    countries.put(shortName, string);
                    WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + shortName + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(string);
                }
            });
        }
    }

    private final class CIAValues {
        private final String summaryURL, travelFactsURL;

        private CIAValues(String summaryURL, String travelFactsURL) {
            this.summaryURL = summaryURL;
            this.travelFactsURL = travelFactsURL;
        }
        private CIAValues(JSONObject json) {
            summaryURL = json.getString("summaryURL");
            travelFactsURL = json.getString("travelFactsURL");
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
