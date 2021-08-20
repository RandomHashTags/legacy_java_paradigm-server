package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.ServerObject;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateResource;
import me.randomhashtags.worldlaws.service.CountryServiceValue;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;

public enum CIAServices implements CountryService {
    INSTANCE;

    private HashMap<String, HashSet<SovereignStateResource>> countries;

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_CIA_VALUES;
    }

    @Override
    public void loadData(CompletionHandler handler) {
    }

    @Override
    public void getResources(String shortName, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        if(countries == null) {
            countries = new HashMap<>();
        }
        if(countries.containsKey(shortName)) {
            handler.handleObject(countries.get(shortName));
        } else {
            final Folder folder = getFolder();
            final String fileName = "CIA";
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadCIAValues(started, folder, shortName, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            final String value = "{\"" + shortName + "\":" + string + "}";
                            handler.handleString(value);
                        }
                    });
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json.has(shortName)) {
                        final CIAValues values = new CIAValues(json.getJSONObject(shortName));
                        final HashSet<SovereignStateResource> resources = getResourcesFrom(values);
                        countries.put(shortName, resources);
                        handler.handleObject(resources);
                    } else {
                        loadCIAValues(started, folder, shortName, new CompletionHandler() {
                            @Override
                            public void handleString(String string) {
                                final JSONObject ciaJSON = new JSONObject(string);
                                final CIAValues values = new CIAValues(ciaJSON);
                                final HashSet<SovereignStateResource> resources = getResourcesFrom(values);
                                countries.put(shortName, resources);
                                json.put(shortName, ciaJSON);
                                setFileJSONObject(folder, fileName, json);
                                handler.handleObject(resources);
                            }
                        });
                    }
                }
            });
        }
    }

    private HashSet<SovereignStateResource> getResourcesFrom(CIAValues ciaValues) {
        final String prefix = "https://www.cia.gov/the-world-factbook/static/";
        final HashSet<SovereignStateResource> set = new HashSet<>();
        set.add(new SovereignStateResource("CIA Summary", prefix + ciaValues.summaryURL));
        set.add(new SovereignStateResource("CIA Travel Facts", prefix + ciaValues.travelFactsURL));
        return set;
    }

    private void loadCIAValues(long started, Folder folder, String shortName, CompletionHandler handler) {
        String summaryURL = null, travelFactsURL = null, key = null;

        final String prefix = "https://www.cia.gov/";
        final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-").replace(",", "") + "/";
        final Elements elements = getDocumentElements(folder, url, "div.thee-link-container a");
        final String string;
        if(elements != null) {
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
            string = values.toServerJSON();
        } else {
            WLLogger.log(Level.WARN, "CIAServices - missing elements for country with short name \"" + shortName + "\", and url \"" + url + "\"!");
            string = null;
        }
        WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + shortName + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handleString(string);
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
            this.summaryURL = json.getString("Summary");
            this.travelFactsURL = json.getString("Travel Facts");
        }

        @Override
        public String toString() {
            final String prefix = "https://www.cia.gov/the-world-factbook/static/";
            final String string = "{" +
                    "\"Summary\":\"" + prefix + summaryURL + "/" + key + "-summary.pdf\"," +
                    "\"Travel Facts\":\"" + prefix + travelFactsURL + "/" + key + "-travel-facts.pdf\"" +
                    "}";
            return new CountryServiceValue(CIAServices.INSTANCE, string).toString();
        }

        @Override
        public String toServerJSON() {
            return "{" +
                    "\"key\":\"" + key + "\"," +
                    "\"Summary\":\"" + summaryURL + "\"," +
                    "\"Travel Facts\":\"" + travelFactsURL + "\"" +
                    "}";
        }
    }
}
