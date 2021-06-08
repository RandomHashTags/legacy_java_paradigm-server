package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.ServerObject;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CountryInformationType;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum CIAServices implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInformationType getInformationType() {
        return CountryInformationType.SERVICES;
    }

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_CIA_VALUES;
    }

    @Override
    public void loadData(CompletionHandler handler) {
    }

    @Override
    public void getCountryValue(String shortName, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        if(countries == null) {
            countries = new HashMap<>();
        }
        if(countries.containsKey(shortName)) {
            handler.handle(countries.get(shortName));
        } else {
            final FileType fileType = getFileType();
            final String fileName = "CIA";
            getJSONObject(fileType, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadCIAValues(started, fileType, shortName, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final String string = "{\"" + shortName + "\":" + object.toString() + "}";
                            handler.handle(string);
                        }
                    });
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json.has(shortName)) {
                        final String string = new CIAValues(json.getJSONObject(shortName)).toString();
                        countries.put(shortName, string);
                        handler.handle(string);
                    } else {
                        loadCIAValues(started, fileType, shortName, new CompletionHandler() {
                            @Override
                            public void handle(Object object) {
                                final JSONObject ciaJSON = new JSONObject(object.toString());
                                final CIAValues values = new CIAValues(ciaJSON);
                                final String string = values.toString();
                                countries.put(shortName, string);
                                json.put(shortName, ciaJSON);
                                setFileJSONObject(fileType, fileName, json);
                                handler.handle(string);
                            }
                        });
                    }
                }
            });
        }
    }
    private void loadCIAValues(long started, FileType fileType, String shortName, CompletionHandler handler) {
        String summaryURL = null, travelFactsURL = null, key = null;

        final String prefix = "https://www.cia.gov/";
        final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-") + "/";
        final Elements elements = getDocumentElements(fileType, url, "div.thee-link-container a");
        for(Element element : elements) {
            final String href = element.attr("href");
            if(href.endsWith("-summary.pdf")) {
                final String text = href.split("/static/")[1];
                final String[] values = text.split("/");
                summaryURL = values[0];
                key = values[1].split("-")[0];
            } else if(href.endsWith("-travel-facts.pdf")) {
                final String text = href.split("/static/")[1];
                final String[] values = text.split("/");
                travelFactsURL = values[0];
                key = values[1].split("-")[0];
            }
        }
        final CIAValues values = new CIAValues(key, summaryURL, travelFactsURL);
        final String string = values.toServerJSON();
        WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + shortName + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(string);
    }

    private final class CIAValues implements ServerObject {
        private final String key, summaryURL, travelFactsURL;

        private CIAValues(String key, String summaryURL, String travelFactsURL) {
            this.key = key;
            this.summaryURL = summaryURL;
            this.travelFactsURL = travelFactsURL;
        }
        private CIAValues(JSONObject json) {
            this.key = json.getString("key");
            this.summaryURL = json.getString("summary");
            this.travelFactsURL = json.getString("travelFacts");
        }

        @Override
        public String toString() {
            final String prefix = "https://www.cia.gov/the-world-factbook/static/";
            final String string = "{" +
                    "\"summaryURL\":\"" + prefix + summaryURL + "/" + key + "-summary.pdf\"," +
                    "\"travelFactsURL\":\"" + prefix + travelFactsURL + "/" + key + "-travel-facts.pdf\"" +
                    "}";
            return new CountryServiceValue(CIAServices.INSTANCE, string).toString();
        }

        @Override
        public String toServerJSON() {
            return "{" +
                    "\"key\":\"" + key + "\"," +
                    "\"summary\":\"" + summaryURL + "\"," +
                    "\"travelFacts\":\"" + travelFactsURL + "\"" +
                    "}";
        }
    }
}
