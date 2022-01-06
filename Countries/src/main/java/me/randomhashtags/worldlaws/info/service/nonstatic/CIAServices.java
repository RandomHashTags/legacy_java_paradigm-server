package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateResource;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

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
    public HashSet<SovereignStateResource> getResources(String shortName) {
        final CIAValues values = loadCIAValues(shortName);
        return getResourcesFrom(values);
    }

    private HashSet<SovereignStateResource> getResourcesFrom(CIAValues ciaValues) {
        final String domain = "https://www.cia.gov";
        final HashSet<SovereignStateResource> set = new HashSet<>();
        set.add(new SovereignStateResource("CIA Summary", domain + ciaValues.summaryURL));
        set.add(new SovereignStateResource("CIA Travel Facts", domain + ciaValues.travelFactsURL));
        return set;
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
