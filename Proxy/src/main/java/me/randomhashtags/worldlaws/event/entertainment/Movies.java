package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.event.EventController;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.event.EventSources;
import me.randomhashtags.worldlaws.location.Country;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum Movies implements EventController {
    INSTANCE;

    private static String JSON;

    //private static final HashMap<Country, HashSet<MovieEvent>> COUNTRIES;
    private static final HashMap<Country, String> COUNTRIES_JSON;

    private static int MAX_HANDLERS;

    static {
        COUNTRIES_JSON = new HashMap<>();
    }

    @Override
    public String getIdentifier() {
        return "movies";
    }

    @Override
    public Country getCountryOrigin() {
        return null;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(JSON != null) {
            handler.handle(JSON);
        } else {
            final LocalDate date = LocalDate.now();
            final int year = date.getYear();
            refreshReleasedFilms(Country.UNITED_STATES_OF_AMERICA, year, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(COUNTRIES_JSON.get(Country.UNITED_STATES_OF_AMERICA));
                }
            });
        }
    }

    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Country country : COUNTRIES_JSON.keySet()) {
            final String array = COUNTRIES_JSON.get(country);
            final String string = "\"" + country.getName() + "\":" + array;
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
            COUNTRIES_JSON.put(country, array);
        }
        builder.append("}");
        JSON = builder.toString();
    }
    private void addMoviesJSON(Country country, HashSet<MovieEvent> movies) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(MovieEvent movie : movies) {
            builder.append(isFirst ? "" : ",").append(movie.toJSON());
            isFirst = false;
        }
        builder.append("]");
        final String string = builder.toString();
        COUNTRIES_JSON.put(country, string);
    }

    @SuppressWarnings({ "unchecked" })
    private void refreshReleasedFilms(Country country, int year, CompletionHandler handler) {
        final long time = System.currentTimeMillis();
        final CompletionHandler completion = new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final HashSet<MovieEvent> movies = (HashSet<MovieEvent>) object;
                addMoviesJSON(country, movies);
                updateJSON();
                System.out.println("Movies - updated upcoming " + country.getName() + " films (took " + (System.currentTimeMillis()-time) + "ms)");
                handler.handle(JSON);
            }
        };
        switch (country) {
            case UNITED_STATES_OF_AMERICA:
                refreshUSAFilms(year, completion);
                break;
            default:
                break;
        }
    }

    private void refreshUSAFilms(int year, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_American_films_of_" + year;
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("div.mw-body-content div.mw-content-ltr div.mw-parser-output h2 + table.wikitable tbody tr");
            if(year >= 2016) {
                refreshUSAFilms2016(year, url, table, handler);
            }
        }
    }
    private void refreshUSAFilms2016(int year, String url, Elements table, CompletionHandler handler) {
        final HashSet<MovieEvent> MOVIES = new HashSet<>();
        final Set<String> keys = MONTHS.keySet();
        Month month = null;
        int day = 1;
        final EventSource source = new EventSource("Wikipedia", url);
        MAX_HANDLERS = 0;
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
                MAX_HANDLERS += 1;
                final Element titleElement = rows.get(0);
                final String title = titleElement.text();
                final String productionCompany = rows.get(1).text();
                final String wikipageURL = "https://en.wikipedia.org" + titleElement.select("i").get(0).select("a[href]").get(0).attr("href");
                new Thread(() -> {
                    final Document wikidoc = getDocument(wikipageURL);
                    String releaseInfo = "", premise = "";
                    if(wikidoc != null) {
                        final Elements elements = wikidoc.select("div.mw-parser-output > *");
                        final Elements headlines = elements.select("h2");
                        boolean isRelease = false, isPremise = false;
                        for(Element target : elements) {
                            final String name = target.tagName();
                            final boolean isHeadline = headlines.contains(target);
                            final String targetID = target.select("span.mw-headline").text();
                            if(isHeadline) {
                                if(targetID.equals("Premise")) {
                                    isPremise = true;
                                } else if(targetID.equals("Release")) {
                                    isRelease = true;
                                }
                            } else if(isPremise) {
                                final boolean isTarget = name.equals("blockquote") || name.equals("p");
                                if(isTarget) {
                                    premise = target.text();
                                    isPremise = false;
                                }
                            } else if(isRelease) {
                                final boolean isP = name.equals("p"), isDiv = name.equals("div");
                                isRelease = isP || isDiv;
                                if(isP) {
                                    releaseInfo = releaseInfo.concat(target.text());
                                }
                                if(!isRelease) {
                                    break;
                                }
                            }
                        }
                    }
                    final EventSource wikipage = new EventSource("Wikipedia", wikipageURL);
                    final EventSources sources = new EventSources(source, wikipage);
                    final Elements targetImage = wikidoc.select("div.mw-parser-output table.infobox tbody tr td a img");
                    final String imgURL = !targetImage.isEmpty() ? targetImage.get(0).attr("src") : null;
                    final String img = imgURL != null ? "https:" + imgURL : null;
                    final MovieEvent movie = new MovieEvent(date, title, premise, img, productionCompany, releaseInfo, sources);
                    MOVIES.add(movie);
                    if(MOVIES.size() == MAX_HANDLERS) {
                        handler.handle(MOVIES);
                    }
                }).start();

            }
        }
    }
}
