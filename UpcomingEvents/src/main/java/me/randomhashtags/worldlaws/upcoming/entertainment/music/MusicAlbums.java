package me.randomhashtags.worldlaws.upcoming.entertainment.music;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.ITunesSearchAPI;
import me.randomhashtags.worldlaws.service.SpotifyService;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.MusicAlbumEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class MusicAlbums extends UpcomingEventController implements SpotifyService, ITunesSearchAPI {

    private String artists;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MUSIC_ALBUM;
    }

    @Override
    public void load() {
        final LocalDate now = WLUtilities.getNow();
        refresh(now.getYear(), now.getMonth(), now.getDayOfMonth());
    }

    private String getArtists() {
        if(artists == null) {
            final StringBuilder builder = new StringBuilder("{");
            final int version = ResponseVersions.MUSIC_ARTISTS.getValue();
            builder.append("\"version\":").append(version).append(",");
            builder.append("\"artists\":{");
            builder.append("}}");
            artists = builder.toString();
        }
        return artists;
    }

    private void refresh(int year, Month startingMonth, int startingDay) {
        final List<Integer> multiPageYears = Settings.ServerValues.UpcomingEvents.getMusicAlbumMultiPageYears();
        if(multiPageYears.contains(year)) {
            refreshMultiList(year, startingMonth, startingDay);
        } else {
            refreshSingularList(year, startingMonth, startingDay);
        }
    }
    private void refreshSingularList(int year, Month startingMonth, int startingDay) {
        final String url = "https://en.wikipedia.org/wiki/List_of_" + year + "_albums";
        refreshList(url, startingMonth.getValue()-1, year, startingMonth, startingDay);
    }
    private void refreshMultiList(int year, Month startingMonth, int startingDay) {
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

        if(urls.size() > 0) {
            new CompletableFutures<String>().stream(urls.keySet(), url -> {
                final int tableIndex = urls.get(url);
                refreshList(url, tableIndex, year, startingMonth, startingDay);
            });
        }
    }
    private String getURL(String baseURL, Month startingMonth) {
        return baseURL + "_(" + (startingMonth.getValue() <= 6 ? "January–June" : "July–December") + ")";
    }

    private void refreshList(String url, int tableIndex, int year, Month startingMonth, int startingDay) {
        final String prefix = "https://en.wikipedia.org";
        final Document doc = getDocument(url);
        loadPreUpcomingEvents(doc, tableIndex, startingMonth, startingDay, prefix, year);
    }

    private void loadPreUpcomingEvents(Document doc, int tableIndex, Month startingMonth, int startingDay, String prefix, int year) {
        if(doc != null) {
            final Elements headers = doc.select("h3");
            final Elements tables = doc.select("h3 + table.wikitable");
            final int[] tableIndexes = { tableIndex, tableIndex+1 };
            for(int targetTableIndex : tableIndexes) {
                final Element table = tables.get(targetTableIndex);
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
                    if(maxTDs >= 5 && (startingMonth != month || day >= startingDay)) {
                        final Element artistElement = tds.get(maxTDs-5), albumElement = tds.get(maxTDs-4);
                        final Elements hrefs = albumElement.select("i a");
                        final String albumURL = !hrefs.isEmpty() ? prefix + hrefs.get(0).attr("href") : null;
                        final String artist = artistElement.text(), album = albumElement.text();

                        if(albumURL != null) {
                            final String dateString = EventDate.getDateString(year, day, month);
                            final String id = getEventDateIdentifier(dateString, album);
                            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, album, albumURL, artist);
                            putPreUpcomingEvent(id, preUpcomingEvent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public UpcomingEvent loadUpcomingEvent(String identifier) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(identifier);
        final String url = preUpcomingEvent.getURL();
        final Document albumDoc = getDocument(url);
        if(albumDoc != null) {
            final String artist = preUpcomingEvent.getUnfixedTag(), album = preUpcomingEvent.getTitle();
            final EventSources sources = new EventSources();

            final String heading = albumDoc.select("div.mw-body h1.firstHeading").get(0).text();
            final EventSource source = new EventSource("Wikipedia: " + heading, url);
            sources.add(source);
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

            final EventDate date = new EventDate(LocalDate.now());
            final String todayDateString = date.getDateString() + ".";
            if(identifier.startsWith(todayDateString)) {
                final HashSet<String> artists = new HashSet<>();
                if(artist.contains(" (") && artist.endsWith(")")) {
                    artists.add(artist.split(" \\(")[0].toLowerCase());
                } else if(artist.contains(" / ")) {
                    artists.add(artist.split(" / ")[0].toLowerCase());
                } else if(artist.contains(", and ")) {
                    if(!artist.contains("(")) {
                        final String[] targetArtists = artist.replace(", and ", ", ").split(", ");
                        for(String targetArtist : targetArtists) {
                            artists.add(targetArtist.toLowerCase());
                        }
                    }
                } else if(artist.contains(" and ")) {
                    artists.add(artist.toLowerCase());
                    final String[] targetArtists = artist.split(" and ");
                    for(String targetArtist : targetArtists) {
                        artists.add(targetArtist.toLowerCase());
                    }
                } else if(artist.contains(" & ")) {
                    artists.add(artist.toLowerCase());
                    final String[] targetArtists = artist.split(" & ");
                    for(String targetArtist : targetArtists) {
                        artists.add(targetArtist.toLowerCase());
                    }
                } else {
                    artists.add(artist);
                }
                final JSONObject spotifyDetails = getSpotifyAlbum(artists, album);
                final JSONObject itunesDetails = getITunesAlbum(album, artist);
                return putUpcomingEvent(date, identifier, artist, album, albumImageURL, description, spotifyDetails, itunesDetails, sources);
            } else {
                return putUpcomingEvent(date, identifier, artist, album, albumImageURL, description, null, null, sources);
            }
        }
        return null;
    }
    private UpcomingEvent putUpcomingEvent(EventDate date, String id, String artist, String album, String imageURL, String description, JSONObject spotifyDetails, JSONObject itunesDetails, EventSources sources) {
        String customImageURL = null;
        if(spotifyDetails != null) {
            customImageURL = spotifyDetails.getString("imageURL");
            final String url = spotifyDetails.getString("url");
            sources.add(new EventSource("Spotify: " + album, url));
        }
        if(itunesDetails != null) {
            customImageURL = itunesDetails.getString("imageURL");
            final String url = itunesDetails.getString("collectionViewUrl"), artistURL = itunesDetails.getString("artistViewUrl");
            sources.add(new EventSource("iTunes: " + album, url));
            sources.add(new EventSource("iTunes: " + artist, artistURL));
        }
        if(customImageURL == null) {
            customImageURL = imageURL;
        }
        return new MusicAlbumEvent(date, artist, album, customImageURL, description, spotifyDetails, itunesDetails, sources);
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new MusicAlbumEvent(json);
    }
}
