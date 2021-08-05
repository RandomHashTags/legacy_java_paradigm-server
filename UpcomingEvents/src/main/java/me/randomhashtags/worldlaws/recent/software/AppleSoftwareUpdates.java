package me.randomhashtags.worldlaws.recent.software;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public enum AppleSoftwareUpdates implements RecentEventController, Jsoupable {
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
            final HashSet<String> updates = new HashSet<>();
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
                            final PreRecentEvent preRecentEvent = new PreRecentEvent(name, description, null);
                            updates.add(preRecentEvent.toString());
                        }
                    }
                }
                if(completed.addAndGet(1) == max) {
                    handler.handleHashSetString(updates);
                }
            });
        } else {
            WLLogger.log(Level.ERROR, "AppleSoftwareUpdates - doc == nil!");
            handler.handleHashSetString(null);
        }
    }
}
