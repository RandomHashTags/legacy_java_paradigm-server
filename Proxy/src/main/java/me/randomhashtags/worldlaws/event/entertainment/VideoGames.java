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
import java.util.*;

public enum VideoGames implements EventController {
    INSTANCE;

    private String json;
    private Month MONTH;
    private int DAY;
    private boolean isFirst;
    private StringBuilder builder;

    @Override
    public String getIdentifier() {
        return "videogames";
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            final int thisYear = LocalDate.now().getYear();
            isFirst = true;
            builder = new StringBuilder("[");
            final long time = System.currentTimeMillis();
            refreshUpcomingVideoGames(thisYear, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    refreshUpcomingVideoGames(thisYear+1, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            builder.append("]");
                            json = builder.toString();
                            System.out.println("Video Games - updated upcoming Video Game releases (took " + (System.currentTimeMillis()-time) + "ms)");
                            handler.handle(json);
                        }
                    });
                }
            });
        }
    }

    @Override
    public Country getCountryOrigin() {
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
        final EventSource source = new EventSource("Wikipedia", "");
        final EventSources sources = new EventSources(source);
        for(Element element : array) {
            if(months.contains(element)) {
                isMonth = true;
            } else if(isMonth) {
                final String name = element.tagName();
                if(name.equals("table") && element.attr("class").equals("wikitable")) {
                    final Elements rows = element.select("tbody tr");
                    rows.remove(0);

                    for(Element row : rows) {
                        checkMonth(row.text(), keys);
                        final EventDate date = new EventDate(MONTH, DAY, year);
                        final String title = row.select("td i").get(0).text();
                        final VideoGameEvent event = new VideoGameEvent(date, title, "videogamedesc", sources);
                        builder.append(isFirst ? "" : ",").append(event.toJSON());
                        isFirst = false;
                    }
                    isMonth = false;
                }
            }
        }
        handler.handle(null);
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
}
