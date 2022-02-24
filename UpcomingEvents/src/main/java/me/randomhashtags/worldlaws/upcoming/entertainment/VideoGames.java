package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.VideoGameEvent;
import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VideoGames extends UpcomingEventController {
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public void load() {
        final int thisYear = WLUtilities.getTodayYear();
        final Month startingMonth = LocalDate.now().getMonth();
        refreshUpcomingVideoGames(thisYear, startingMonth);
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    private void refreshUpcomingVideoGames(int year, Month startingMonth) {
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_video_games";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-parser-output").select("*");
            final Elements headlines = table.select("h2");
            final List<Element> elementList = new ArrayList<>();
            boolean foundReleases = false;
            final List<String> headlineIDs = Settings.ServerValues.UpcomingEvents.getVideoGameWikipediaHeadlineIDs();
            for(Element element : table) {
                final Element headline = element.selectFirst("h2 span.mw-headline");
                if(headlines.contains(element) && headline != null) {
                    final String id = headline.attr("id");
                    foundReleases = headlineIDs.contains(id);
                } else if(foundReleases) {
                    elementList.add(element);
                }
            }
            refreshUpcomingVideoGames(year, startingMonth, new Elements(elementList));
        }
    }
    private void refreshUpcomingVideoGames(int year, Month startingMonth, Elements array) {
        final Month endingMonth = startingMonth.plus(2);
        final Elements months = array.select("h3");
        months.removeIf(month -> month.select("span").size() != 5);

        boolean isMonth = false, foundStartingMonth = false;
        Month month = null;
        int day = 0;
        final HashMap<String, String> platforms = Settings.ServerValues.UpcomingEvents.getVideoGamePlatforms();
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
                            final boolean hasStyle = td0.hasAttr("style"), hasRowspan = td0.hasAttr("rowspan"), hasMatches = td0.text().matches("[0-9]+"), isTBA = td0.text().equals("TBA");
                            final Element platformsElement = hasStyle ? tds.get(3) : hasRowspan || hasMatches || isTBA ? tds.get(2) : tds.get(1);
                            final String[] platformValues = platformsElement.text().replace(", ", ",").split(",");
                            final List<String> realPlatforms = new ArrayList<>();
                            for(String platform : platformValues) {
                                final String value = platforms.getOrDefault(platform, "Unknown");
                                realPlatforms.add(value);
                            }

                            final String genres = tds.get(hasStyle ? 4 : hasRowspan || hasMatches || isTBA ? 3 : 2).text();

                            final Element titleElement = isEmpty ? tds.get(0) : tdIs.get(0);
                            final String title = titleElement.text();
                            final Elements hrefs = titleElement.select("a[href]");
                            if(!hrefs.isEmpty()) {
                                final String wikipediaURL = "https://en.wikipedia.org" + hrefs.get(0).attr("href");
                                final String dateString = getEventDateString(year, month, day);
                                final String id = getEventDateIdentifier(dateString, title);
                                final StringBuilder builder = new StringBuilder();
                                boolean isFirst = true;
                                for(String platform : realPlatforms) {
                                    builder.append(isFirst ? "" : ", ").append(platform);
                                    isFirst = false;
                                }

                                final HashMap<String, Object> customValues = new HashMap<>();
                                customValues.put("genres", genres);
                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, wikipediaURL, builder.toString(), null, customValues);
                                putPreUpcomingEvent(id, preUpcomingEvent);
                            }
                        }
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
                        sources.add(source);
                    }
                }
            }
        }
    }

    @Override
    public String loadUpcomingEvent(String id) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String url = preUpcomingEvent.getURL();
        final String title = preUpcomingEvent.getTitle(), platforms = preUpcomingEvent.getTag(), genres = (String) preUpcomingEvent.getCustomValue("genres");
        final Document wikidoc = getDocument(url);
        String string = null;
        if(wikidoc != null) {
            final String wikipediaName = url.split("/wiki/")[1].replace("_", " ");
            final EventSource videoGameSource = new EventSource("Wikipedia: " + wikipediaName, url);
            final EventSources sources = new EventSources(videoGameSource);
            addExternalLinks(wikidoc, sources);

            final Elements infobox = wikidoc.select("table.infobox tbody tr");
            final Elements images = infobox.select("td a[href] img");
            final String coverArtURL = !images.isEmpty() ? "https:" + images.get(0).attr("src") : null;
            final Elements paragraphs = wikidoc.select("div.mw-parser-output p");
            paragraphs.removeIf(p -> p.className().equals("mw-empty-elt"));
            final String desc = removeReferences(paragraphs.get(0).text());

            final JSONArray array = getVideosJSONArray(YouTubeVideoType.VIDEO_GAME, title);
            final VideoGameEvent event = new VideoGameEvent(title, desc, coverArtURL, platforms, genres, array, sources);
            string = event.toString();
            putUpcomingEvent(id, string);
        }
        return string;
    }
}