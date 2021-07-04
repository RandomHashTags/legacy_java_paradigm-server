package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public enum MusicAlbums implements UpcomingEventController, SpotifyService {
    INSTANCE;

    private HashMap<String, PreUpcomingEvent> preUpcomingEvents;
    private HashMap<String, String> upcomingEvents, loadedPreUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getLoadedPreUpcomingEvents() {
        return loadedPreUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preUpcomingEvents = new HashMap<>();
        loadedPreUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();

        final int year = WLUtilities.getTodayYear();
        final Month startingMonth = LocalDate.now().getMonth();
        refresh(year, startingMonth, handler);
    }

    private void refresh(int year, Month startingMonth, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_" + year + "_albums";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String prefix = "https://en.wikipedia.org";
            final Elements headers = doc.select("h3");
            final Elements tables = doc.select("h3 + table.wikitable");
            final Month nextMonth = startingMonth.plus(1);
            final int maxCompleted = tables.size();
            final AtomicInteger completed = new AtomicInteger(0);
            tables.parallelStream().forEach(table -> {
                int previousDay = 1;
                final int header = tables.indexOf(table);
                final Month month = Month.valueOf(headers.get(header).text().split("\\[")[0].toUpperCase());
                if(month == startingMonth || month == nextMonth) {
                    final String monthName = month.name();
                    final Elements trs = table.select("tbody tr");
                    trs.remove(0);
                    trs.remove(0);
                    for(Element row : trs) {
                        final Elements tds = row.select("td");
                        final String targetDay = tds.get(0).text().toUpperCase();
                        final int max = tds.size();
                        final boolean isNewDay = max == 6;
                        if(max >= 5) {
                            final int day = targetDay.equals("TBA") ? -1 : isNewDay ? Integer.parseInt(targetDay.split(monthName + " ")[1]) : previousDay;
                            previousDay = day;

                            final Element artistElement = tds.get(max-5), albumElement = tds.get(max-4);
                            final Elements hrefs = albumElement.select("i a");
                            final String albumURL = !hrefs.isEmpty() ? prefix + hrefs.get(0).attr("href") : null;
                            final String artist = artistElement.text(), album = albumElement.text();

                            if(albumURL != null) {
                                final String dateString = getEventDateString(year, month, day);
                                final String id = getEventDateIdentifier(dateString, album);
                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, album, albumURL, artist);
                                preUpcomingEvents.put(id, preUpcomingEvent);
                            }
                        }
                    }
                }
                if(completed.addAndGet(1) == maxCompleted) {
                    handler.handleString(null);
                }
            });
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.get(id);
        final String url = preUpcomingEvent.getURL();
        final Document albumDoc = getDocument(url);
        if(albumDoc != null) {
            final String artist = preUpcomingEvent.getTag(), album = preUpcomingEvent.getTitle();
            final EventSources sources = new EventSources();

            final String heading = albumDoc.select("div.mw-body h1.firstHeading").get(0).text();
            final EventSource source = new EventSource("Wikipedia: " + heading, url);
            sources.addSource(source);
            final String description = albumDoc.select("div.mw-parser-output p").get(0).text();
            final String albumImageURL;
            final Elements infobox = albumDoc.select("table.infobox");
            if(!infobox.isEmpty()) {
                final Elements elements = infobox.get(0).select("tbody tr td a img");
                albumImageURL = !elements.isEmpty() ? "https:" + elements.get(0).attr("src") : null;
            } else {
                albumImageURL = null;
            }
            final HashSet<String> artists = new HashSet<>();
            if(artist.contains(" (") && artist.endsWith(")")) {
                artists.add(artist.split(" \\(")[0].toLowerCase());
            } else if(artist.contains(" / ")) {
                artists.add(artist.split(" / ")[0].toLowerCase());
            } else if(artist.contains(", and ")) {
                if(!artist.contains("(")) {
                    final String[] targetArtists = artist.replace(", and ", ", ").split(", ");
                    for(String string : targetArtists) {
                        artists.add(string.toLowerCase());
                    }
                }
            } else if(artist.contains(" and ")) {
                artists.add(artist.toLowerCase());
                final String[] targetArtists = artist.split(" and ");
                for(String string : targetArtists) {
                    artists.add(string.toLowerCase());
                }
            } else if(artist.contains(" & ")) {
                artists.add(artist.toLowerCase());
                final String[] targetArtists = artist.split(" & ");
                for(String string : targetArtists) {
                    artists.add(string.toLowerCase());
                }
            }
            getSpotifyAlbum(artists, album, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject spotifyDetails) {
                    final MusicAlbumEvent event = new MusicAlbumEvent(artist, album, albumImageURL, description, spotifyDetails, sources);
                    final String string = event.toJSON();
                    upcomingEvents.put(id, string);
                    final String preUpcomingEventString = preUpcomingEvent.toStringWithImageURL(albumImageURL);
                    loadedPreUpcomingEvents.put(id, preUpcomingEventString);
                    handler.handleString(string);
                }
            });
        } else {
            handler.handleString(null);
        }
    }
}
