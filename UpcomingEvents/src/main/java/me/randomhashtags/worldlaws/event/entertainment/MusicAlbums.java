package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum MusicAlbums implements EventController {
    INSTANCE;

    private HashMap<String, NewPreUpcomingEvent> preEventURLS;
    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public WLCountry getCountry() {
        return null;
    }
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public HashMap<String, NewPreUpcomingEvent> getPreEventURLs() {
        return preEventURLS;
    }

    @Override
    public HashMap<String, String> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preEventURLS = new HashMap<>();
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();

        final int year = WLUtilities.getTodayYear();
        refresh(year, handler);
    }

    private void refresh(int year, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_" + year + "_albums";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements headers = doc.select("h3");
            final Elements tables = doc.select("h3 + table.wikitable");
            final LocalDate today = LocalDate.now();
            final int todayMonthValue = today.getMonthValue();
            for(int i = 1; i < todayMonthValue; i++) {
                tables.remove(0);
            }
            final String prefix = "https://en.wikipedia.org";
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
                    if(max >= 5 && (!isNewDay || targetDay.startsWith(monthName))) {
                        final int day = targetDay.equals("TBA") ? -1 : isNewDay ? Integer.parseInt(targetDay.split(monthName + " ")[1]) : previousDay;
                        previousDay = day;
                        final String dateString = month.getValue() + "-" + year + "-" + day;

                        final Element artistElement = tds.get(max-5), albumElement = tds.get(max-4);
                        final Elements hrefs = albumElement.select("i a");
                        final String albumURL = !hrefs.isEmpty() ? prefix + hrefs.get(0).attr("href") : null;
                        final String artist = artistElement.text(), album = albumElement.text();

                        if(albumURL != null) {
                            final String id = dateString + album.replace(" ", "");
                            final NewPreUpcomingEvent preUpcomingEvent = new NewPreUpcomingEvent(id, album, albumURL, artist);
                            preEventURLS.put(id, preUpcomingEvent);
                        }
                    }
                }
                final int value = completed.addAndGet(1);
                if(value == maxCompleted) {
                    handler.handle(null);
                }
            });
        }
    }

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        if(upcomingEvents.containsKey(id)) {
            handler.handle(upcomingEvents.get(id));
        } else {
            final NewPreUpcomingEvent preUpcomingEvent = preEventURLS.get(id);
            final String url = preUpcomingEvent.getURL();
            final Document albumDoc = getDocument(url);
            if(albumDoc != null) {
                final String artist = preUpcomingEvent.getTag(), album = preUpcomingEvent.getTitle();
                final EventSources sources = new EventSources();

                final String heading = albumDoc.select("div.mw-body h1.firstHeading").get(0).text();
                final EventSource source = new EventSource("Wikipedia: " + heading, url);
                sources.addSource(source);
                final String description = albumDoc.select("div.mw-parser-output p").get(0).text();
                String albumImageURL = null;
                final Elements infobox = albumDoc.select("table.infobox");
                if(!infobox.isEmpty()) {
                    final Elements elements = infobox.get(0).select("tbody tr td a img");
                    if(!elements.isEmpty()) {
                        albumImageURL = "https:" + elements.get(0).attr("src");
                    }
                }
                final MusicAlbumEvent event = new MusicAlbumEvent(artist, album, albumImageURL, description, sources);
                final String string = event.toJSON();
                upcomingEvents.put(id, string);
                final String preUpcomingEventString = preUpcomingEvent.getPreUpcomingEvent(albumImageURL).toString();
                preUpcomingEvents.put(id, preUpcomingEventString);
                handler.handle(string);
            } else {
                handler.handle(null);
            }
        }
    }
}
