package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.upcoming.events.PresentationEvent;
import me.randomhashtags.worldlaws.upcoming.events.PresentationEventType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public enum PresentationType implements Jsoupable {
    //ACADEMY_AWARDS("https://en.wikipedia.org/wiki/Academy_Awards", PresentationEventType.AWARD_CEREMONY),
    BLIZZCON("https://en.wikipedia.org/wiki/BlizzCon", PresentationEventType.CONVENTION_GAMING),
    COACHELLA("https://en.wikipedia.org/wiki/Coachella_Valley_Music_and_Arts_Festival", PresentationEventType.FESTIVAL_MUSIC),
    E3("https://en.wikipedia.org/wiki/E3", PresentationEventType.EXPO_GAMING),
    //EGX("https://en.wikipedia.org/wiki/EGX_(expo)", PresentationEventType.EXPO_GAMING),
    //G20("https://en.wikipedia.org/wiki/List_of_G20_summits", PresentationEventType.SUMMIT),
    //GAME_DEVELOPERS_CONFERENCE("https://en.wikipedia.org/wiki/Game_Developers_Conference", PresentationEventType.CONFERENCE),
    //GAMESCON("https://en.wikipedia.org/wiki/Gamescom", PresentationEventType.EXPO_GAMING),
    GOLDEN_GLOBE_AWARDS("https://en.wikipedia.org/wiki/List_of_Golden_Globe_Awards_ceremonies", PresentationEventType.AWARD_CEREMONY),
    GOOGLE_IO("https://en.wikipedia.org/wiki/Google_I/O", PresentationEventType.CONFERENCE_DEVELOPER),
    MET_GALA("https://en.wikipedia.org/wiki/Met_Gala", PresentationEventType.EXHIBIT_FASHION),
    //MINECON("https://en.wikipedia.org/wiki/Minecon", PresentationEventType.CONVENTION_GAMING),
    NINTENDO_DIRECT("https://en.wikipedia.org/wiki/Nintendo_Direct", PresentationEventType.PRESENTATION),
    //PAX("https://en.wikipedia.org/wiki/List_of_PAX_events", PresentationEventType.FESTIVAL_GAMING),
    //SOUTH_BY_SOUTHWEST("https://en.wikipedia.org/wiki/South_by_Southwest", PresentationEventType.FESTIVAL_MUSIC),
    TWITCHCON("https://en.wikipedia.org/wiki/TwitchCon", PresentationEventType.CONVENTION_GAMING),
    //TWITCH_RIVALS("https://en.wikipedia.org/wiki/Twitch_Rivals", PresentationEventType.TOURNAMENT_GAMING),
    ;

    private final String url;
    private final PresentationEventType type;

    PresentationType(String url, PresentationEventType type) {
        this.url = url;
        this.type = type;
    }

    public List<PresentationEvent> refresh(LocalDate startingDay) {
        final List<PresentationEvent> events = get();
        if(events != null) {
            final EventSources keySources = getSources();
            events.removeIf(event -> event.getDate().getLocalDate().isBefore(startingDay));
            for(PresentationEvent event : events) {
                final EventSources sources = new EventSources(keySources);
                sources.addAll(event.getExternalLinks());
                event.setSources(sources);
                event.setCustomTypeSingularName(type.getName());
            }
        }
        return events == null || events.isEmpty() ? null : events;
    }

    private List<PresentationEvent> get() {
        switch (this) {
            case BLIZZCON: return refreshBlizzCon();
            case COACHELLA: return refreshCoachella();
            case E3: return refreshE3();
            case GOLDEN_GLOBE_AWARDS: return refreshGoldenGlobeAwards();
            case GOOGLE_IO: return refreshGoogleIO();
            case MET_GALA: return refreshMetGala();
            case NINTENDO_DIRECT: return refreshNintendoDirect();
            case TWITCHCON: return refreshTwitchCon();
            default: return null;
        }
    }
    private EventSources getSources() {
        final EventSources sources = new EventSources();
        final String wikipediaName = url.substring("https://en.wikipedia.org/wiki/".length()).replace("_", " ");
        sources.add(new EventSource("Wikipedia: " + wikipediaName, url));
        return sources;
    }

    private List<EventDate> parseDatesFrom(int year, String input) {
        final List<EventDate> list = new ArrayList<>();
        addDaysFrom(list, input, year);
        return list;
    }
    private void addDaysFrom(List<EventDate> list, String input, int year) {
        input = LocalServer.removeWikipediaReferences(input);
        final String[] values = input.split(" ");
        final Month month = WLUtilities.valueOfMonthFromInput(values[0]);
        if(month != null) {
            final String[] targetDays = values[1].split("–");
            if(targetDays.length > 1) {
                final int startingDay = Integer.parseInt(targetDays[0]), endingDay = Integer.parseInt(targetDays[1]);
                for(int day = startingDay; day <= endingDay; day++) {
                    final EventDate date = new EventDate(month, day, year);
                    list.add(date);
                }
            } else {
                final int day = Integer.parseInt(targetDays[0]);
                final EventDate date = new EventDate(month, day, year);
                list.add(date);
            }
        }
    }

    private List<PresentationEvent> refreshBlizzCon() {
        final String title = "BlizzCon", location = "Anaheim, California, United States", imageURL = null;
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("table.wikitable");
        final Elements elements = table.select("tbody tr");
        elements.removeIf(element -> element.select("td").size() < 7);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int year = Integer.parseInt(tds.get(0).text());
            final List<EventDate> dates = parseDatesFrom(year, tds.get(1).text());
            if(!dates.isEmpty()) {
                final EventDate lastDate = dates.get(dates.size()-1);
                boolean isFirst = true;
                for(EventDate date : dates) {
                    final String tag = title + ", " + (isFirst ? "BEGINS TODAY" : date.equals(lastDate) ? "ENDS TODAY" : "CONTINUED");
                    final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, tag, externalLinks);
                    events.add(event);
                    isFirst = false;
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshCoachella() {
        final WikipediaDocument doc = new WikipediaDocument(url);
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("h2 + table.wikitable");
        if(table != null) {
            final EventSources externalLinks = doc.getExternalLinks();
            final String location = "Indio, California, United States", imageURL = null;
            final String description = doc.getDescription();
            final Elements elements = table.select("tbody tr");
            elements.removeIf(element -> element.select("td").size() < 7);
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final int year = Integer.parseInt(tds.get(1).text());
                final Element datesElement = tds.get(2);
                final Element dateElementList = datesElement.selectFirst("ul");
                final List<EventDate> dates = new ArrayList<>();
                if(dateElementList != null) {
                    for(Element listElement : dateElementList.select("li")) {
                        dates.addAll(parseDatesFrom(year, listElement.text()));
                    }
                } else {
                    dates.addAll(parseDatesFrom(year, datesElement.text()));
                }

                if(!dates.isEmpty()) {
                    final String title = "Coachella";
                    final EventDate lastDate = dates.get(dates.size()-1);
                    boolean isFirst = true;
                    for(EventDate date : dates) {
                        final String tag = title + ", " + (isFirst ? "BEGINS TODAY" : lastDate.equals(date) ? "ENDS TODAY" : "CONTINUED");
                        final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, tag, externalLinks);
                        events.add(event);
                        isFirst = false;
                    }
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshE3() {
        final String title = "E3", imageURL = null;
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();

        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("table.wikitable");
        final Elements elements = table.select("tbody tr");
        elements.remove(0);
        String previousLocation = null;
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String dateString = LocalServer.removeWikipediaReferences(tds.get(1).text());
            if(!dateString.toLowerCase().contains("canceled") && dateString.contains(", ")) {
                final String[] values = dateString.split(", ");
                final int year = Integer.parseInt(values[1]);
                final String targetLocation = LocalServer.removeWikipediaReferences(tds.get(2).text()).replace(",", "");
                final boolean hasLocation = !targetLocation.matches("[0-9]+");
                String location = hasLocation ? targetLocation : previousLocation;
                if(location.toLowerCase().endsWith("california") || location.toLowerCase().endsWith("georgia")) {
                    location = location + ", United States";
                }
                previousLocation = location;

                final List<EventDate> dates = parseDatesFrom(year, values[0]);
                if(!dates.isEmpty()) {
                    final String presenters = LocalServer.removeWikipediaReferences(tds.get(hasLocation ? 4 : 3).text()), notes = LocalServer.removeWikipediaReferences(tds.get(hasLocation ? 5 : 4).text());
                    final EventDate lastDate = dates.get(dates.size()-1);
                    boolean isFirst = true;
                    for(EventDate date : dates) {
                        final String tag = title + ", " + (isFirst ? "BEGINS TODAY" : date.equals(lastDate) ? "ENDS TODAY" : "CONTINUED");
                        final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, tag, externalLinks);
                        events.add(event);
                        isFirst = false;
                    }
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshGoldenGlobeAwards() {
        final String title = "Golden Globe Awards";
        final WikipediaDocument doc = new WikipediaDocument(url);
        final WikipediaDocument page = new WikipediaDocument("https://en.wikipedia.org/wiki/Golden_Globe_Awards");
        final String description = page.getDescription(), imageURL = null, location = "California, United States";
        final EventSources externalLinks = page.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Elements elements = doc.select("table.wikitable tbody tr");
        elements.remove(0);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String[] dateValues = LocalServer.removeWikipediaReferences(tds.get(1).text()).split(", ");
            final String[] dateArray = dateValues[0].split(" ");
            final Month month = WLUtilities.valueOfMonthFromInput(dateArray[0]);
            if(month != null) {
                final int year = Integer.parseInt(dateValues[dateValues.length-1]);
                final int day = Integer.parseInt(dateArray[1]);
                final EventDate date = new EventDate(month, day, year);
                final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, null, externalLinks);
                events.add(event);
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshGoogleIO() {
        final String title = "Google I/O", location = "California, United States", imageURL = null;
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("table.wikitable");
        final Elements elements = table.select("tbody tr");
        elements.remove(0);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int year = Integer.parseInt(tds.get(0).text());
            final String targetDate = LocalServer.removeWikipediaReferences(tds.get(1).text());
            if(!targetDate.toLowerCase().contains("cancelled")) {
                final List<EventDate> dates = parseDatesFrom(year, targetDate);
                if(!dates.isEmpty()) {
                    final EventDate lastDate = dates.get(dates.size()-1);
                    boolean isFirst = true;
                    for(EventDate date : dates) {
                        final String tag = title + ", " + (isFirst ? "BEGINS TODAY" : date.equals(lastDate) ? "ENDS TODAY" : "CONTINUED");
                        final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, tag, externalLinks);
                        events.add(event);
                        isFirst = false;
                    }
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshMetGala() {
        final String title = "Met Gala", location = "Fifth Avenue, Manhattan, New York City, New York, United States", imageURL = null;
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("table.wikitable");
        final Elements elements = table.select("tbody tr");
        elements.remove(0);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String dateString = LocalServer.removeWikipediaReferences(tds.get(0).text());
            if(!dateString.toLowerCase().contains("canceled")) {
                final String theme = LocalServer.removeWikipediaReferences(tds.get(1).text());

                final String[] dateValues = dateString.split(", ");
                final int year = Integer.parseInt(dateValues[1]);
                final List<EventDate> dates = parseDatesFrom(year, dateValues[0]);
                if(dates.size() > 0) {
                    final EventDate date = dates.get(0);
                    final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, null, externalLinks);
                    events.add(event);
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshNintendoDirect() {
        final WikipediaDocument doc = new WikipediaDocument(url);
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("div.legend + table.wikitable");
        if(table != null) {
            final String description = doc.getDescription();
            final EventSources externalLinks = doc.getExternalLinks();

            final Elements elements = table.select("tbody tr");
            elements.removeIf(element -> element.select("td").size() < 5);
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final String[] dateValues = LocalServer.removeWikipediaReferences(tds.get(2).text()).split(", ");
                final String[] dateArray = dateValues[0].split(" ");
                final Month month = WLUtilities.valueOfMonthFromInput(dateArray[0]);
                if(month != null) {
                    final int day = Integer.parseInt(dateArray[1]), year = Integer.parseInt(dateValues[1]);
                    final EventDate date = new EventDate(month, day, year);

                    final String title = tds.get(0).text(), topic = tds.get(1).text(), imageURL = null, location = null;
                    final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, null, externalLinks);
                    events.add(event);
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshTwitchCon() {
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Elements tables = doc.select("table.wikitable");
        for(int i = 0; i < 2; i++) {
            final String locationSuffix = i == 0 ? ", California, United States" : "";
            addEventsFromTwitchCon(events, tables.get(i), description, null, externalLinks, locationSuffix);
        }
        return events;
    }
    private void addEventsFromTwitchCon(List<PresentationEvent> list, Element table, String description, String imageURL, EventSources externalLinks, String locationSuffix) {
        final String title = "TwitchCon";
        final Elements elements = table.select("tbody tr");
        elements.remove(0);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String dateString = LocalServer.removeWikipediaReferences(tds.get(1).text());
            if(dateString.contains(", ") && !tds.get(4).text().toLowerCase().startsWith("cancelled")) {
                final String location = LocalServer.removeWikipediaReferences(tds.get(2).text()).replace("The Netherlands", "Netherlands").replace("RAI Amsterdam Convention Centre, ", "") + locationSuffix;
                final String[] values = dateString.split(", ");
                final int year = Integer.parseInt(values[1]);
                final List<EventDate> dates = parseDatesFrom(year, values[0]);

                boolean isFirst = true;
                if(!dates.isEmpty()) {
                    final EventDate lastDate = dates.get(dates.size()-1);
                    for(EventDate date : dates) {
                        final String tag = title + ", " + (isFirst ? "BEGINS TODAY" : date.equals(lastDate) ? "ENDS TODAY" : "CONTINUED");
                        final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, tag, externalLinks);
                        list.add(event);
                        isFirst = false;
                    }
                }
            }
        }
    }
}
