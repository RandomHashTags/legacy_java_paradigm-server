package me.randomhashtags.worldlaws.upcoming.science;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.service.WikipediaEvent;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.events.WikipediaTodaysFeaturedPictureEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.HashMap;

public final class WikipediaTodaysFeaturedPicture extends LoadedUpcomingEventController {
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WIKIPEDIA_TODAYS_FEATURED_PICTURE;
    }

    @Override
    public void load() {
        final String url = "https://en.wikipedia.org/wiki/Main_Page";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String title = "Wikipedia: Today's Featured Picture";
            final Elements bg = doc.select("div.MainPageBG");
            for(Element element : bg) {
                final Element headlineElement = element.selectFirst("span.mw-headline");
                if(headlineElement != null && headlineElement.hasAttr("id") && headlineElement.attr("id").equals("Today's_featured_picture")) {
                    final Element imageElement = element.selectFirst("a.image img");
                    String imageURL = imageElement != null ? "https:" + imageElement.attr("src") : null, videoURL = null;
                    if(imageURL == null) {
                        final Element playerElement = element.selectFirst("span.mw-tmh-player");
                        if(playerElement != null) {
                            final Element videoElement = playerElement.selectFirst("video");
                            if(videoElement != null) {
                                final String posterURL = videoElement.attr("poster");
                                imageURL = "https:" + posterURL;
                                final String[] values = posterURL.split("/");
                                final String last = values[values.length-1];
                                videoURL = "https:" + posterURL.split("/" + last)[0].replace("/thumb/", "/");
                            }
                        }
                    }
                    if(imageURL != null) {
                        final String[] values = imageURL.split("/");
                        final String lastValue = values[values.length-1];
                        final String pixels = lastValue.split("px-")[0];
                        imageURL = imageURL.replace("/" + pixels + "px-", "/%quality%px-");
                    }

                    final StringBuilder description = new StringBuilder();
                    boolean isFirst = true;
                    final HashMap<String, String> hyperlinks = new HashMap<>();
                    final Elements paragraphs = element.selectFirst("table").select("tbody tr td p");
                    for(Element paragraphElement : paragraphs) {
                        description.append(isFirst ? "" : "\n").append(paragraphElement.text());
                        isFirst = false;
                        final HashMap<String, String> links = WikipediaEvent.getWikipediaHyperlinks(paragraphElement);
                        if(links != null) {
                            hyperlinks.putAll(links);
                        }
                    }

                    String photographCredit = null;
                    final Elements smallElements = element.select("small");
                    for(Element smallElement : smallElements) {
                        final String text = smallElement.text();
                        if(text.startsWith("Photograph credit: ")) {
                            photographCredit = text;
                            break;
                        }
                    }

                    final EventDate date = new EventDate(LocalDate.now());
                    final String dateString = date.getDateString();
                    final String identifier = getEventDateIdentifier(dateString, title);
                    final EventSources sources = new EventSources(
                            new EventSource("Wikipedia: Main Page", url)
                    );
                    final WikipediaTodaysFeaturedPictureEvent event = new WikipediaTodaysFeaturedPictureEvent(date, title, description.toString(), imageURL, videoURL, sources, hyperlinks);
                    putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(getType(), identifier, photographCredit));
                    putUpcomingEvent(identifier, event);
                    break;
                }
            }
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new WikipediaTodaysFeaturedPictureEvent(json);
    }
}
