package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.ProWrestlingEvent;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class ProfessionalWrestling extends LoadedUpcomingEventController {
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_PROFESSIONAL_WRESTLING;
    }

    @Override
    public void load(CompletionHandler handler) {
        refreshFromWikipedia(handler);
    }

    private void refreshFromWikipedia(CompletionHandler handler) {
        final int year = WLUtilities.getTodayYear();
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_professional_wrestling";
        final Document doc = getDocument(url);
        if(doc != null) {
            final HashSet<String> promoters = getPromoters();
            final Elements headers = doc.select("h3");
            final Elements monthElements = doc.select("h3 + table.wikitable");
            final LocalDate today = LocalDate.now();
            final int monthValue = today.getMonthValue()-1, dayValue = today.getDayOfMonth();
            monthElements.removeIf(element -> {
                final int index = headers.indexOf(element.previousElementSibling());
                return index < monthValue || index > monthValue+1;
            });
            int targetMonthValue = monthValue;
            for(Element monthElement : monthElements) {
                targetMonthValue += 1;

                final Elements dayElements = monthElement.select("tbody tr");
                dayElements.remove(0);
                dayElements.remove(dayElements.last());

                int previousDay = 1;
                String previousLocation = null;
                for(Element element : dayElements) {
                    final Elements tds = element.select("td");

                    final String dayString = tds.get(0).text();
                    final boolean hasDay = dayString.matches("[0-9]+");
                    int day = previousDay;
                    if(hasDay) {
                        day = Integer.parseInt(dayString);
                        previousDay = day;
                    }
                    if(day >= dayValue) {
                        final boolean hasPromoter = promoters.contains(!hasDay ? dayString : tds.get(1).text());
                        final Element eventElement = tds.get(hasDay ? hasPromoter ? 2 : 1 : hasPromoter ? 1 : 0).selectFirst("a[href]");
                        if(eventElement != null) {
                            final EventDate eventDate = new EventDate(Month.of(targetMonthValue), day, year);
                            final String dateString = eventDate.getDateString();
                            final String eventURL = "https://en.wikipedia.org" + eventElement.attr("href");
                            final WikipediaDocument eventDoc = new WikipediaDocument(eventURL);
                            final List<String> images = eventDoc.getImages();
                            final String imageURL = !images.isEmpty() ? images.get(0) : null;
                            final List<Element> paragraphs = eventDoc.getConsecutiveParagraphs();
                            final String paragraph = !paragraphs.isEmpty() ? paragraphs.get(0).text() : null;

                            final String title = removeReferences(eventElement.text());
                            final String identifier = getEventDateIdentifier(dateString, title);

                            final String location;
                            if(tds.size() == 6) {
                                location = tds.get(3).text();
                                previousLocation = location;
                            } else {
                                location = previousLocation;
                            }
                            final int tdsSize = tds.size();
                            String mainEvent = tds.get(tdsSize-2).text(), notes = tds.get(tdsSize-1).text();
                            if(mainEvent.isEmpty()) {
                                mainEvent = null;
                            }
                            if(notes.isEmpty()) {
                                notes = null;
                            }

                            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(identifier, title, eventURL, location, null);
                            putPreUpcomingEvent(identifier, preUpcomingEvent);

                            final EventSources sources = new EventSources(eventDoc.getEventSource());
                            final ProWrestlingEvent event = new ProWrestlingEvent(title, paragraph, imageURL, location, mainEvent, notes, sources);
                            putUpcomingEvent(identifier, event.toString());
                        }
                    }
                }
            }
        }
        handler.handleString(null);
    }

    private HashSet<String> getPromoters() {
        return new HashSet<>(Arrays.asList(
                "2AW",
                "AEW",
                "AJPW",
                "BJW",
                "CF",
                "CMLL",
                "Dragon Gate",
                "GCW",
                "Gleat",
                "FMW-E",
                "Impact",
                "AAA",
                "MLW",
                "M-Pro",
                "NWA",
                "NJPW",
                "Zero1",
                "ROH",
                "Stardom",
                "The Crash",
                "WWE",

                "N/A"
        ));
    }
}
