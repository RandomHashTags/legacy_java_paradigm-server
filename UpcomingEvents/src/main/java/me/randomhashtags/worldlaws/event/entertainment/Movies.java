package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.HashMap;
import java.util.Set;

public enum Movies implements EventController {
    INSTANCE;

    private HashMap<String, NewPreUpcomingEvent> preEventURLS;
    private HashMap<String, String> preUpcomingEvents, upcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    @Override
    public HashMap<String, String> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, NewPreUpcomingEvent> getPreEventURLs() {
        return preEventURLS;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preEventURLS = new HashMap<>();
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();
        refreshFilms(2021, handler);
    }

    private void refreshFilms(int year, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_American_films_of_" + year;
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-body-content div.mw-content-ltr div.mw-parser-output h2 + table.wikitable tbody tr");
            if(year >= 2016) { // supported format only until 2016
                refreshUSAFilms2016(year, table, handler);
            }
        }
    }
    private void refreshUSAFilms2016(int year, Elements table, CompletionHandler handler) {
        final Set<String> keys = MONTHS.keySet();
        Month month = null;
        int day = 1;
        for(Element element : table) {
            String text = element.text();
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
                day = isDay ? Integer.parseInt(value0) : day;

                final String dateString = month.getValue() + "-" + year + "-" + day;
                final Elements rows = element.select("td");
                if(!rows.isEmpty()) {
                    rows.removeIf(row -> row.hasAttr("style"));
                    final Element titleElement = rows.get(0);
                    final String wikipageURL = getWikipageURL(titleElement);
                    if(wikipageURL != null) {
                        final String title = titleElement.text();
                        final String id = dateString + "." + title.replace(" ", "");
                        final String productionCompany = rows.get(1).text();
                        final NewPreUpcomingEvent preUpcomingEvent = new NewPreUpcomingEvent(id, title, wikipageURL, productionCompany);
                        preEventURLS.put(id, preUpcomingEvent);
                    }
                }
            }
        }
        handler.handle(null);
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
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        if(upcomingEvents.containsKey(id)) {
            handler.handle(upcomingEvents.get(id));
        } else {
            final NewPreUpcomingEvent preUpcomingEvent = preEventURLS.get(id);
            final String url = preUpcomingEvent.getURL();
            final String title = preUpcomingEvent.getTitle(), productionCompany = preUpcomingEvent.getTag();
            final Document wikidoc = getDocument(url);
            if(wikidoc != null) {
                final int year = WLUtilities.getTodayYear();
                final EventSource listOfAmericanFilmsSource = new EventSource("Wikipedia: List of American films of " + year, "https://en.wikipedia.org/wiki/List_of_American_films_of_" + year);
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
                final EventSources sources = new EventSources(listOfAmericanFilmsSource, wikipage);
                sources.addSources(externalSources);
                final Elements targetImage = wikidoc.select("div.mw-parser-output table.infobox tbody tr td a img");
                final String imageSourceURL = !targetImage.isEmpty() ? targetImage.get(0).attr("src") : null;
                final String imageURL = imageSourceURL != null ? "https:" + imageSourceURL : null;
                final MovieEvent movie = new MovieEvent(title, premise, imageURL, productionCompany, releaseInfo, sources);
                final String string = movie.toJSON();
                upcomingEvents.put(id, string);
                final String preUpcomingEventString = preUpcomingEvent.getPreUpcomingEvent(imageURL).toString();
                preUpcomingEvents.put(id, preUpcomingEventString);
                handler.handle(string);
            } else {
                handler.handle(null);
            }
        }
    }
}
