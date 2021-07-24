package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.service.IMDbService;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public enum Movies implements UpcomingEventController, IMDbService {
    INSTANCE;

    private HashMap<String, PreUpcomingEvent> preUpcomingEvents;
    private HashMap<String, String> loadedPreUpcomingEvents, upcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    @Override
    public HashMap<String, String> getLoadedPreUpcomingEvents() {
        return loadedPreUpcomingEvents;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        refreshFilms(WLUtilities.getTodayYear(), LocalDate.now().getMonth(), handler);
    }

    private void refreshFilms(int year, Month startingMonth, CompletionHandler handler) {
        preUpcomingEvents = new HashMap<>();
        loadedPreUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_American_films_of_" + year;
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-body-content div.mw-parser-output h2 + table.wikitable tbody tr");
            if(year >= 2016) { // supported format only until 2016
                refreshUSAFilms2016(year, startingMonth, table, handler);
            }
        }
    }
    private void refreshUSAFilms2016(int year, Month startingMonth, Elements table, CompletionHandler handler) {
        int day = 1;
        boolean foundStartingMonth = false;
        final Month endingMonth = startingMonth.plus(2);
        Month month = null;
        for(Element element : table) {
            String text = element.text();
            final Month targetMonth = WLUtilities.valueOfMonthFromInput(text.replace(" ", ""));
            if(foundStartingMonth && targetMonth == endingMonth) {
                break;
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
                day = value0.matches("[0-9]+") ? Integer.parseInt(value0) : day;

                final Elements rows = element.select("td");
                if(!rows.isEmpty()) {
                    rows.removeIf(row -> row.text().matches("[0-9]+"));
                    final Element titleElement = rows.get(0);
                    final String wikipageURL = getWikipageURL(titleElement);
                    if(wikipageURL != null) {
                        final String title = titleElement.text();
                        final String dateString = getEventDateString(year, month, day), id = getEventDateIdentifier(dateString, title);
                        final String productionCompany = rows.get(1).text();
                        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, wikipageURL, productionCompany);
                        preUpcomingEvents.put(id, preUpcomingEvent);
                    }
                }
            }
        }
        handler.handleString(null);
    }
    private String getWikipageURL(Element titleElement) {
        final Elements hrefs = titleElement.select("i").get(0).select("a[href]");
        if(!hrefs.isEmpty()) {
            return "https://en.wikipedia.org" + hrefs.get(0).attr("href");
        } else {
            return null;
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.get(id);
        final String url = preUpcomingEvent.getURL();
        final String title = preUpcomingEvent.getTitle(), productionCompany = preUpcomingEvent.getTag();
        final Document wikidoc = getDocument(url);
        if(wikidoc != null) {
            String releaseInfo = "", premise = "";
            final EventSources externalSources = new EventSources();
            final Elements elements = wikidoc.select("div.mw-parser-output > *");
            final Elements headlines = elements.select("h2");
            boolean isRelease = false, isPremise = false, isExternalLinks = false, setPremise = false;
            for(Element target : elements) {
                final String targetTagName = target.tagName();
                final boolean isHeadline = headlines.contains(target);
                if(isHeadline) {
                    final String targetID = target.select("span.mw-headline").text();
                    switch (targetID) {
                        case "Premise":
                        case "Synopsis":
                        case "Plot":
                            isPremise = true;
                            break;
                        case "Release":
                            isRelease = true;
                            break;
                        case "External links":
                            isExternalLinks = true;
                            break;
                    }
                }
                if(isPremise) {
                    final boolean isTarget = targetTagName.equals("blockquote") || targetTagName.equals("p");
                    if(isTarget) {
                        premise = target.text();
                        isPremise = false;
                        setPremise = true;
                    }
                }
                if(isRelease) {
                    final boolean isParagraph = targetTagName.equals("p"), isDiv = targetTagName.equals("div");
                    isRelease = isParagraph || isDiv;
                    if(isParagraph) {
                        releaseInfo = releaseInfo.concat(target.text());
                    }
                }
                if(isExternalLinks) {
                    final boolean isUL = targetTagName.equals("ul");
                    if(isUL) {
                        for(Element list : target.select("li")) {
                            final Elements hrefs = list.select("a");
                            for(Element href : hrefs) {
                                EventSource externalSource = null;
                                final String hrefText = href.text(), hrefTextLowercase = hrefText.toLowerCase();
                                switch (hrefTextLowercase) {
                                    case "official website":
                                        externalSource = new EventSource(hrefText, href.attr("href"));
                                        break;
                                    case "imdb":
                                    case "disney+":
                                    case "facebook":
                                    case "allmovie":
                                    case "history vs. hollywood":
                                    case "netflix":
                                    case "rotten tomatoes":
                                    case "metacritic":
                                    case "box office mojo":
                                        externalSource = new EventSource(hrefText + ": " + title, hrefs.first().attr("href"));
                                        break;
                                    default:
                                        break;
                                }
                                if(externalSource != null) {
                                    externalSources.addSource(externalSource);
                                }
                            }
                        }
                        isExternalLinks = false;
                    }
                }
            }
            if(releaseInfo.isEmpty()) {
                releaseInfo = null;
            }
            if(!setPremise) {
                premise = elements.select("p").get(0).text();
            }

            final String wikiSuffix = url.split("/wiki/")[1].replace("_", " ");
            final EventSource wikipage = new EventSource("Wikipedia: " + wikiSuffix, url);
            final EventSources sources = new EventSources(wikipage);
            sources.addSources(externalSources);
            final Elements targetImage = wikidoc.select("div.mw-parser-output table.infobox tbody tr td a img");
            final String imageSourceURL = !targetImage.isEmpty() ? targetImage.get(0).attr("src") : null;
            final String imageURL = imageSourceURL != null ? "https:" + imageSourceURL : null;

            final String premiseFinal = premise, releaseInfoFinal = releaseInfo, urlLowercased = url.toLowerCase();
            int year = url.contains("_(") && urlLowercased.endsWith("_film)") ? Integer.parseInt(urlLowercased.split("_film\\)")[0].split("_\\(")[1]) : -1;
            if(year == -1) {
                final Elements infoboxElements = wikidoc.select("table.infobox tbody tr");
                infoboxElements.removeIf(element -> element.select("th.infobox-label").isEmpty());
                boolean foundYear = false;
                for(Element element : infoboxElements) {
                    final String label = element.select("th.infobox-label div").text();
                    if(label.equalsIgnoreCase("release date")) {
                        foundYear = true;
                        year = Integer.parseInt(element.select("td.infobox-data div.plainlist ul li").get(0).text().split(" ")[2]);
                        break;
                    }
                }
                if(!foundYear) {
                    year = WLUtilities.getTodayYear();
                }
            }
            getMovieDetails(title, year, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    @SuppressWarnings({ "unchecked" })
                    final HashMap<String, Object> values = (HashMap<String, Object>) object;
                    final JSONObject imdbJSON = (JSONObject) values.get("imdbInfo");
                    final JSONArray youtubeVideoIDs = (JSONArray) values.get("youtubeVideoIDs");
                    final String ratingsString = (String) values.get("ratings");
                    final String movieImageURL;
                    if(imdbJSON != null && imdbJSON.has("imageURL")) {
                        movieImageURL = imdbJSON.getString("imageURL");
                        imdbJSON.remove("imageURL");
                    } else {
                        movieImageURL = imageURL;
                    }
                    final MovieEvent movie = new MovieEvent(title, premiseFinal, movieImageURL, productionCompany, releaseInfoFinal, imdbJSON, ratingsString, youtubeVideoIDs, sources);
                    final String string = movie.toJSON();
                    upcomingEvents.put(id, string);
                    handler.handleString(string);
                }
            });
        } else {
            handler.handleString(null);
        }
    }

    private void getMovieDetails(String title, int year, CompletionHandler handler) {
        final HashSet<String> set = new HashSet<>() {{
            add("imdbInfo");
            add("ratings");
            add("youtubeVideoIDs");
        }};
        final int max = set.size();
        final HashMap<String, Object> values = new HashMap<>();
        final AtomicInteger completed = new AtomicInteger();
        set.parallelStream().forEach(request -> {
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    handleObject(json);
                }
                @Override
                public void handleJSONArray(JSONArray array) {
                    handleObject(array);
                }
                @Override
                public void handleString(String string) {
                    handleObject(string);
                }

                @Override
                public void handleObject(Object object) {
                    values.put(request, object);
                    if(completed.addAndGet(1) == max) {
                        handler.handleObject(values);
                    }
                }
            };
            switch (request) {
                case "imdbInfo":
                    getIMDbMovieDetails(title, year, completionHandler);
                    break;
                case "ratings":
                    getRatings(title, completionHandler);
                    break;
                case "youtubeVideoIDs":
                    getVideosJSONArray(YouTubeVideoType.MOVIE, title, completionHandler);
                    break;
                default:
                    break;
            }
        });
    }

    private void getRatings(String movieTitle, CompletionHandler handler) {
        final MovieRatingType[] ratings = MovieRatingType.values();
        final int max = ratings.length;
        final HashMap<String, String> values = new HashMap<>();
        final AtomicInteger completion = new AtomicInteger(0);
        Arrays.stream(ratings).parallel().forEach(rating -> {
            final String ratingName = rating.getName();
            rating.get(movieTitle, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        values.put(ratingName, string);
                    }
                    if(completion.addAndGet(1) == max) {
                        String value = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirst = true;
                            for(Map.Entry<String, String> map : values.entrySet()) {
                                final String targetRatingName = map.getKey();
                                builder.append(isFirst ? "" : ",").append("\"").append(targetRatingName).append("\":").append(map.getValue());
                                isFirst = false;
                            }
                            builder.append("}");
                            value = builder.toString();
                        }
                        handler.handleString(value);
                    }
                }
            });
        });
    }


    private enum MovieRatingType implements Jsoupable {
        ROTTEN_TOMATOES,
        ;

        public String getName() {
            switch (this) {
                case ROTTEN_TOMATOES: return "Rotten Tomatoes";
                default: return "Unknown";
            }
        }

        public void get(String movieTitle, CompletionHandler handler) {
            switch (this) {
                case ROTTEN_TOMATOES:
                    getRottenTomatoesScore(movieTitle, handler);
                    break;
                default:
                    handler.handleString(null);
                    break;
            }
        }

        private void getRottenTomatoesScore(String movieTitle, CompletionHandler handler) { // TODO: cache rotten tomatoes document so it doesn't spam requests to same url
            final String url = "https://www.rottentomatoes.com/browse/opening";
            final Elements elements = getDocumentElements(Folder.UPCOMING_EVENTS_HOLIDAYS, url, false, "body div.container main.container div div.layout div.layout__column div.media-list div.media-list__item div.media-list__movie-info a");
            if(elements != null) {
                final AtomicBoolean found = new AtomicBoolean(false);
                elements.parallelStream().forEach(element -> {
                    if(!found.get()) {
                        final Element titleElement = element.selectFirst("div.media-list__title");
                        if(movieTitle.equals(titleElement.text())) {
                            found.set(true);
                            final Elements meterScoreElements = element.selectFirst("div.media-list__meter-container span").select("span.tMeterScore");
                            final Element meterScoreElement = !meterScoreElements.isEmpty() ? meterScoreElements.get(0) : null;
                            String string = null;
                            if(meterScoreElement != null) {
                                string = meterScoreElement.selectFirst("span.tMeterScore").text().replace("%", "");
                            }
                            handler.handleString(string);
                        }
                    }
                });
                if(!found.get()) {
                    handler.handleString(null);
                }
            } else {
                handler.handleString(null);
            }
        }
    }
}
