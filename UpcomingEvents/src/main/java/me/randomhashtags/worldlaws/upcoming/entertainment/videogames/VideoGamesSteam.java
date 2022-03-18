package me.randomhashtags.worldlaws.upcoming.entertainment.videogames;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class VideoGamesSteam extends UpcomingEventController {

    private String cache;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public void load() {
        refreshStream();
    }

    @Override
    public UpcomingEvent loadUpcomingEvent(String id) {
        return null;
    }

    private void refreshStream() {
        final String url = "https://store.steampowered.com/search/?os=win%2Cmac%2Clinux&filter=popularcomingsoon";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.search_results div a[href]");
            final int max = elements.size();
            if(max > 0) {
                final UpcomingEventType eventType = getType();
                final HashSet<VideoGameRelease> releases = new HashSet<>();
                new CompletableFutures<Element>().stream(elements, element -> {
                    String href = element.attr("href");
                    final String[] hrefValues = href.split("/");
                    href = href.substring(0, href.length()-hrefValues[hrefValues.length-1].length()-1);
                    final Document page = getDocument(href);
                    if(page != null) {
                        final VideoGameRelease release = getPage(eventType, page, href);
                        if(release != null) {
                            releases.add(release);
                        }
                    }
                });
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(VideoGameRelease release : releases) {
                    builder.append(isFirst ? "" : ",").append(release.toString());
                    isFirst = false;
                }
                builder.append("}");
                cache = builder.toString();
            }
        }
    }

    private VideoGameRelease getPage(UpcomingEventType eventType, Element element, String pageURL) {
        final Element pageContent = element.selectFirst("div.tablet_grid div.page_content_ctn");
        if(pageContent != null) {
            final String name = pageContent.select("div.page_title_area div.apphub_HomeHeaderContent div.apphub_HeaderStandardTop div.apphub_AppName").text();
            final Element glance = element.selectFirst("div.block div.game_background_glow div.block_content div.rightcol div.glance_ctn");
            if(glance != null) {
                final Element releaseElement = glance.selectFirst("div.glance_ctn_responsive_left div.release_date div.date");
                if(releaseElement != null) {
                    final int year = WLUtilities.getTodayYear(), nextYear = year + 1;
                    String releaseDate = releaseElement.text().replace(",", "");
                    final boolean isCurrentYear = releaseDate.contains(" " + year), isNextYear = releaseDate.contains(" " + nextYear);
                    final int targetYear = isCurrentYear ? year : isNextYear ? nextYear : -1;
                    if(targetYear != -1) {
                        releaseDate = releaseDate.replace(" " + targetYear, "");
                    }
                    final String[] values = releaseDate.split(" ");
                    if(values.length >= 2) {
                        final String key = values[0], value = values[1], dayPattern = "[0-9]+";
                        final int day;
                        final Month month;
                        if(key.matches(dayPattern)) {
                            day = Integer.parseInt(key);
                            month = WLUtilities.valueOfMonthFromInput(value);
                        } else if(value.matches(dayPattern)) {
                            day = Integer.parseInt(value);
                            month = WLUtilities.valueOfMonthFromInput(key);
                        } else {
                            WLLogger.logInfo("VideoGamesSteam;getPage;name=" + name + "; could not find release details with text \"" + releaseElement.text() + "\"!");
                            return null;
                        }
                        final EventDate date = new EventDate(month, day, targetYear);
                        final Element descriptionElement = glance.selectFirst("div.game_description_snippet");
                        final String description = descriptionElement != null ? descriptionElement.text() : null;
                        final JSONArray genres = new JSONArray();
                        final Elements genresElement = glance.select("div.glance_ctn_responsive_right div.glance_tags_ctn div.glance_tags a[href]");
                        for(Element genre : genresElement) {
                            genres.put(genre.text());
                        }

                        final Elements linkElements = pageContent.select("div.page_content div.rightcol div.block div.block_content div.block_content_inner div.details_block a.linkbar");
                        String officialSite = null;
                        for(Element linkElement : linkElements) {
                            if(linkElement.text().toLowerCase().contains("visit the website")) {
                                final String href = linkElement.attr("href");
                                officialSite = href.split("url=")[1].replace("http://", "https://");
                                break;
                            }
                        }

                        final EventSources sources = new EventSources();
                        sources.add(new EventSource("Steam Store: " + name, pageURL));
                        if(officialSite != null) {
                            sources.add(new EventSource("Official Site", officialSite));
                        }
                        final String wikipediaPageURL = getWikipediaArticleURL(name);
                        if(wikipediaPageURL != null) {
                            sources.add(new EventSource("Wikipedia: " + name, wikipediaPageURL));
                        }

                        final Element imageElement = glance.selectFirst("div.game_header_image_ctn img.game_header_image_full");
                        String imageURL = null;
                        if(imageElement != null) {
                            imageURL = imageElement.attr("src");
                        }
                        final String identifier = getEventDateIdentifier(date.getDateString(), name);
                        final VideoGameRelease release = new VideoGameRelease(date, name, description, imageURL, genres, sources);
                        putLoadedPreUpcomingEvent(identifier, release.toPreUpcomingEventJSON(eventType, identifier, null));
                        putUpcomingEvent(identifier, release);
                        return release;
                    } else {
                        WLLogger.logInfo("VideoGamesSteam;getPage;name=" + name + ";unconfirmed date =" + releaseDate);
                    }
                } else {
                    WLLogger.logInfo("VideoGamesSteam;getPage;name=" + name + ";releaseDate==null");
                }
            }
        }
        return null;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new VideoGameRelease(json);
    }

    private String getEpicGamesURL(String gameName) {
        return null;
    }
    private String getWikipediaArticleURL(String gameName) {
        gameName = gameName.replace(":", "").replace("–", "").replace(" ", "+");
        final String searchURL = "https://en.wikipedia.org/w/index.php?search=" + gameName + "&title=Special:Search&profile=advanced&fulltext=1&ns0=1";
        final Document doc = getDocument(searchURL);
        if(doc != null) {
            final Element searchResults = doc.selectFirst("div.mw-body-content div.searchresults");
            final Element pageElement = searchResults.selectFirst("p.mw-search-exists b a[href]");
            if(pageElement != null) {
                return "https://en.wikipedia.org" + pageElement.attr("href");
            }
            final Element list = searchResults.selectFirst("ul.mw-search-results li.mw-search-result div.mw-search-result-heading a[href]");
            if(list != null) {
                final String[] targetText = list.text().toLowerCase().replace(":", "").split(" ");
                final List<String> gameValues = Arrays.asList(gameName.toLowerCase().split("\\+"));
                for(String term : targetText) {
                    if(!gameValues.contains(term)) {
                        return null;
                    }
                }
                return "https://en.wikipedia.org" + list.attr("href");
            }
        }
        return null;
    }
}
