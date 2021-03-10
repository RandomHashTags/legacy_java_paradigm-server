package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.UpcomingEventType;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public enum MusicAlbums implements EventController {
    INSTANCE;

    private final HashMap<Integer, String> years;
    private final HashMap<String, String> preEvents, events;

    MusicAlbums() {
        years = new HashMap<>();
        preEvents = new HashMap<>();
        events = new HashMap<>();
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        final int year = WLUtilities.getTodayYear();
        refresh(year, handler);
    }

    @Override
    public String getCache() {
        final int year = WLUtilities.getTodayYear();
        return years.getOrDefault(year, null);
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return preEvents;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return events;
    }


    private void refresh(int year, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = "https://en.wikipedia.org/wiki/List_of_" + year + "_albums";
        final Document doc = getDocument(url);
        if(doc != null) {
            final StringBuilder builder = new StringBuilder("[");
            final Elements headers = doc.select("h3");
            final Elements tables = doc.select("h3 + table.wikitable");
            final LocalDate today = LocalDate.now();
            final int todayMonthValue = today.getMonthValue();
            for(int i = 1; i < todayMonthValue; i++) {
                tables.remove(0);
            }
            final String prefix = "https://en.wikipedia.org";
            final EventSource wikipedia = new EventSource("Wikipedia: List of " + year + " albums", url);
            final AtomicBoolean isFirst = new AtomicBoolean(true);
            final int maxCompleted = tables.size();
            final AtomicInteger completed = new AtomicInteger(0);
            tables.parallelStream().forEach(table -> {
                final Elements trs = table.select("tbody tr");
                trs.remove(0);
                trs.remove(0);
                int previousDay = 1;
                final int header = tables.indexOf(table);
                final Month month = Month.valueOf(headers.get(header).text().toUpperCase().split("\\[")[0]);
                final String monthName = month.name();
                for(Element row : trs) {
                    final Elements tds = row.select("td");
                    final int max = tds.size();
                    final String targetDay = tds.get(0).text().toUpperCase();
                    final boolean isNewDay = max == 6;
                    if(!isNewDay || targetDay.startsWith(monthName)) {
                        final int day = targetDay.equals("TBA") ? -1 : isNewDay ? Integer.parseInt(targetDay.split(monthName + " ")[1]) : previousDay;
                        previousDay = day;
                        final EventDate releaseDate = new EventDate(month, day, year);
                        final Element artistElement = tds.get(max-5), albumElement = tds.get(max-4);
                        final Elements hrefs = albumElement.select("i a");
                        final String albumURL = !hrefs.isEmpty() ? prefix + hrefs.get(0).attr("href") : null;
                        final String artist = artistElement.text(), album = albumElement.text();
                        final EventSources sources = new EventSources(wikipedia);
                        String albumImageURL = null, description = "Information about this music album is currently unknown";
                        if(albumURL != null) {
                            final Document albumDoc = getDocument(albumURL);
                            if(albumDoc != null) {
                                final String heading = albumDoc.select("div.mw-body h1.firstHeading").get(0).text();
                                final EventSource source = new EventSource("Wikipedia: " + heading, albumURL);
                                sources.addSource(source);
                                description = albumDoc.select("div.mw-parser-output p").get(0).text();
                                final Elements infobox = albumDoc.select("table.infobox");
                                if(!infobox.isEmpty()) {
                                    final Elements elements = infobox.get(0).select("tbody tr td a img");
                                    if(!elements.isEmpty()) {
                                        albumImageURL = "https:" + elements.get(0).attr("src");
                                    }
                                }
                            }
                        }
                        final MusicAlbumEvent event = new MusicAlbumEvent(releaseDate, artist, album, albumImageURL, description, sources);
                        final String identifier = getEventIdentifier(releaseDate, album);
                        events.put(identifier, event.toJSON());
                        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(album, artist, albumImageURL);
                        final String string = preUpcomingEvent.toString();
                        preEvents.put(identifier, string);
                        builder.append(isFirst.get() ? "" : ",").append(string);
                        isFirst.set(false);
                    }
                }
                final int value = completed.addAndGet(1);
                if(value == maxCompleted) {
                    final String string = builder.append("]").toString();
                    WLLogger.log(Level.INFO, "MusicAlbums - refreshed for year " + year + " (took " + (System.currentTimeMillis()-started) + "ms)");
                    years.put(year, string);
                    handler.handle(string);
                }
            });
        }
    }
}
