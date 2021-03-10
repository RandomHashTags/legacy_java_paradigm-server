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

import java.time.Month;
import java.util.*;

public enum VideoGames implements EventController {
    INSTANCE;

    private String json;
    private volatile Month MONTH;
    private volatile int DAY;
    private int MAX_COMPLETED_HANDLERS;
    private HashSet<PreUpcomingEvent> VIDEO_GAMES;
    private HashMap<String, String> preEvents, events;

    VideoGames() {
        VIDEO_GAMES = new HashSet<>();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        final int thisYear = WLUtilities.getTodayYear();
        final long time = System.currentTimeMillis();
        MAX_COMPLETED_HANDLERS = 0;
        preEvents = new HashMap<>();
        events = new HashMap<>();
        refreshUpcomingVideoGames(thisYear, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                refreshUpcomingVideoGames(thisYear+1, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        updateJSON();
                        WLLogger.log(Level.INFO, "Video Games - refreshed year " + thisYear + "-" + (thisYear+1) + " releases (took " + (System.currentTimeMillis()-time) + "ms)");
                        handler.handle(json);
                    }
                });
            }
        });
    }

    @Override
    public String getCache() {
        return json;
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return preEvents;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return events;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    private void refreshUpcomingVideoGames(int year, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_video_games";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-parser-output").select("*");
            final Elements headlines = table.select("h2");
            final List<Element> elementList = new ArrayList<>();
            boolean foundReleases = false;
            for(Element element : table) {
                final Element headline = element.selectFirst("h2 span.mw-headline");
                if(headlines.contains(element) && headline != null) {
                    foundReleases = headline.attr("id").equals("Game_releases");
                } else if(foundReleases) {
                    elementList.add(element);
                }
            }
            refreshUpcomingVideoGames(year, new Elements(elementList), handler);
        }
    }
    private void refreshUpcomingVideoGames(int year, Elements array, CompletionHandler handler) {
        final Elements months = array.select("h3");
        months.removeIf(month -> month.select("span").size() != 5);

        final Set<String> keys = MONTHS.keySet();
        boolean isMonth = false;
        final EventSource source = new EventSource("Wikipedia: " + year + " in video games", "https://en.wikipedia.org/wiki/" + year + "_in_video_games");
        final String url = "https://en.wikipedia.org";
        for(Element element : array) {
            if(months.contains(element)) {
                isMonth = true;
            } else if(isMonth) {
                final String name = element.tagName();
                if(name.equals("table") && element.attr("class").equals("wikitable")) {
                    final Elements rows = element.select("tbody tr");
                    rows.remove(0);

                    addMaxCompletionHandlers(rows.size());
                    for(Element row : rows) {
                        checkMonth(row.text(), keys);
                        final EventDate date = new EventDate(MONTH, DAY, year);
                        final Elements tds = row.select("td");
                        final Elements tdIs = tds.select("i");
                        final boolean isEmpty = tdIs.isEmpty();
                        final Element td0 = tds.get(0);
                        final Element platformsElement = td0.hasAttr("style") ? tds.get(3) : td0.hasAttr("rowspan") || td0.text().matches("[0-9]+") || td0.text().equals("TBA") ? tds.get(2) : tds.get(1);
                        final List<String> platforms = Arrays.asList(
                                platformsElement.text()
                                        .replace("Win", "Windows")
                                        .replace("XBO", "Xbox One")
                                        .replace("NS", "Nintendo Switch")
                                        .replace("XSX", "Xbox Series X/S")
                                        .replace("Mac", "***REMOVED***")
                                        .replace("Lin", "Linux")
                                        .replace("Droid", "***REMOVED***")
                                        .replace("PS4", "PlayStation 4")
                                        .replace("PS5", "PlayStation 5")
                                        .replace("PSVita", "PlayStation Vita")
                                        .replace(", ", ",").split(",")
                        );

                        final Element titleElement = isEmpty ? tds.get(0) : tdIs.get(0);
                        final String title = titleElement.text();
                        final Elements hrefs = titleElement.select("a[href]");
                        final boolean hasHref = !hrefs.isEmpty();
                        final EventSources sources = new EventSources(source);
                        new Thread(() -> {
                            String desc = "", coverArtURL = null;
                            if(hasHref) {
                                final String targetURL = url + hrefs.get(0).attr("href");
                                final Document doc = getDocument(targetURL);
                                if(doc != null) {
                                    final EventSource videoGameSource = new EventSource("Wikipedia: " + title, targetURL);
                                    sources.addSource(videoGameSource);
                                    addExternalLinks(doc, sources);

                                    final Elements infobox = doc.select("table.infobox tbody tr");
                                    final Elements images = infobox.select("td a[href] img");
                                    coverArtURL = !images.isEmpty() ? "https:" + images.get(0).attr("src") : null;
                                    final Elements paragraphs = doc.select("div.mw-parser-output p");
                                    paragraphs.removeIf(p -> p.className().equals("mw-empty-elt"));
                                    desc = removeReferences(paragraphs.get(0).text());
                                }
                            } else {
                                desc = "Information about this video game is currently unknown.";
                            }
                            final VideoGameEvent event = new VideoGameEvent(date, title, desc, coverArtURL, platforms, sources);
                            final String identifier = getEventIdentifier(date, title);
                            events.put(identifier, event.toJSON());
                            addVideoGame(event);
                            if(getVideoGameSize() == getMaxCompletedHandlers()) {
                                handler.handle(null);
                            }
                        }).start();
                    }
                    isMonth = false;
                }
            }
        }
    }

    private void addExternalLinks(Document doc, EventSources sources) {
        final Elements elements = doc.select("div.mw-parser-output > *");
        final Elements headlines = elements.select("h2");
        headlines.removeIf(headline -> !headline.select("span.mw-headline").text().equals("External links"));
        if(!headlines.isEmpty()) {
            final Element headline = headlines.get(0);
            final int indexOfExternalLinks = elements.indexOf(headline);
            if(indexOfExternalLinks != -1) {
                for(int i = 1; i <= indexOfExternalLinks; i++) {
                    elements.remove(0);
                }
                final Element ul = elements.select("ul").get(0);
                for(Element test : ul.select("li")) {
                    final String text = test.text();
                    for(Element externalLink : test.select("a.external")) {
                        final EventSource source = new EventSource(text, externalLink.attr("href"));
                        sources.addSource(source);
                    }
                }
            }
        }
    }

    private void checkMonth(String text, Set<String> keys) {
        final Month targetMonth = getMonthFrom(text, keys);
        if(targetMonth != null) {
            MONTH = targetMonth;
            final String key = getMonthKey(MONTH, keys);
            text = text.substring(key.length());
        }
        final String[] values = text.split(" ");
        final String value0 = values[0];
        final boolean isDay = value0.matches("[0-9]+");
        final boolean isRealDay = isDay || value0.equalsIgnoreCase("tba");
        DAY = isDay ? Integer.parseInt(value0) : isRealDay ? -1 : DAY;
    }

    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(PreUpcomingEvent event : VIDEO_GAMES) {
            builder.append(isFirst ? "" : ",").append(event.toString());
            isFirst = false;
        }
        builder.append("]");
        json = builder.toString();
        VIDEO_GAMES = null;
        MAX_COMPLETED_HANDLERS = 0;
    }

    private synchronized void addMaxCompletionHandlers(int added) {
        MAX_COMPLETED_HANDLERS += added;
    }
    private synchronized int getMaxCompletedHandlers() {
        return MAX_COMPLETED_HANDLERS;
    }
    private synchronized void addVideoGame(VideoGameEvent event) {
        final EventDate date = event.getDate();
        final String title = event.getTitle();
        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(title, event.getPlatforms(), event.getImageURL());
        final String identifier = getEventIdentifier(date, title), string = preUpcomingEvent.toString();
        preEvents.put(identifier, string);
        VIDEO_GAMES.add(preUpcomingEvent);
    }
    private synchronized int getVideoGameSize() {
        return VIDEO_GAMES.size();
    }
}