package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.ChampionshipEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Championships extends UpcomingEventController { // https://en.wikipedia.org/wiki/2021_in_sports

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_CHAMPIONSHIPS;
    }

    @Override
    public void load() {
        final int year = WLUtilities.getTodayYear();
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_sports_by_month";
        final Document doc = getDocument(url);
        if(doc != null) {
            final LocalDate date = LocalDate.now();
            final Month thisMonth = date.getMonth(), previousMonth = date.minusMonths(1).getMonth();
            final int thisMonthInt = thisMonth.getValue(), previousMonthInt = previousMonth.getValue();
            final Elements tables = doc.select("h3 + table.wikitable");
            final List<Runnable> test = Arrays.asList(
                    () -> loadPreEventsFrom(year, thisMonth, tables.get(thisMonthInt-1)),
                    () -> loadPreEventsFrom(year, previousMonth, tables.get(previousMonthInt-1))
            );
            new CompletableFutures<Runnable>().stream(test.stream(), Runnable::run, 2);
        }
    }

    private void loadPreEventsFrom(int year, Month month, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.removeIf(tr -> {
            final Elements tds = tr.select("td");
            if(tds.size() > 0) {
                final String text = tds.get(tds.size()-1).text().toLowerCase();
                return text.equals("postponed") || text.contains("postponed to") || text.contains("cancelled") || text.contains("canceled");
            }
            return true;
        });
        final String hyphen = "???", space = " ", urlPrefix = "https://en.wikipedia.org";
        for(Element tr : trs) {
            final Elements tds = tr.select("td");
            String day = tds.get(0).text();
            if(!day.equals("?")) {
                Month endingMonth = null;
                int endingYear = -1;
                if(day.contains(space)) {
                    final String[] values = day.split(space);
                    final int length = values.length;
                    String targetEndingMonth = values[length-1];
                    if(targetEndingMonth.matches("[0-9]+")) {
                        endingYear = Integer.parseInt(targetEndingMonth);
                        targetEndingMonth = values[length-2];
                    }
                    endingMonth = WLUtilities.valueOfMonthFromInput(targetEndingMonth);
                    day = values[0];
                }
                int startingDay = -1, endingDay = -1;
                if(day.contains(hyphen)) {
                    final String[] values = day.split(hyphen);
                    startingDay = Integer.parseInt(values[0]);
                    final String[] endingDays = values[1].split("/");
                    endingDay = Integer.parseInt(endingDays[endingDays.length-1]);
                    if(endingMonth == null) {
                        endingMonth = month;
                    }
                    if(endingYear == -1) {
                        endingYear = year;
                    }
                }

                final EventDate startingDate = new EventDate(month, startingDay, year);
                final String startingDateString = startingDate.getDateString();
                final String sport = tds.get(1).text();

                final Element eventElement = tds.get(2);
                String event = eventElement.text();
                while (event.startsWith("/") || event.startsWith(" ")) {
                    event = event.substring(1);
                }
                final Elements eventElementLinks = eventElement.select("a[href]");
                String eventURL = null;
                final List<String> countries = new ArrayList<>();
                if(!eventElementLinks.isEmpty()) { // TODO: fix more than 1 event per row (example: Men & Women's sporting event, same row, same dates)
                    final Elements flagIconElements = eventElement.select("span.flagicon");
                    for(Element flagIcon : flagIconElements) {
                        final Element flagElement = flagIcon.selectFirst("a[href]");
                        if(flagElement != null) {
                            final String[] href = flagElement.attr("href").split("/");
                            final String country = href[href.length-1].toLowerCase().replace("(country)", "");
                            countries.add(country.replace(" ", "").replace("_", ""));
                        }
                    }
                    final int max = flagIconElements.size();
                    if(eventElementLinks.size() > max) {
                        final Element element = eventElementLinks.get(max);
                        if(!element.hasAttr("class") || !element.attr("class").equals("new")) {
                            eventURL = urlPrefix + element.attr("href");
                        }
                    }
                }

                final String winners = tds.get(tds.size()-1).text();
                boolean finished = false;
                if(!winners.isEmpty()) {
                    finished = !winners.contains("postponed from");
                }

                if(!finished) {
                    if(endingMonth != null && endingDay != -1 && endingYear != -1) {
                        final EventDate endingDate = new EventDate(endingMonth, endingDay, endingYear);
                        final LocalDate endingLocalDate = endingDate.getLocalDate();
                        LocalDate startingLocalDate = startingDate.getLocalDate();
                        int usages = 0;
                        while (startingLocalDate.isBefore(endingLocalDate) || startingLocalDate.equals(endingLocalDate)) {
                            final EventDate eventDate = new EventDate(startingLocalDate);
                            final String id = getEventDateIdentifier(eventDate.getDateString(), event);
                            final String suffix = ", " + (usages == 0 ? "BEGINS TODAY" : startingLocalDate.equals(endingLocalDate) ? "ENDS TODAY" : "CONTINUED");
                            final String tag = sport + suffix;
                            final ClientEmoji clientEmoji = ClientEmoji.valueOfString(sport);
                            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, event, eventURL, tag, countries);
                            preUpcomingEvent.setClientEmoji(clientEmoji);
                            putPreUpcomingEvent(id, preUpcomingEvent);
                            startingLocalDate = startingLocalDate.plusDays(1);
                            usages += 1;
                        }
                    } else {
                        final String id = getEventDateIdentifier(startingDateString, event);
                        final ClientEmoji clientEmoji = ClientEmoji.valueOfString(sport);
                        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, event, eventURL, sport, countries);
                        preUpcomingEvent.setClientEmoji(clientEmoji);
                        putPreUpcomingEvent(id, preUpcomingEvent);
                    }
                }
            }
        }
    }

    @Override
    public UpcomingEvent loadUpcomingEvent(String id) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String url = preUpcomingEvent.getURL();
        if(url != null) {
            final String title = preUpcomingEvent.getTitle(), tag = preUpcomingEvent.getTag();
            final WikipediaDocument wikiDoc = new WikipediaDocument(url);
            final EventSources sources = new EventSources(new EventSource("Wikipedia: " + wikiDoc.getPageName(), url));
            if(tag.toLowerCase().startsWith("chess")) {
                sources.add(new EventSource("Chess: Events", "https://www.chess.com/events"));
                sources.add(new EventSource("Twitch: Chess", "https://www.twitch.tv/chess"));
            }
            sources.addAll(wikiDoc.getExternalLinks());

            final List<String> images = wikiDoc.getImages();
            final String imageURL = !images.isEmpty() ? images.get(0) : null;
            final StringBuilder builder = new StringBuilder();
            final List<Element> paragraphs = wikiDoc.getConsecutiveParagraphs();
            if(paragraphs != null) {
                boolean isFirst = true;
                for(Element node : paragraphs) {
                    builder.append(isFirst ? "" : "\n").append(node.text());
                    isFirst = false;
                }
            }
            final String description = builder.length() == 0 ? null : builder.toString();
            final List<String> countries = preUpcomingEvent.getCountries();
            final String location = countries != null && !countries.isEmpty() ? countries.get(0) : null;
            return new ChampionshipEvent(preUpcomingEvent.getEventDate(), title, description, imageURL, location, sources);
        }
        return null;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new ChampionshipEvent(json);
    }
}
