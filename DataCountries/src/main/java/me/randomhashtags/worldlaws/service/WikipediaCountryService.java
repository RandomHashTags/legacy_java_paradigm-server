package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class WikipediaCountryService implements NewCountryService {

    private final Folder folder;

    public WikipediaCountryService(boolean isCountries) {
        this.folder = isCountries ? Folder.COUNTRIES_WIKIPEDIA : Folder.COUNTRIES_WIKIPEDIA_SUBDIVISIONS;
    }

    @Override
    public Folder getFolder() {
        return folder;
    }

    @Override
    public String getServiceFileName(WLCountry country) {
        return country.getBackendID();
    }

    @Override
    public String getServiceFileName(SovereignStateSubdivision subdivision) {
        return subdivision.getBackendID();
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_STATIC;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_WIKIPEDIA;
    }

    @Override
    public boolean doesSaveLoadedData() {
        return false;
    }

    @Override
    public JSONObjectTranslatable loadData() {
        return null;
    }

    @Override
    public JSONObjectTranslatable loadData(WLCountry country) {
        return getWikipediaJSON(country.getBackendID(), country.getWikipediaURL());
    }

    @Override
    public JSONObjectTranslatable loadData(SovereignStateSubdivision subdivision) {
        return getWikipediaJSON(subdivision.getBackendID(), subdivision.getWikipediaURL());
    }

    @Override
    public JSONObjectTranslatable getJSONObject(SovereignStateSubdivision subdivision) {
        return getJSONObject(subdivision, true);
    }

    @Override
    public JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable("paragraph");
        translatable.put("paragraph", json.getString("paragraph"));
        translatable.put("url", json.getString("url"));
        return translatable;
    }

    private JSONObjectTranslatable getWikipediaJSON(String identifier, String url) {
        Document document = Jsoupable.getLocalDocument(folder, url);
        if(document == null) {
            document = getDocument(folder, url, false);
            if(document == null) {
                return null;
            }
        }
        final WikipediaDocument wikiDoc = new WikipediaDocument(url, document);
        final List<Element> paragraphs = wikiDoc.getConsecutiveParagraphs();
        if(paragraphs != null && !paragraphs.isEmpty()) {
            final Element paragraphElement = paragraphs.get(0);
            final Element listenElement = paragraphElement.selectFirst("span.rt-commentedText");
            final String listenReplacement = listenElement != null ? " (" + listenElement.text() + ")" : null;
            String paragraph = LocalServer.removeWikipediaReferences(paragraphElement.text());
            if(listenReplacement != null) {
                paragraph = paragraph.replace(listenReplacement, "");
            }
            paragraph = paragraph.replace(" (listen)", "").replace("(listen)", "").replace(" (listen to all)", "").replace("(listen to all)", "");
            paragraph = LocalServer.removeWikipediaTranslations(paragraph);

            final Element infobox = wikiDoc.getInfobox();
            final JSONObjectTranslatable statistics = new JSONObjectTranslatable();
            if(infobox != null) {
                final Elements trs = infobox.selectFirst("tbody").select("tr");
                String previousHeaderText = null, previousHeaderIdentifier = null;
                final HashMap<String, List<String>> allowedStatistics = new HashMap<>() {{
                    put("area", Arrays.asList("total", "land", "water"));
                    put("dimensions", Arrays.asList("length", "width"));
                    put("population", Arrays.asList("total", "density", "median household income"));
                }};

                for(Element tr : trs) {
                    final Element headerElement = tr.selectFirst("th");
                    if(headerElement != null) {
                        final List<TextNode> textNodes = headerElement.textNodes();
                        if(!textNodes.isEmpty()) {
                            final String text = textNodes.get(0).text(), symbols1 = " • ", symbols2 = " • ";
                            final String headerIdentifier = (text.equals(symbols1) || text.equals(symbols2) ? headerElement.selectFirst("a[href]").text() : text.replace(symbols1, "").replace(symbols2, "")).toLowerCase().replace(" ", "_");
                            switch (headerIdentifier) {
                                case "area":
                                case "dimensions":
                                case "population":
                                    previousHeaderText = headerIdentifier.replace(" ", "_");
                                    previousHeaderIdentifier = headerIdentifier;
                                    break;
                                case "elevation":
                                case "highest_elevation":
                                case "lowest_elevation":
                                    final String key = "elevation";
                                    if(!statistics.has(key)) {
                                        statistics.put(key, new JSONObjectTranslatable());
                                    }
                                    statistics.getJSONObjectTranslatable(key).put(headerIdentifier.replace("_elevation", "").replace("elevation", "median"), getStatisticValue(tr));
                                    break;
                                default:
                                    if(previousHeaderIdentifier != null && allowedStatistics.containsKey(previousHeaderIdentifier) && allowedStatistics.get(previousHeaderIdentifier).contains(headerIdentifier)) {
                                        if(!statistics.has(previousHeaderText)) {
                                            statistics.put(previousHeaderText, new JSONObjectTranslatable());
                                        }
                                        statistics.getJSONObjectTranslatable(previousHeaderText).put(headerIdentifier, getStatisticValue(tr));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            final JSONObjectTranslatable json = new JSONObjectTranslatable("paragraph", "statistics");
            if(!statistics.isEmpty()) {
                json.put("statistics", statistics);
            }
            json.put("paragraph", paragraph);
            json.put("url", url);
            return json;
        } else {
            WLLogger.logError(this, "missing paragraph for identifier \"" + identifier + "\"!");
            return null;
        }
    }
    private String getStatisticValue(Element tr) {
        final Element td = tr.selectFirst("td");
        return td != null ? LocalServer.removeWikipediaReferences(td.text()) : "Unknown";
    }

    @Override
    public JSONObjectTranslatable getJSONObject(WLCountry country) {
        return getJSONObject(country, true);
    }

    @Override
    public EventSources getResources(WLCountry country) {
        final String wikipediaURL = country.getWikipediaURL();
        return new EventSources(
            new EventSource("Wikipedia: " + country.getShortName(), wikipediaURL)
        );
    }

    @Override
    public EventSources getResources(SovereignStateSubdivision subdivision) {
        final String wikipediaURL = subdivision.getWikipediaURL();
        return new EventSources(
                new EventSource("Wikipedia: " + subdivision.getConditionalName(), wikipediaURL)
        );
    }

    private enum WikipediaCountryStatistic {
        ELEVATION,
        ;
    }
}
