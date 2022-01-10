package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public enum CIAServices implements CountryService {
    INSTANCE;

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_STATIC;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_CIA_VALUES;
    }

    @Override
    public void loadData(CompletionHandler handler) {
    }

    @Override
    public EventSources getResources(String shortName) {
        final CIAValues values = loadCIAValues(shortName);
        return getResourcesFrom(values);
    }

    private EventSources getResourcesFrom(CIAValues ciaValues) {
        final String domain = "https://www.cia.gov";
        return new EventSources(
                new EventSource("CIA Summary", domain + ciaValues.summaryURL),
                new EventSource("CIA Travel Facts", domain + ciaValues.travelFactsURL)

        );
    }

    private CIAValues loadCIAValues(String shortName) {
        final String prefix = "https://www.cia.gov/";
        final String url = prefix + "the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-").replace(",", "") + "/";
        final String missingMessage = "CIAServices - missing elements for country with short name \"" + shortName + "\", and url \"" + url + "\"!";
        final Document doc = getDocument(Folder.OTHER, url, false);
        CIAValues ciaValues = null;
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
                ciaValues = new CIAValues(summaryURL, travelFactsURL);
            } else {
                WLLogger.logError(this, missingMessage);
            }
        } else {
            WLLogger.logError(this, missingMessage);
        }
        return ciaValues;
    }

    private final class CIAValues {
        private final String summaryURL, travelFactsURL;

        private CIAValues(String summaryURL, String travelFactsURL) {
            this.summaryURL = summaryURL;
            this.travelFactsURL = travelFactsURL;
        }
    }
}
