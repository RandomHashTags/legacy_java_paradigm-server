package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public final class WikipediaCountryService implements CountryService {

    private final Folder folder, wikiFolder, featuredPicturesFolder;
    private Elements featuredPicturesElements;

    public WikipediaCountryService(boolean isCountries) {
        this.folder = isCountries ? Folder.COUNTRIES_SERVICES_WIKIPEDIA : null;
        this.wikiFolder = isCountries ? Folder.COUNTRIES_WIKIPEDIA_PAGES : Folder.COUNTRIES_SUBDIVISIONS_WIKIPEDIA_PAGES;
        this.featuredPicturesFolder = isCountries ? Folder.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES : null;
    }

    @Override
    public Folder getFolder() {
        return folder;
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
    public String loadData() {
        return null;
    }

    @Override
    public EventSources getResources(String countryBackendID) {
        return new EventSources(
            new EventSource("Wikipedia: " + countryBackendID, "https://en.wikipedia.org/wiki/" + countryBackendID.replace(" ", "_"))
        );
    }

    @Override
    public String getCountryValue(String tag) {
        final String json = loadWikipedia(tag);
        return json != null ? new CountryServiceValue(this, json).toString() : null;
    }

    private String loadWikipedia(String tag) {
        final String url = "https://en.wikipedia.org/wiki/" + tag.replace(" ", "_");
        Document document = Jsoupable.getLocalDocument(wikiFolder, url);
        if(document == null) {
            document = getDocument(wikiFolder, url, true);
            if(document == null) {
                return null;
            }
        }
        final WikipediaDocument wikiDoc = new WikipediaDocument(url, document);
        final List<Element> paragraphs = wikiDoc.getConsecutiveParagraphs();
        JSONObjectTranslatable string = null;
        if(paragraphs != null && !paragraphs.isEmpty()) {
            final Element paragraphElement = paragraphs.get(0);
            final Element listenElement = paragraphElement.selectFirst("span.rt-commentedText");
            final String listenReplacement = listenElement != null ? " (" + listenElement.text() + ")" : null;
            String firstParagraph = LocalServer.removeWikipediaReferences(paragraphElement.text());
            if(listenReplacement != null) {
                firstParagraph = firstParagraph.replace(listenReplacement, "");
            }
            firstParagraph = firstParagraph.replace(" (listen)", "").replace("(listen)", "").replace(" (listen to all)", "").replace("(listen to all)", "");
            firstParagraph = LocalServer.removeWikipediaTranslations(firstParagraph);
            final String paragraph = LocalServer.fixEscapeValues(firstParagraph);
            string = new JSONObjectTranslatable("paragraph");
            string.put("paragraph", paragraph);
            string.put("url", url);
        } else {
            WLLogger.logError(this, "missing paragraph for country \"" + tag + "\"!");
        }
        return string != null ? string.toString() : null;
    }
}
