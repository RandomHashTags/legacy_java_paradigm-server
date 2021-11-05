package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.ITunesSearchAPI;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.MusicAlbumEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public final class MusicAlbums extends UpcomingEventController implements SpotifyService, ITunesSearchAPI {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public void load(CompletionHandler handler) {
        final int year = WLUtilities.getTodayYear();
        final Month startingMonth = LocalDate.now().getMonth();
        refresh(year, startingMonth, handler);
    }

    private String getURL(String baseURL, Month startingMonth) {
        return baseURL + "_(" + (startingMonth.getValue() <= 6 ? "January–June" : "July–December") + ")";
    }
    private void refresh(int year, Month startingMonth, CompletionHandler handler) {
        final Month nextMonth = startingMonth.plus(1);
        final boolean isBoth = nextMonth == Month.JULY;
        final String prefix = "https://en.wikipedia.org", baseURL = prefix + "/wiki/List_of_" + year + "_albums";
        final int startingMonthValue = startingMonth.getValue();
        final HashMap<String, Integer> urls = new HashMap<>() {{
            put(getURL(baseURL, startingMonth), startingMonthValue - (startingMonthValue >= 7 ? 7 : 0));
        }};

        if(isBoth) {
            urls.put(getURL(baseURL, nextMonth), nextMonth.getValue()-6);
        }

        final AtomicInteger completed = new AtomicInteger(0);
        final int max = urls.size();
        if(max == 0) {
            handler.handleString(null);
        } else {
            urls.keySet().parallelStream().forEach(url -> {
                final Document doc = getDocument(url);
                if(doc != null) {
                    final Elements headers = doc.select("h3");
                    final Elements tables = doc.select("h3 + table.wikitable");
                    final int tableIndex = urls.get(url);
                    final Element table = tables.get(tableIndex);
                    int previousDay = 1;
                    final int header = tables.indexOf(table);
                    final Month month = Month.valueOf(headers.get(header).text().split("\\[")[0].toUpperCase());
                    final String monthName = month.name();
                    final Elements trs = table.select("tbody tr");
                    trs.remove(0);
                    trs.remove(0);
                    for(Element row : trs) {
                        final Elements tds = row.select("td");
                        final Element targetDayElement = row.selectFirst("th");
                        final boolean isNewDay = targetDayElement != null;
                        final String targetDay = isNewDay ? targetDayElement.text().toUpperCase() : null;
                        final int maxTDs = tds.size();
                        final int day = isNewDay ? targetDay.equals("TBA") ? -1 : Integer.parseInt(targetDay.split(monthName + " ")[1]) : previousDay;
                        previousDay = day;
                        if(maxTDs >= 5) {
                            final Element artistElement = tds.get(maxTDs-5), albumElement = tds.get(maxTDs-4);
                            final Elements hrefs = albumElement.select("i a");
                            final String albumURL = !hrefs.isEmpty() ? prefix + hrefs.get(0).attr("href") : null;
                            final String artist = artistElement.text(), album = albumElement.text();

                            if(albumURL != null) {
                                final String dateString = getEventDateString(year, month, day);
                                final String id = getEventDateIdentifier(dateString, album);
                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, album, albumURL, artist);
                                putPreUpcomingEvent(id, preUpcomingEvent);
                            }
                        }
                    }
                }

                if(completed.addAndGet(1) == max) {
                    handler.handleString(null);
                }
            });
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String url = preUpcomingEvent.getURL();
        final Document albumDoc = getDocument(url);
        if(albumDoc != null) {
            final String artist = preUpcomingEvent.getTag(), album = preUpcomingEvent.getTitle();
            final EventSources sources = new EventSources();

            final String heading = albumDoc.select("div.mw-body h1.firstHeading").get(0).text();
            final EventSource source = new EventSource("Wikipedia: " + heading, url);
            sources.append(source);
            final Elements paragraphs = albumDoc.select("div.mw-parser-output p");
            paragraphs.removeIf(paragraph -> paragraph.hasClass("mw-empty-elt"));
            final String description = paragraphs.get(0).text();
            final String albumImageURL;
            final Elements infobox = albumDoc.select("table.infobox");
            if(!infobox.isEmpty()) {
                final Elements elements = infobox.get(0).select("tbody tr td a img");
                albumImageURL = !elements.isEmpty() ? "https:" + elements.get(0).attr("src") : null;
            } else {
                albumImageURL = null;
            }

            final LocalDate now = LocalDate.now();
            final Month month = now.getMonth();
            final int day = now.getDayOfMonth(), year = now.getYear();
            final String todayDateString = getEventDateString(new EventDate(month, day, year));
            if(id.startsWith(todayDateString)) {
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
                } else {
                    artists.add(artist);
                }
                getSpotifyAlbum(artists, album, new CompletionHandler() {
                    @Override
                    public void handleJSONObject(JSONObject spotifyDetails) {
                        getITunesAlbum(album, artist, new CompletionHandler() {
                            @Override
                            public void handleJSONObject(JSONObject itunesDetails) {
                                putUpcomingEvent(id, artist, album, albumImageURL, description, spotifyDetails, itunesDetails, sources, handler);
                            }
                        });
                    }
                });
            } else {
                putUpcomingEvent(id, artist, album, albumImageURL, description, null, null, sources, handler);
            }
        } else {
            handler.handleString(null);
        }
    }
    private void putUpcomingEvent(String id, String artist, String album, String imageURL, String description, JSONObject spotifyDetails, JSONObject itunesDetails, EventSources sources, CompletionHandler handler) {
        final MusicAlbumEvent event = new MusicAlbumEvent(artist, album, imageURL, description, spotifyDetails, itunesDetails, sources);
        final String string = event.toString();
        putUpcomingEvent(id, string);
        handler.handleString(string);
    }
}
