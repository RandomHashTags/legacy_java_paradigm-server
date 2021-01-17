package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.EventController;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.PreUpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEventType;
import me.randomhashtags.worldlaws.location.CountryBackendID;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.logging.Level;

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
    public CountryBackendID getCountryBackendID() {
        return null;
    }
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        final int year = LocalDate.now().getYear();
        if(years.containsKey(year)) {
            handler.handle(years.get(year));
        } else {
            refresh(year, handler);
        }
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
            final Elements tables = doc.select("h3 + table.wikitable");
            final LocalDate today = LocalDate.now();
            final int todayMonthValue = today.getMonthValue();
            Month month = today.getMonth();
            int monthIndex = todayMonthValue;
            for(int i = 1; i < todayMonthValue; i++) {
                tables.remove(0);
            }
            boolean isFirst = true;
            final String prefix = "https://en.wikipedia.org";
            final EventSource wikipedia = new EventSource("Wikipedia: List of " + year + " albums", url);
            for(Element table : tables) {
                final Elements trs = table.select("tbody tr");
                trs.remove(0);
                trs.remove(0);
                int previousDay = 1;
                final String monthName = month.name();
                for(Element row : trs) {
                    final Elements tds = row.select("td");
                    final int max = tds.size();
                    final String targetDay = tds.get(0).text().toUpperCase();
                    final int day = targetDay.equals("TBA") ? -1 : max == 6 ? Integer.parseInt(targetDay.split(monthName + " ")[1]) : previousDay;
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
                    final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(event.getType(), releaseDate, album, artist, albumImageURL);
                    final String string = preUpcomingEvent.toString();
                    preEvents.put(identifier, string);
                    builder.append(isFirst ? "" : ",").append(string);
                    isFirst = false;
                }
                monthIndex += 1;
                if(monthIndex <= 12) {
                    month = Month.of(monthIndex);
                }
            }
            final String string = builder.append("]").toString();
            WLLogger.log(Level.INFO, "MusicAlbums - refreshed for year " + year + " (took " + (System.currentTimeMillis()-started) + "ms)");
            years.put(year, string);
            handler.handle(string);
        }
    }
}
