package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.*;

public enum VideoGames implements EventController {
    INSTANCE;

    private HashMap<String, NewPreUpcomingEvent> preEventURLS;
    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public void load(CompletionHandler handler) {
        preEventURLS = new HashMap<>();
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();

        final int thisYear = WLUtilities.getTodayYear();
        refreshUpcomingVideoGames(thisYear, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                refreshUpcomingVideoGames(thisYear+1, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        handler.handle(null);
                    }
                });
            }
        });
    }

    @Override
    public WLCountry getCountry() {
        return null;
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
        Month month = null;
        int day = 0;
        for(Element element : array) {
            if(months.contains(element)) {
                isMonth = true;
            } else if(isMonth) {
                final String name = element.tagName();
                if(name.equals("table") && element.attr("class").equals("wikitable")) {
                    final Elements rows = element.select("tbody tr");
                    rows.remove(0);

                    for(Element row : rows) {
                        String text = row.text();
                        final Month targetMonth = getMonthFrom(text, keys);
                        if(targetMonth != null) {
                            month = targetMonth;
                            final String key = getMonthKey(month, keys);
                            text = text.substring(key.length());
                        }
                        if(month != null) {
                            final String[] values = text.split(" ");
                            final String value0 = values[0];
                            final boolean isDay = value0.matches("[0-9]+");
                            final boolean isRealDay = isDay || value0.equalsIgnoreCase("tba");
                            day = isDay ? Integer.parseInt(value0) : isRealDay ? -1 : day;

                            final String dateString = month.getValue() + "-" + year + "-" + day;
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
                            if(!hrefs.isEmpty()) {
                                final String wikipediaURL = "https://en.wikipedia.org" + hrefs.get(0).attr("href");
                                final String id = dateString + "." + title.replace(" ", "");
                                final StringBuilder builder = new StringBuilder();
                                boolean isFirst = true;
                                for(String platform : platforms) {
                                    builder.append(isFirst ? "" : ", ").append(platform);
                                    isFirst = false;
                                }
                                final NewPreUpcomingEvent preUpcomingEvent = new NewPreUpcomingEvent(id, title, wikipediaURL, builder.toString());
                                preEventURLS.put(id, preUpcomingEvent);
                            }
                        }
                    }
                    isMonth = false;
                }
            }
        }
        handler.handle(null);
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

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        if(upcomingEvents.containsKey(id)) {
            handler.handle(upcomingEvents.get(id));
        } else {
            final NewPreUpcomingEvent preUpcomingEvent = preEventURLS.get(id);
            final String url = preUpcomingEvent.getURL();
            final String title = preUpcomingEvent.getTitle(), platforms = preUpcomingEvent.getTag();
            final Document wikidoc = getDocument(url);
            if(wikidoc != null) {
                final EventSource videoGameSource = new EventSource("Wikipedia: " + title, url);
                final EventSources sources = new EventSources(videoGameSource);
                addExternalLinks(wikidoc, sources);

                final Elements infobox = wikidoc.select("table.infobox tbody tr");
                final Elements images = infobox.select("td a[href] img");
                final String coverArtURL = !images.isEmpty() ? "https:" + images.get(0).attr("src") : null;
                final Elements paragraphs = wikidoc.select("div.mw-parser-output p");
                paragraphs.removeIf(p -> p.className().equals("mw-empty-elt"));
                final String desc = removeReferences(paragraphs.get(0).text());

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(String string : platforms.split(", ")) {
                    builder.append(isFirst ? "" : ",").append("\"").append(string).append("\"");
                    isFirst = false;
                }
                builder.append("]");

                final String realPlatforms = builder.toString();
                final VideoGameEvent event = new VideoGameEvent(title, desc, coverArtURL, realPlatforms, sources);
                final String string = event.toJSON();
                upcomingEvents.put(id, string);
                final String preUpcomingEventString = preUpcomingEvent.getPreUpcomingEvent(coverArtURL).toString();
                preUpcomingEvents.put(id, preUpcomingEventString);
                handler.handle(string);
            } else {
                handler.handle(null);
            }
        }
    }
}