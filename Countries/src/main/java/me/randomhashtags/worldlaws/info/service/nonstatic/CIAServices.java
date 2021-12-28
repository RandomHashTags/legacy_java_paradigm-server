package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.ServerObject;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateResource;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.service.CountryServiceValue;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;

public enum CIAServices implements CountryService {
    INSTANCE;

    private final HashMap<String, HashSet<SovereignStateResource>> countries;

    CIAServices() {
        countries = new HashMap<>();
    }

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
        if(countries.containsKey(shortName)) {
            handler.handleObject(countries.get(shortName));
        } else {
            final String string = loadCIAValues(started, shortName);
            final JSONObject ciaJSON = string != null ? new JSONObject(string) : new JSONObject();
            final CIAValues values = new CIAValues(ciaJSON);
            final HashSet<SovereignStateResource> resources = getResourcesFrom(values);
            countries.put(shortName, resources);
            handler.handleObject(resources);
        }
    }

    private HashSet<SovereignStateResource> getResourcesFrom(CIAValues ciaValues) {
        final String prefix = "https://www.cia.gov/the-world-factbook/static/";
        final HashSet<SovereignStateResource> set = new HashSet<>();
        set.add(new SovereignStateResource("CIA Summary", prefix + ciaValues.summaryURL));
        set.add(new SovereignStateResource("CIA Travel Facts", prefix + ciaValues.travelFactsURL));
        return set;
    }

    private String loadCIAValues(long started, String shortName) {
        String summaryURL = null, travelFactsURL = null, key = null;

        final String prefix = "https://www.cia.gov/";
        final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-").replace(",", "") + "/";
        final String missingMessage = "CIAServices - missing elements for country with short name \"" + shortName + "\", and url \"" + url + "\"!";
        final Document doc = getDocument(Folder.OTHER, url, false);
        String string = null;
        if(doc != null) {
            final Elements elements = doc.select("div.thee-link-container a");
            if(!elements.isEmpty()) {
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
                WLLogger.logInfo(getInfo().name() + " - loaded \"" + shortName + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
            } else {
                WLLogger.logError(this, missingMessage);
            }
        } else {
            WLLogger.logError(this, missingMessage);
        }
        return string;
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
