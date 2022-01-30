package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum CIAServices implements CountryService {
    INSTANCE;
    // Natural Resources = https://www.cia.gov/the-world-factbook/field/natural-resources/
    // Natural Hazards = https://www.cia.gov/the-world-factbook/field/natural-hazards/
    // Current Environment Issues = https://www.cia.gov/the-world-factbook/field/environment-current-issues


    private final HashMap<String, String> backgrounds, flagDetails, nationalAnthems;

    CIAServices() {
        backgrounds = new HashMap<>();
        flagDetails = new HashMap<>();
        nationalAnthems = new HashMap<>();
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
        final String identifier = countryBackendID.replace(" ", "").toLowerCase();
        final JSONObject json = new JSONObject();
        if(backgrounds.containsKey(identifier)) {
            json.put("background", backgrounds.get(identifier));
        }
        if(flagDetails.containsKey(identifier)) {
            json.put("flagDetails", flagDetails.get(identifier));
        }
        if(nationalAnthems.containsKey(identifier)) {
            json.put("nationalAnthemURL", nationalAnthems.get(identifier));
        }
        return json.isEmpty() ? null : "\"CIA\":" + json.toString();
    }

    @Override
    public EventSources getResources(String shortName) {
        final TravelValues values = loadTravelValues(shortName);
        return getResourcesFrom(shortName, values);
    }

    private EventSources getResourcesFrom(String shortName, TravelValues travelValues) {
        final String domain = "https://www.cia.gov";
        return new EventSources(
                new EventSource("CIA: " + shortName, travelValues.ciaCountryURL),
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
        final String domain = "https://www.cia.gov/";
        final String identifier = shortName.toLowerCase().replace(" ", "").replace(",", "");
        final String url = domain + "the-world-factbook/countries/" + identifier + "/";
        final Document doc = getDocument(Folder.OTHER, url, false);
        TravelValues travelValues = null;
        String flagURL = null;
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
                    } else if(href.endsWith("/flag")) {
                        flagURL = href;
                    }
                }
                travelValues = new TravelValues(url, summaryURL, travelFactsURL);
            }
            final Element nationalAnthemElement = doc.selectFirst("span.card-exposed__text div audio");
            if(nationalAnthemElement != null) {
                final String nationalAnthemURL = domain + nationalAnthemElement.attr("src").substring(1);
                nationalAnthems.put(identifier, nationalAnthemURL);
            }
        }
        if(travelValues == null) {
            final String missingMessage = "CIAServices - missing elements for country with short name \"" + shortName + "\", and url \"" + url + "\"!";
            WLLogger.logError(this, missingMessage);
        } else {
            if(flagURL != null) {
                final Document flagDoc = getDocument(domain + flagURL.substring(1));
                if(flagDoc != null) {
                    final Elements paragraphs = flagDoc.selectFirst("div.mb0").select("p");
                    final StringBuilder flagDescription = new StringBuilder();
                    boolean isFirst = true;
                    for(Element element : paragraphs) {
                        flagDescription.append(isFirst ? "" : "\n\n").append(element.text());
                        isFirst = false;
                    }
                    final String string = LocalServer.fixEscapeValues(flagDescription.toString());
                    flagDetails.put(identifier, string);
                }
            }
        }
        return travelValues;
    }

    private final class TravelValues {
        private final String ciaCountryURL, summaryURL, travelFactsURL;

        private TravelValues(String ciaCountryURL, String summaryURL, String travelFactsURL) {
            this.ciaCountryURL = ciaCountryURL;
            this.summaryURL = summaryURL;
            this.travelFactsURL = travelFactsURL;
        }
    }
}
