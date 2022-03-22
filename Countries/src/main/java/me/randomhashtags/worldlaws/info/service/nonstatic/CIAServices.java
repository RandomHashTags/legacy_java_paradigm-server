package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryServiceNonStatic;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public enum CIAServices implements NewCountryServiceNonStatic {
    INSTANCE;
    // Telecommunication Systems = https://www.cia.gov/the-world-factbook/field/telecommunication-systems/

    private final HashMap<String, String> nationalAnthems;
    private final HashMap<String, TravelValues> travelValues;
    private final HashMap<String, HashMap<String, String>> values;

    CIAServices() {
        nationalAnthems = new HashMap<>();
        travelValues = new HashMap<>();
        values = new HashMap<>();
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_CIA_VALUES;
    }

    @Override
    public JSONObjectTranslatable getJSONObject(WLCountry country) {
        final String countryBackendID = country.getBackendID();
        final String identifier = countryBackendID.replace(" ", "").toLowerCase();
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(values.containsKey(identifier)) {
            for(Map.Entry<String, String> map : values.get(identifier).entrySet()) {
                json.put(map.getKey(), map.getValue());
            }
        }
        return json;
    }

    @Override
    public EventSources getResources(WLCountry country) {
        final String shortName = country.getShortName();
        final TravelValues values = getTravelValues(shortName);
        return getResourcesFrom(shortName, values);
    }

    private EventSources getResourcesFrom(String shortName, TravelValues travelValues) {
        if(travelValues == null) {
            return null;
        }
        final String domain = "https://www.cia.gov";
        return new EventSources(
                new EventSource("CIA: " + shortName, travelValues.ciaCountryURL),
                new EventSource("CIA Summary", domain + travelValues.summaryURL),
                new EventSource("CIA Travel Facts", domain + travelValues.travelFactsURL)
        );
    }

    private TravelValues getTravelValues(String shortName) {
        if(!travelValues.containsKey(shortName)) {
            loadTravelValues(shortName);
        }
        return travelValues.get(shortName);
    }
    private TravelValues loadTravelValues(String shortName) {
        final String domain = "https://www.cia.gov/";
        final String identifier = shortName.toLowerCase().replace(" ", "-").replace(",", "");
        final String countryBackendID = identifier.replace("-", "");
        final String url = domain + "the-world-factbook/countries/" + identifier;
        final Document doc = getDocument(Folder.OTHER, url, false);
        TravelValues travelValues = null;
        if(doc != null) {
            final Elements hrefs = doc.select("div.thee-link-container a[href]");
            if(!hrefs.isEmpty()) {
                String summaryURL = null, travelFactsURL = null;
                for(Element element : hrefs) {
                    final String href = element.attr("href");
                    if(href.endsWith("-summary.pdf")) {
                        summaryURL = href;
                    } else if(href.endsWith("-travel-facts.pdf")) {
                        travelFactsURL = href;
                    }
                }
                travelValues = new TravelValues(url, summaryURL, travelFactsURL);
                this.travelValues.put(shortName, travelValues);
            }
            final Element nationalAnthemElement = doc.selectFirst("span.card-exposed__text div audio");
            /*if(nationalAnthemElement != null) {
                final String nationalAnthemURL = domain + nationalAnthemElement.attr("src").substring(1);
                nationalAnthems.put(identifier, nationalAnthemURL);
            }*/

            final Elements elements = doc.select("div.container div.row div.col-lg-6 div.free-form-content__content");
            for(Element element : elements) {
                final String id = element.attr("id");
                final HashMap<String, String> keys = new HashMap<>();
                switch (id) {
                    case "introduction":
                        keys.put("Background", "background");
                        break;
                    case "geography":
                        keys.put("Natural hazards", "naturalHazards");
                        keys.put("Natural resources", "naturalResources");
                        keys.put("Geography - note", "geographyNotes");
                        break;
                    case "people-and-society":
                        keys.put("Median age", "medianAge");
                        keys.put("Population growth rate", "populationGrowthRate");
                        keys.put("Population distribution", "populationDistribution");
                        keys.put("Obesity - adult prevalence rate", "obesityRate");
                        keys.put("People - note", "peopleNotes");
                        break;
                    case "environment":
                        keys.put("Environment - current issues", "environmentCurrentIssues");
                        keys.put("Climate", "climate");
                        keys.put("Major infectious diseases", "majorInfectiousDiseases");
                        break;
                    case "government":
                        keys.put("Citizenship", "citizenship");
                        keys.put("Flag description", "flagDescription");
                        keys.put("National symbol(s)", "nationalSymbols");
                        break;
                    case "economy":
                        keys.put("Economic overview", "economicOverview");
                        keys.put("Agricultural products", "agriculturalProducts");
                        keys.put("Exports - partners", "exportPartners");
                        keys.put("Imports - partners", "importPartners");
                        break;
                    case "energy":
                        break;
                    case "communications":
                        break;
                    case "transportation":
                        break;
                    case "military-and-security":
                        keys.put("Military service age and obligation", "militaryAgeAndObligations");
                        break;
                    case "transnational-issues":
                        keys.put("Disputes - international", "internationalDisputes");
                        keys.put("Illicit drugs", "drugsDescription");
                        break;
                    default:
                        break;
                }
                if(!keys.isEmpty()) {
                    loadTest(countryBackendID, element, keys);
                }
            }
        }
        if(travelValues == null) {
            final String missingMessage = "CIAServices - missing elements for country with short name \"" + shortName + "\", and url \"" + url + "\"!";
            WLLogger.logError(this, missingMessage);
        }
        return travelValues;
    }
    private void loadTest(String country, Element element, HashMap<String, String> keys) {
        final Elements divs = element.select("div");
        for(Element div : divs) {
            final Element headerElement = div.selectFirst("h3");
            final String header = headerElement != null ? headerElement.text() : null;
            if(header != null && keys.containsKey(header)) {
                final Elements paragraphs = div.select("p");
                if(!paragraphs.isEmpty()) {
                    final StringBuilder builder = new StringBuilder();
                    boolean isFirst = true;
                    for(Element paragraphElement : paragraphs) {
                        final String text = paragraphElement.text();
                        if(!text.isEmpty()
                                && !text.equalsIgnoreCase("NA")
                                && !text.equalsIgnoreCase("none")
                                && !text.equalsIgnoreCase("not available")
                                && !text.startsWith("no economic activity")
                        ) {
                            builder.append(isFirst ? "" : "\n\n").append(text);
                            isFirst = false;
                        }
                    }
                    final String string = LocalServer.fixEscapeValues(builder.toString());
                    if(!string.isEmpty()) {
                        values.putIfAbsent(country, new HashMap<>());
                        values.get(country).put(keys.get(header), string);
                    }
                }
            }
        }
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
