package me.randomhashtags.worldlaws.history.country;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.history.CountryHistoryEra;
import me.randomhashtags.worldlaws.history.CountryHistorySection;
import me.randomhashtags.worldlaws.history.ICountryHistory;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public enum HistoryUnitedStates implements ICountryHistory {
    INSTANCE;

    private String getURL() {
        return "https://en.wikipedia.org/wiki/History_of_the_United_States";
    }

    @Override
    public CountryHistorySection getEras() {
        final WikipediaDocument doc = getWikipediaDocument(getURL());
        if(doc != null) {
            final int todayYear = WLUtilities.getTodayYear();
            final String urlPrefix = "https://en.wikipedia.org";
            final Elements elements = doc.getSideBar().select("tbody tr td div.sidebar-list div.sidebar-list-content table tbody tr");
            elements.remove(0);
            final CountryHistorySection section = new CountryHistorySection("Eras", new ArrayList<>());
            String sectionURL = null;
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final Elements bold = tds.select("b");
                if(!bold.isEmpty()) {
                    sectionURL = urlPrefix + bold.select("a[href]").get(0).attr("href");
                } else {
                    final Element titleElement = tds.get(0);
                    final String title = titleElement.text();
                    final String[] yearValues = tds.get(1).text().split("–");
                    final String endingYearString = yearValues[1];
                    final int startingYear = Integer.parseInt(yearValues[0]), endingYear = endingYearString.equalsIgnoreCase("present") ? todayYear : Integer.parseInt(yearValues[1]);
                    final Elements links = titleElement.select("a[href]");
                    final String url = links.isEmpty() ? sectionURL : urlPrefix + links.get(0).attr("href");

                    final WikipediaDocument urlDoc = getWikipediaDocument(url);
                    final List<Element> wikiElements = urlDoc.getAllParagraphs();
                    final String description = wikiElements.get(0).text();

                    final List<String> images = urlDoc.getImages();
                    final String imageURL = !images.isEmpty() ? images.get(0) : null;
                    final CountryHistoryEra era = new CountryHistoryEra(title, startingYear, endingYear, description, imageURL, url);
                    section.addEra(era);
                }
            }
            return section;
        }
        return null;
    }

    @Override
    public EventSources getSources() {
        return new EventSources(
                new EventSource("Wikipedia: History of the United States", getURL())
        );
    }
}
