package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum CIAServices implements CountryService {
    INSTANCE;

    private final HashMap<String, String> backgrounds;

    CIAServices() {
        backgrounds = new HashMap<>();
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_NONSTATIC;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_CIA_VALUES;
    }

    @Override
    public String loadData() {
        loadBackground();
        return null;
    }

    @Override
    public String getCountryValue(String countryBackendID) {
        String string = backgrounds.get(countryBackendID.replace(" ", "").toLowerCase());
        if(string != null) {
            string = "\"CIA\":{" +
                    "\"background\":\"" + string + "\"" +
                    "}";
        }
        return string;
    }

    @Override
    public EventSources getResources(String shortName) {
        final TravelValues values = loadTravelValues(shortName);
        return getResourcesFrom(values);
    }

    private EventSources getResourcesFrom(TravelValues travelValues) {
        final String domain = "https://www.cia.gov";
        return new EventSources(
                new EventSource("CIA Summary", domain + travelValues.summaryURL),
                new EventSource("CIA Travel Facts", domain + travelValues.travelFactsURL)

        );
    }

    private void loadBackground() {
        final String url = "https://www.cia.gov/the-world-factbook/field/background";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.col-lg-6 ul li");
            elements.removeIf(element -> element.selectFirst("a[href]") == null);
            for(Element element : elements) {
                final Element hrefElement = element.selectFirst("a[href]");
                final String country = hrefElement.text().split("\\(")[0].toLowerCase().replace(" ", "").replace(",", "");
                final StringBuilder builder = new StringBuilder();
                boolean isFirst = true;
                for(Element paragraphElement : element.select("p")) {
                    final String text = paragraphElement.text();
                    if(!text.isEmpty()) {
                        builder.append(isFirst ? "" : "\n\n").append(text);
                        isFirst = false;
                    }
                }
                final String string = LocalServer.fixEscapeValues(builder.toString());
                backgrounds.put(country, string);
            }
        }
    }

    private TravelValues loadTravelValues(String shortName) {
        final String prefix = "https://www.cia.gov/";
        final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-").replace(",", "") + "/";
        final String missingMessage = "CIAServices - missing elements for country with short name \"" + shortName + "\", and url \"" + url + "\"!";
        final Document doc = getDocument(Folder.OTHER, url, false);
        TravelValues travelValues = null;
        if(doc != null) {
            final Elements elements = doc.select("div.thee-link-container a");
            if(!elements.isEmpty()) {
                String summaryURL = null, travelFactsURL = null;
                for(Element element : elements) {
                    final String href = element.attr("href");
                    if(href.endsWith("-summary.pdf")) {
                        summaryURL = href;
                    } else if(href.endsWith("-travel-facts.pdf")) {
                        travelFactsURL = href;
                    }
                }
                travelValues = new TravelValues(summaryURL, travelFactsURL);
            } else {
                WLLogger.logError(this, missingMessage);
            }
        } else {
            WLLogger.logError(this, missingMessage);
        }
        return travelValues;
    }

    private final class TravelValues {
        private final String summaryURL, travelFactsURL;

        private TravelValues(String summaryURL, String travelFactsURL) {
            this.summaryURL = summaryURL;
            this.travelFactsURL = travelFactsURL;
        }
    }
}
