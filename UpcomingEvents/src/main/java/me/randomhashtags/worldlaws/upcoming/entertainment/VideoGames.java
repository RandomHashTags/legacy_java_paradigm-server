package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.recent.VideoGameUpdates;
import me.randomhashtags.worldlaws.recent.software.videogames.VideoGameUpdateController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.VideoGameEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class VideoGames extends UpcomingEventController {
    private String videoGameListCache;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public void load(CompletionHandler handler) {
        final int thisYear = WLUtilities.getTodayYear();
        final Month startingMonth = LocalDate.now().getMonth();
        refreshUpcomingVideoGames(thisYear, startingMonth, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                handler.handleString(null);
            }
        });
    }

    @Override
    public void getResponse(String input, CompletionHandler handler) {
        if(input.equals("list")) {
            handler.handleString(getVideoGameList());
        } else {
            getUpcomingEvent(input, handler);
        }
    }

    private String getVideoGameList() {
        if(videoGameListCache == null) {
            final JSONObject json = new JSONObject();
            for(VideoGameUpdateController controller : VideoGameUpdates.getSupportedVideoGames()) {
                json.put(controller.getName(), new JSONObject());
            }
            videoGameListCache = json.toString();
        }
        return videoGameListCache;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    private void refreshUpcomingVideoGames(int year, Month startingMonth, CompletionHandler handler) {
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
            refreshUpcomingVideoGames(year, startingMonth, new Elements(elementList), handler);
        } else {
            handler.handleString(null);
        }
    }
    private void refreshUpcomingVideoGames(int year, Month startingMonth, Elements array, CompletionHandler handler) {
        final Month endingMonth = startingMonth.plus(2);
        final Elements months = array.select("h3");
        months.removeIf(month -> month.select("span").size() != 5);

        boolean isMonth = false, foundStartingMonth = false;
        Month month = null;
        int day = 0;
        outer : for(Element element : array) {
            if(months.contains(element)) {
                isMonth = true;
            } else if(isMonth) {
                final String name = element.tagName();
                if(name.equals("table") && element.attr("class").equals("wikitable")) {
                    final Elements rows = element.select("tbody tr");
                    rows.remove(0);

                    for(Element row : rows) {
                        String text = row.text();
                        final Month targetMonth = WLUtilities.valueOfMonthFromInput(text.replace(" ", ""));
                        if(foundStartingMonth && targetMonth == endingMonth) {
                            break outer;
                        } else if(!foundStartingMonth && targetMonth == startingMonth) {
                            foundStartingMonth = true;
                        }
                        if(targetMonth != null) {
                            month = targetMonth;
                        }
                        if(foundStartingMonth) {
                            if(targetMonth != null) {
                                final int length = (month.name().length()*2);
                                text = text.substring(length);
                            }
                            final String[] values = text.split(" ");
                            final String value0 = values[0];
                            final boolean isDay = value0.matches("[0-9]+");
                            final boolean isRealDay = isDay || value0.equalsIgnoreCase("tba");
                            day = isDay ? Integer.parseInt(value0) : isRealDay ? -1 : day;

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
                                final String dateString = getEventDateString(year, month, day);
                                final String id = getEventDateIdentifier(dateString, title);
                                final StringBuilder builder = new StringBuilder();
                                boolean isFirst = true;
                                for(String platform : platforms) {
                                    builder.append(isFirst ? "" : ", ").append(platform);
                                    isFirst = false;
                                }
                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, wikipediaURL, builder.toString());
                                putPreUpcomingEvent(id, preUpcomingEvent);
                            }
                        }
                    }
                    isMonth = false;
                }
            }
        }
        handler.handleString(null);
    }

    private void addExternalLinks(Document doc, EventSources sources) {
        final Elements elements = doc.select("div.mw-parser-output > *");
        final Elements headlines = elements.select("h2");
        headlines.removeIf(headline -> !headline.select("span.mw-headline").text().equals("External links"));
        if(!headlines.isEmpty()) {
            final Element headline = headlines.get(0);
            final int indexOfExternalLinks = headline.elementSiblingIndex();
            if(indexOfExternalLinks != -1) {
                for(int i = 1; i <= indexOfExternalLinks; i++) {
                    elements.remove(0);
                }
                final Element ul = elements.select("ul").get(0);
                final Element list = ul.selectFirst("li");
                if(list != null) {
                    final Elements hrefs = list.select("a.external");
                    for(Element href : hrefs) {
                        final String text = href.text();
                        final EventSource source = new EventSource(text, href.attr("href"));
                        sources.append(source);
                    }
                }
            }
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
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
            getVideosJSONArray(YouTubeVideoType.VIDEO_GAME, title, new CompletionHandler() {
                @Override
                public void handleJSONArray(JSONArray array) {
                    final VideoGameEvent event = new VideoGameEvent(title, desc, coverArtURL, realPlatforms, array, sources);
                    final String string = event.toString();
                    putUpcomingEvent(id, string);
                    handler.handleString(string);
                }
            });
        } else {
            handler.handleString(null);
        }
    }
}