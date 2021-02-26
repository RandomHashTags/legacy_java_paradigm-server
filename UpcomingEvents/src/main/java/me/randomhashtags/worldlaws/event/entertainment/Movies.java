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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public enum Movies implements EventController {
    INSTANCE;

    private String json;
    private final HashMap<WLCountry, String> countriesJSON;
    private final HashMap<String, String> preEvents, events;

    Movies() {
        countriesJSON = new HashMap<>();
        preEvents = new HashMap<>();
        events = new HashMap<>();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            final int year = WLUtilities.getTodayYear();
            final WLCountry usa = WLCountry.UNITED_STATES;
            refreshReleasedFilms(usa, year, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(countriesJSON.get(usa));
                }
            });
        }
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return preEvents;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return events;
    }

    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<WLCountry, String> set : countriesJSON.entrySet()) {
            final WLCountry country = set.getKey();
            final String array = set.getValue();
            final String string = "\"" + country.getBackendID() + "\":" + array;
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
            countriesJSON.put(country, array);
        }
        builder.append("}");
        json = builder.toString();
    }
    private void addMoviesJSON(WLCountry country, HashSet<MovieEvent> movies) {
        final UpcomingEventType type = getType();
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(MovieEvent event : movies) {
            final EventDate date = event.getDate();
            final String title = event.getTitle();
            final String identifier = getEventIdentifier(date, title);
            events.put(identifier, event.toJSON());

            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(type, date, title, event.getProductionCompany(), event.getImageURL());
            final String string = preUpcomingEvent.toString();
            preEvents.put(identifier, string);
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
        }
        builder.append("]");
        final String string = builder.toString();
        countriesJSON.put(country, string);
    }

    @SuppressWarnings({ "unchecked" })
    private void refreshReleasedFilms(WLCountry country, int year, CompletionHandler handler) {
        final long time = System.currentTimeMillis();
        final CompletionHandler completion = new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final HashSet<MovieEvent> movies = (HashSet<MovieEvent>) object;

                addMoviesJSON(country, movies);
                updateJSON();
                WLLogger.log(Level.INFO, "Movies - refreshed \"" + country.getBackendID() + "\" movies for year " + year + " (took " + (System.currentTimeMillis()-time) + "ms)");
                handler.handle(json);
            }
        };
        if(country == WLCountry.UNITED_STATES) {
            refreshUSAFilms(year, completion);
        }
    }

    private void refreshUSAFilms(int year, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_American_films_of_" + year;
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-body-content div.mw-content-ltr div.mw-parser-output h2 + table.wikitable tbody tr");
            if(year >= 2016) { // supported format only until 2016
                refreshUSAFilms2016(year, url, table, handler);
            }
        }
    }
    private void refreshUSAFilms2016(int year, String url, Elements table, CompletionHandler handler) {
        final HashSet<MovieEvent> MOVIES = new HashSet<>();
        final Set<String> keys = MONTHS.keySet();
        Month month = null;
        int day = 1;
        final EventSource listOfAmericanFilmsSource = new EventSource("Wikipedia: List of American films of " + year, url);
        final AtomicInteger maxHandlers = new AtomicInteger(0);
        for(Element element : table) {
            String text = element.text();
            final Month targetMonth = getMonthFrom(text, keys);
            if(targetMonth != null) {
                month = targetMonth;
                final String key = getMonthKey(month, keys);
                text = text.substring(key.length());
            }
            final String[] values = text.split(" ");
            final String value0 = values[0];
            final boolean isDay = value0.matches("[0-9]+");
            day = isDay ? Integer.parseInt(value0) : day;
            final EventDate date = new EventDate(month, day, year);
            final Elements rows = element.select("td");
            if(!rows.isEmpty()) {
                rows.removeIf(row -> row.hasAttr("style"));
                maxHandlers.addAndGet(1);
                final Element titleElement = rows.get(0);
                final String title = titleElement.text();
                final String productionCompany = rows.get(1).text();
                final String wikipageURL = getWikipageURL(titleElement);
                if(wikipageURL != null) {
                    new Thread(() -> {
                        final Document wikidoc = getDocument(wikipageURL);
                        String releaseInfo = "", premise = "";
                        final EventSources externalSources = new EventSources();
                        if(wikidoc != null) {
                            final Elements elements = wikidoc.select("div.mw-parser-output > *");
                            final Elements headlines = elements.select("h2");
                            boolean isRelease = false, isPremise = false, isExternalLinks = false;
                            boolean setPremise = false;
                            for(Element target : elements) {
                                final String targetTagName = target.tagName();
                                final boolean isHeadline = headlines.contains(target);
                                if(isHeadline) {
                                    final String targetID = target.select("span.mw-headline").text();
                                    if(targetID.equals("Premise") || targetID.equals("Synopsis") || targetID.equals("Plot")) {
                                        isPremise = true;
                                    } else if(targetID.equals("Release")) {
                                        isRelease = true;
                                    } else if(targetID.equals("External links")) {
                                        isExternalLinks = true;
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
                                    final boolean isP = targetTagName.equals("p"), isDiv = targetTagName.equals("div");
                                    isRelease = isP || isDiv;
                                    if(isP) {
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
                            if(!setPremise) {
                                premise = elements.select("p").get(0).text();
                            }
                        }
                        final EventSource wikipage = new EventSource("Wikipedia: " + title, wikipageURL);
                        final EventSources sources = new EventSources(listOfAmericanFilmsSource, wikipage);
                        for(EventSource source : externalSources.getSources()) {
                            sources.addSource(source);
                        }
                        final Elements targetImage = wikidoc.select("div.mw-parser-output table.infobox tbody tr td a img");
                        final String imgURL = !targetImage.isEmpty() ? targetImage.get(0).attr("src") : null;
                        final String img = imgURL != null ? "https:" + imgURL : null;
                        final MovieEvent movie = new MovieEvent(date, title, premise, img, productionCompany, releaseInfo, sources);
                        MOVIES.add(movie);
                        if(MOVIES.size() == maxHandlers.get()) {
                            handler.handle(MOVIES);
                        }
                    }).start();
                } else {
                    maxHandlers.addAndGet(-1);
                }
            }
        }
    }
    private String getWikipageURL(Element titleElement) {
        final Elements hrefs = titleElement.select("i").get(0).select("a[href]");
        if(!hrefs.isEmpty()) {
            return "https://en.wikipedia.org" + hrefs.get(0).attr("href");
        } else {
            return null;
        }
    }
}
