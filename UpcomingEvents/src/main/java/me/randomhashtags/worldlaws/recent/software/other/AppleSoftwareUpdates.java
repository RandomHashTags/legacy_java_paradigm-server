package me.randomhashtags.worldlaws.recent.software.other;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class AppleSoftwareUpdates extends RecentEventController {
    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public HashSet<PreRecentEvent> refreshHashSet(LocalDate startingDate) {
        final String url = "https://support.apple.com/en-us/HT201222";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements updateElements = doc.select("body div div section.section div div.column div.main div div div div div table tbody tr");
            updateElements.removeIf(element -> element.select("td").isEmpty() || updateElements.indexOf(element) > 20);
            final int max = updateElements.size();
            if(max > 0) {
                final HashSet<PreRecentEvent> events = new HashSet<>();
                final HashMap<String, String> descriptionValues = new HashMap<>() {{
                    put(" (Details available soon)", "Details available soon");
                    put(" This update has no published CVE entries.", "This update doesn't share its patch notes");
                }};
                new ParallelStream<Element>().stream(updateElements, updateElement -> {
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
                                final String nameLowercase = name.toLowerCase().replace(" ", "");
                                String description = null;
                                for(Map.Entry<String, String> map : descriptionValues.entrySet()) {
                                    final String key = map.getKey();
                                    final String lowercaseValue = key.toLowerCase().replace(" ", "");
                                    if(nameLowercase.contains(lowercaseValue)) {
                                        final String value = map.getValue(), descriptionValue = value == null ? key : value;
                                        description = descriptionValue;
                                        name = name.replace(descriptionValue, "");
                                    }
                                }

                                final HashMap<String, Object> customValues = new HashMap<>();
                                customValues.put("availableFor", tds.get(1).text());

                                final EventDate date = new EventDate(localDate);
                                final EventSources sources = new EventSources(new EventSource("Apple Support: Security Updates", url));

                                final Element link = nameElement.selectFirst("a[href]");
                                if(link != null) {
                                    final String ahref = link.attr("href");
                                    sources.add(new EventSource("Apple Support: " + name, ahref));
                                }
                                final PreRecentEvent preRecentEvent = new PreRecentEvent(date, name, description, null, sources, customValues);
                                events.add(preRecentEvent);
                            }
                        }
                    }
                });
                return events;
            }
        }
        return null;
    }
}
