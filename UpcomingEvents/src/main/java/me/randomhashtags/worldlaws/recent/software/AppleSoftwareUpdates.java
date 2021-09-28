package me.randomhashtags.worldlaws.recent.software;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public enum AppleSoftwareUpdates implements RecentEventController {
    INSTANCE;

    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final String url = "https://support.apple.com/en-us/HT201222";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements updateElements = doc.select("body div div section.section div div.column div.main div div div div div table tbody tr");
            updateElements.removeIf(element -> {
                return element.select("td").isEmpty() || updateElements.indexOf(element) > 20;
            });
            final HashSet<PreRecentEvent> updates = new HashSet<>();
            final int max = updateElements.size();
            final AtomicInteger completed = new AtomicInteger(0);
            updateElements.parallelStream().forEach(updateElement -> {
                final Elements tds = updateElement.select("td");
                final Element releaseDateElement = tds.get(2);
                final String releaseDateString = releaseDateElement.text();
                final String[] releaseDateValues = releaseDateString.split(" ");
                if(releaseDateValues.length == 3) {
                    final int day = Integer.parseInt(releaseDateValues[0]), year = Integer.parseInt(releaseDateValues[2]);
                    final Month month = WLUtilities.valueOfMonthFromInput(releaseDateValues[1]);
                    final LocalDate localDate = LocalDate.of(year, month, day);
                    if(localDate.isAfter(startingDate)) {
                        final Element nameElement = tds.get(0);
                        String name = nameElement.text();
                        if(name.startsWith("***REMOVED***")
                                || name.startsWith("***REMOVED***") || name.startsWith("***REMOVED***") || name.startsWith("***REMOVED***") || name.startsWith("***REMOVED***")
                                || name.startsWith("***REMOVED***") || name.startsWith("Apple TV")
                                || name.startsWith("***REMOVED***")
                                || name.startsWith("***REMOVED***")
                        ) {
                            final String description = name.toLowerCase().replace(" ", "").contains("(detailsavailablesoon)") ? "Details available soon" : null;
                            if(description != null) {
                                name = name.replace(" (details available soon)", "");
                            }
                            final EventDate date = new EventDate(localDate);
                            final EventSources sources = new EventSources(new EventSource("Apple Support: Security Updates", url));

                            final Element link = nameElement.selectFirst("a[href]");
                            if(link != null) {
                                final String ahref = link.attr("href");
                                sources.addSource(new EventSource("Apple Support: " + name, ahref));
                            }
                            final PreRecentEvent preRecentEvent = new PreRecentEvent(date, name, description, null, sources);
                            updates.add(preRecentEvent);
                        }
                    }
                }
                if(completed.addAndGet(1) == max) {
                    handler.handleObject(updates);
                }
            });
        } else {
            handler.handleObject(null);
        }
    }
}
