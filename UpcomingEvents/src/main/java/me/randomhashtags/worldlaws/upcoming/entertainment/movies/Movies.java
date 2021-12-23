package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.IMDbService;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.MovieEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Movies extends UpcomingEventController implements IMDbService {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    @Override
    public void load(CompletionHandler handler) {
        refreshFilms(WLUtilities.getTodayYear(), LocalDate.now().getMonth(), handler);
    }

    private void refreshFilms(int year, Month startingMonth, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_American_films_of_" + year;
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-body-content div.mw-parser-output h2 + table.wikitable tbody tr");
            if(year >= 2016) { // supported format only until 2016
                refreshUSAFilms2016(year, startingMonth, table, handler);
            } else {
                handler.handleString(null);
            }
        } else {
            handler.handleString(null);
        }
    }
    private void refreshUSAFilms2016(int year, Month startingMonth, Elements table, CompletionHandler handler) {
        int day = 1;
        boolean foundStartingMonth = false;
        final Month endingMonth = startingMonth.plus(2);
        Month month = null;
        for(Element element : table) {
            String text = element.text();
            final Element monthElement = element.selectFirst("th");
            Month targetMonth = null;
            if(monthElement != null) {
                targetMonth = WLUtilities.valueOfMonthFromInput(monthElement.text().replace(" ", ""));
            }
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
                    final Element href = titleElement.select("i").get(0).selectFirst("a[href]");
                    final String wikipageURL = href != null ? "https://en.wikipedia.org" + href.attr("href") : null;
                    if(wikipageURL != null) {
                        final String title = titleElement.text();
                        final String dateString = getEventDateString(year, month, day), id = getEventDateIdentifier(dateString, title);

                        final HashSet<String> productionCompanies = new HashSet<>(Arrays.asList(rows.get(1).text().split(" / ")));
                        final StringBuilder builder = new StringBuilder("[");
                        boolean isFirst = true;
                        for(String company : productionCompanies) {
                            builder.append(isFirst ? "" : ",").append("\"").append(company).append("\"");
                            isFirst = false;
                        }
                        builder.append("]");

                        final HashMap<String, Object> customValues = new HashMap<>();
                        customValues.put("productionCompanies", builder.toString());

                        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, wikipageURL, null, null, customValues);
                        putPreUpcomingEvent(id, preUpcomingEvent);
                    }
                }
            }
        }
        handler.handleString(null);
    }

    @Override
    public void getResponse(String input, CompletionHandler handler) {
        final String[] values = input.split("/");
        final String key = values[0];
        switch (key) {
            case "productionCompanies":
                MovieProductionCompanies.getResponse(input.substring(key.length()), handler);
                break;
            default:
                super.getResponse(input, handler);
                break;
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String url = preUpcomingEvent.getURL();
        final String title = preUpcomingEvent.getTitle();
        final WikipediaDocument wikiDoc = new WikipediaDocument(url);
        final Document wikidoc = wikiDoc.getDocument();
        if(wikidoc != null) {
            String releaseInfo = "", premise = "";
            final Elements elements = wikidoc.select("div.mw-parser-output > *");
            final Elements headlines = elements.select("h2");
            boolean isRelease = false, isPremise = false, setPremise = false;
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
            }
            if(releaseInfo.isEmpty()) {
                releaseInfo = null;
            }
            if(!setPremise) {
                for(Element element : elements.select("p")) {
                    if(!element.hasAttr("class")) {
                        premise = element.text();
                        break;
                    }
                }
            }

            final EventSources sources = wikiDoc.getExternalLinks();
            sources.append(new EventSource("Wikipedia: " + wikiDoc.getPageName(), url));
            final Elements targetImage = wikidoc.select("div.mw-parser-output table.infobox tbody tr td a img");
            final String imageSourceURL = !targetImage.isEmpty() ? targetImage.get(0).attr("src") : null;
            final String imageURL = imageSourceURL != null ? "https:" + imageSourceURL : null;

            final String premiseFinal = premise, releaseInfoFinal = releaseInfo, urlLowercased = url.toLowerCase();
            int year = urlLowercased.contains("(upcoming_film)") ? -1 : url.contains("_(") && urlLowercased.endsWith("_film)") ? Integer.parseInt(urlLowercased.split("_film\\)")[0].split("_\\(")[1].split("_")[0]) : -1;
            if(year == -1) {
                final Elements infoboxElements = wikidoc.select("table.infobox tbody tr");
                infoboxElements.removeIf(element -> element.select("th.infobox-label").isEmpty());
                boolean foundYear = false;
                for(Element element : infoboxElements) {
                    final String label = element.select("th.infobox-label div").text();
                    if(label.equalsIgnoreCase("release date")) {
                        foundYear = true;
                        final Element infoboxData = element.selectFirst("td.infobox-data");
                        final Element first = infoboxData.selectFirst("div.plainlist ul li");
                        final String targetReleaseDate = first != null ? first.textNodes().get(0).text() : infoboxData.text();
                        if(targetReleaseDate.toLowerCase().contains("present")) {
                            foundYear = false;
                        } else {
                            final String[] values = targetReleaseDate.split(" ");
                            year = Integer.parseInt(values[values.length - 1]);
                        }
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

                    final String productionCompany = (String) preUpcomingEvent.getCustomValue("productionCompanies");
                    final JSONArray array = new JSONArray(productionCompany);
                    final Collection<String> productionCompanies = new HashSet<>();
                    for(Object obj : array) {
                        productionCompanies.add((String) obj);
                    }

                    final MovieEvent movie = new MovieEvent(title, premiseFinal, movieImageURL, productionCompanies, releaseInfoFinal, imdbJSON, ratingsString, youtubeVideoIDs, sources);
                    final String string = movie.toString();
                    putUpcomingEvent(id, string);
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
        final HashMap<String, Object> values = new HashMap<>();
        ParallelStream.stream(set, requestObj -> {
            final String request = (String) requestObj;
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
        handler.handleObject(values);
    }

    private void getRatings(String movieTitle, CompletionHandler handler) {
        final MovieRatingType[] ratings = MovieRatingType.values();
        final HashMap<String, String> values = new HashMap<>();
        ParallelStream.stream(Arrays.asList(ratings), ratingObj -> {
            final MovieRatingType rating = (MovieRatingType) ratingObj;
            final String ratingName = rating.getName();
            rating.load(new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    rating.get(movieTitle, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            if(string != null) {
                                values.put(ratingName, string);
                            }
                        }
                    });
                }
            });
        });

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


    private enum MovieRatingType implements Jsoupable {
        ROTTEN_TOMATOES,
        ;

        private ConcurrentHashMap<MovieRatingType, Elements> cacheElements;

        MovieRatingType() {
            cacheElements = new ConcurrentHashMap<>();
        }

        public String getName() {
            switch (this) {
                case ROTTEN_TOMATOES: return "Rotten Tomatoes";
                default: return "Unknown";
            }
        }

        public void load(CompletionHandler handler) {
            switch (this) {
                case ROTTEN_TOMATOES:
                    loadRottenTomatoesDocument(handler);
                    break;
                default:
                    handler.handleString(null);
                    break;
            }
        }
        public void get(String movieTitle, CompletionHandler handler) {
            switch (this) {
                case ROTTEN_TOMATOES:
                    getRottenTomatoesScores(movieTitle, handler);
                    break;
                default:
                    break;
            }
        }

        private void loadRottenTomatoesDocument(CompletionHandler handler) {
            if(!cacheElements.containsKey(ROTTEN_TOMATOES)) {
                cacheElements.put(ROTTEN_TOMATOES, new Elements());

                final String url = "https://www.rottentomatoes.com/browse/opening";
                final Elements elements = getDocumentElements(Folder.OTHER, url, false, "body div.container main.container div div.layout div.layout__column div.media-list div.media-list__item div.media-list__movie-info a");
                cacheElements.put(ROTTEN_TOMATOES, elements);
            }
            handler.handleObject(null);
        }
        public void getRottenTomatoesScores(String movieTitle, CompletionHandler handler) {
            handleRottenTomatoes(movieTitle, handler);
        }
        private void handleRottenTomatoes(String movieTitle, CompletionHandler handler) {
            final Elements elements = new Elements(cacheElements.get(ROTTEN_TOMATOES));
            elements.removeIf(element -> {
                final String titleElement = element.selectFirst("div.media-list__title").text();
                return !movieTitle.equals(titleElement);
            });
            if(!elements.isEmpty()) {
                final Element element = elements.get(0);
                final Elements meterScoreElements = element.selectFirst("div.media-list__meter-container span").select("span.tMeterScore");
                final Element meterScoreElement = !meterScoreElements.isEmpty() ? meterScoreElements.get(0) : null;
                String string = null;
                if(meterScoreElement != null) {
                    string = meterScoreElement.selectFirst("span.tMeterScore").text().replace("%", "");
                }
                handler.handleString(string);
            } else {
                handler.handleString(null);
            }
        }
    }
}
