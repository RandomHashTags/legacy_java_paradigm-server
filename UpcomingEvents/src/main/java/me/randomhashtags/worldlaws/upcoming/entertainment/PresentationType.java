package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.upcoming.events.PresentationEvent;
import me.randomhashtags.worldlaws.upcoming.events.PresentationEventType;
import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public enum PresentationType implements Jsoupable {
    APPLE_EVENT(
            getNames("Apple Event"),
            "https://www.apple.com/apple-events/",
            PresentationEventType.PRESENTATION,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Apple_logo_black.svg/%quality%px-Apple_logo_black.svg.png"
    ),
    APPLE_EVENT_WWDC(
            getNames("World Wide Developers Conference", "WWDC"),
            "https://en.wikipedia.org/wiki/Apple_Worldwide_Developers_Conference",
            PresentationEventType.PRESENTATION,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Apple_WWDC_wordmark.svg/%quality%px-Apple_WWDC_wordmark.svg.png"
    ),

    /*ACADEMY_AWARDS(
            "Academy Awards",
            "https://en.wikipedia.org/wiki/Academy_Awards",
            PresentationEventType.AWARD_CEREMONY,
            "https://upload.wikimedia.org/wikipedia/en/thumb/7/7f/Academy_Award_trophy.png/%quality%px-Academy_Award_trophy.png"
    ),*/
    BLIZZCON(
            "BlizzCon",
            "https://en.wikipedia.org/wiki/BlizzCon",
            PresentationEventType.CONVENTION_GAMING,
            "https://upload.wikimedia.org/wikipedia/en/thumb/5/51/BlizzCon_logo.svg/%quality%px-BlizzCon_logo.svg.png"
    )
    ,
    COACHELLA(
            "Coachella",
            "https://en.wikipedia.org/wiki/Coachella_Valley_Music_and_Arts_Festival",
            PresentationEventType.FESTIVAL_MUSIC,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Coachella_Valley_Music_and_Arts_Festival_logo.svg/%quality%px-Coachella_Valley_Music_and_Arts_Festival_logo.svg.png"
    ),
    E3(
            "E3",
            "https://en.wikipedia.org/wiki/E3",
            PresentationEventType.EXPO_GAMING,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/E3_Logo.svg/%quality%px-E3_Logo.svg.png"
    ),
    /*EGX(
            "EGX",
            "https://en.wikipedia.org/wiki/EGX_(expo)",
            PresentationEventType.EXPO_GAMING,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/EGX_2014.jpg/%quality%px-EGX_2014.jpg"
    ),*/
    /*G20(
            "G20 summit",
            "https://en.wikipedia.org/wiki/List_of_G20_summits",
            PresentationEventType.SUMMIT,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/G20_map.png/%quality%px-G20_map.png"
    ),*/
    GAME_DEVELOPERS_CONFERENCE(
            "Game Developers Conference",
            "https://en.wikipedia.org/wiki/Game_Developers_Conference",
            PresentationEventType.CONFERENCE,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Game_Developers_Conference_logo.svg/%quality%px-Game_Developers_Conference_logo.svg.png"
    ),
    /*GAMESCOM(
            "Gamescom",
            "https://en.wikipedia.org/wiki/Gamescom",
            PresentationEventType.CONVENTION_GAMING,
            "https://upload.wikimedia.org/wikipedia/en/thumb/7/7a/Games_Convention_Logo.png/%quality%px-Games_Convention_Logo.png"
    ),*/
    GOLDEN_GLOBE_AWARDS(
            "Golden Globe Awards",
            "https://en.wikipedia.org/wiki/List_of_Golden_Globe_Awards_ceremonies",
            PresentationEventType.AWARD_CEREMONY,
            "https://upload.wikimedia.org/wikipedia/en/thumb/0/09/Golden_Globe_Trophy.jpg/%quality%px-Golden_Globe_Trophy.jpg"
    ),
    /*
    GOLDEN_JOYSTICK_AWARDS(
            "Golden Joystick Awards",
            "https://en.wikipedia.org/wiki/Golden_Joystick_Awards",
            PresentationEventType.AWARD_CEREMONY,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Golden_Joystick_Award_%28Award_Cup%29.svg/%quality%px-Golden_Joystick_Award_%28Award_Cup%29.svg.png"
    ),*/
    GOOGLE_IO(
            "Google I/O",
            "https://en.wikipedia.org/wiki/Google_I/O",
            PresentationEventType.CONFERENCE_DEVELOPER,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_IO_logo.svg/%quality%px-Google_IO_logo.svg.png"
    ),
    /*
    LOLLAPALOOZA(
            "Lollapalooza",
            "https://en.wikipedia.org/wiki/Lollapalooza",
            PresentationEventType.FESTIVAL_MUSIC,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Lollapalooza_logo.svg/%quality%px-Lollapalooza_logo.svg.png"
    ),*/
    MET_GALA(
            "Met Gala",
            "https://en.wikipedia.org/wiki/Met_Gala",
            PresentationEventType.EXHIBIT_FASHION,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/The_MET.jpg/%quality%px-The_MET.jpg"
    ),
    /*MINECON(
            "Minecon",
            "https://en.wikipedia.org/wiki/Minecon",
            PresentationEventType.CONVENTION_GAMING,
            "https://upload.wikimedia.org/wikipedia/en/thumb/3/3a/Minecon_logo.png/%quality%px-Minecon_logo.png"
    ),*/
    NINTENDO_DIRECT(
            "Nintendo Direct",
            "https://en.wikipedia.org/wiki/Nintendo_Direct",
            PresentationEventType.PRESENTATION,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Nintendo_Direct_logo.svg/%quality%px-Nintendo_Direct_logo.svg.png"
    ),
    /*PAX(
            "PAX",
            "https://en.wikipedia.org/wiki/List_of_PAX_events",
            PresentationEventType.FESTIVAL_GAMING,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/PAX_new_logo%2C_blue.svg/%quality%px-PAX_new_logo%2C_blue.svg.png"
    ),*/
    /*PLAYSTATION_STATE_OF_PLAY(
            "PlayStation State of Play",
            "https://www.playstation.com/en-us/state-of-play",
            PresentationEventType.PRESENTATION
    ),*/
    /*POKEMON_GO(
            getNames("Pokemon Go Community Day", "Pokemon Go Fest", "Pokemon Go Safazri Zone"),
            "https://pokemongolive.com/events/",
            PresentationEventType.EVENT_GATHERING
    ),*/
    /*SOUTH_BY_SOUTHWEST(
            "South by Southwest",
            "https://en.wikipedia.org/wiki/South_by_Southwest",
            PresentationEventType.FESTIVAL_MUSIC,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8b/SXSW_Stack_logo.svg/%quality%px-SXSW_Stack_logo.svg.png"
    ),*/
    /*THE_GAME_AWARDS(
            "The Game Awards",
            "https://en.wikipedia.org/wiki/The_Game_Awards",
            PresentationEventType.AWARD_CEREMONY,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/The_Game_Awards_logo_2020.svg/%quality%px-The_Game_Awards_logo_2020.svg.png"
    ),*/
    TWITCHCON(
            "TwitchCon",
            "https://en.wikipedia.org/wiki/TwitchCon",
            PresentationEventType.CONVENTION_GAMING,
            "https://upload.wikimedia.org/wikipedia/en/thumb/d/d4/Twitchcon_logo.svg/%quality%px-Twitchcon_logo.svg.png"
    ),
    /*TWITCH_RIVALS(
            "Twitch Rivals",
            "https://en.wikipedia.org/wiki/Twitch_Rivals",
            PresentationEventType.TOURNAMENT_GAMING,
            "https://upload.wikimedia.org/wikipedia/en/thumb/e/e1/Twitchrivals.svg/%quality%px-Twitchrivals.svg.png"
    ),*/
    ;

    private static String[] getNames(String...names) {
        return names;
    }


    private final String[] names;
    private final String url, defaultImageURL;
    private final PresentationEventType type;

    PresentationType(String name, String url, PresentationEventType type) {
        this(new String[] { name }, url, type);
    }
    PresentationType(String[] names, String url, PresentationEventType type) {
        this(names, url, type, null);
    }
    PresentationType(String name, String url, PresentationEventType type, String defaultImageURL) {
        this(new String[] { name }, url, type, defaultImageURL);
    }
    PresentationType(String[] names, String url, PresentationEventType type, String defaultImageURL) {
        this.names = names;
        this.url = url;
        this.type = type;
        this.defaultImageURL = defaultImageURL;
    }

    public static JSONObjectTranslatable getTypesJSON() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("types");
        json.put(WLUtilities.RESPONSE_VERSION_KEY, ResponseVersions.PRESENTATIONS.getValue());

        final JSONObjectTranslatable typesJSON = new JSONObjectTranslatable();
        for(PresentationType type : values()) {
            final String defaultImageURL = type.defaultImageURL;
            final JSONObjectTranslatable typeJSON = new JSONObjectTranslatable("names", "type");
            typeJSON.put("names", new JSONArray(type.names));
            typeJSON.put("type", type.type.getName());
            if(defaultImageURL != null) {
                typeJSON.put("defaultImageURL", defaultImageURL);
            }
            final String typeID = type.name().toLowerCase();
            typesJSON.put(typeID, typeJSON);
        }
        json.put("types", typesJSON);
        return json;
    }

    public List<PresentationEvent> refresh(LocalDate startingDay) {
        final List<PresentationEvent> events = get();
        if(events != null) {
            final EventSources keySources = getSources();
            events.removeIf(event -> event.getDate().getLocalDate().isBefore(startingDay));
            for(PresentationEvent event : events) {
                final EventSources sources = new EventSources(keySources), externalLinks = event.getExternalLinks();
                if(externalLinks != null && !externalLinks.isEmpty()) {
                    sources.addAll(externalLinks);
                }
                event.setSources(sources);
                event.setCustomTypeSingularName(type.getName());
            }
        }
        return events == null || events.isEmpty() ? null : events;
    }

    private List<PresentationEvent> get() {
        switch (this) {
            case APPLE_EVENT: return refreshAppleEvent();
            case APPLE_EVENT_WWDC: return refreshAppleEventWWDC();
            case BLIZZCON: return refreshBlizzCon();
            case COACHELLA: return refreshCoachella();
            case E3: return refreshE3();
            case GAME_DEVELOPERS_CONFERENCE: return refreshGameDevelopersConference();
            case GOLDEN_GLOBE_AWARDS: return refreshGoldenGlobeAwards();
            case GOOGLE_IO: return refreshGoogleIO();
            case MET_GALA: return refreshMetGala();
            case NINTENDO_DIRECT: return refreshNintendoDirect();
            //case TWITCHCON: return refreshTwitchCon(); wikipedia editors broke it
            default: return null;
        }
    }

    private EventSources getSources() {
        final EventSources sources = new EventSources();
        switch (this) {
            case APPLE_EVENT:
                sources.add(new EventSource("Apple: Apple Events", url));
                break;
            default:
                final String wikipediaName = url.substring("https://en.wikipedia.org/wiki/".length()).replace("_", " ");
                sources.add(new EventSource("Wikipedia: " + wikipediaName, url));
                break;
        }
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
            final String targetValue = values[1];
            final boolean isHyphen = targetValue.contains("-");
            final String[] targetDays = targetValue.split(isHyphen ? "-" : "–");
            if(targetDays.length > 1) {
                final int startingDay = Integer.parseInt(targetDays[0]);
                final String targetEndingDay = targetDays[1];
                Month endingMonth = WLUtilities.valueOfMonthFromInput(targetEndingDay);
                if(endingMonth != null) {
                    final int endingDay = Integer.parseInt(values[values.length-1]);
                    for(int day = startingDay; day <= month.maxLength(); day++) {
                        final EventDate eventDate = new EventDate(month, day, year);
                        list.add(eventDate);
                    }
                    for(int day = 1; day <= endingDay; day++) {
                        final EventDate eventDate = new EventDate(endingMonth, day, year);
                        list.add(eventDate);
                    }
                } else {
                    final int endingDay = Integer.parseInt(targetEndingDay);
                    for(int day = startingDay; day <= endingDay; day++) {
                        final EventDate date = new EventDate(month, day, year);
                        list.add(date);
                    }
                }
            } else {
                final int day = Integer.parseInt(targetDays[0]);
                final EventDate date = new EventDate(month, day, year);
                list.add(date);
            }
        }
    }
    private void iterateDays(List<EventDate> dates, List<PresentationEvent> events, String title, String description, String imageURL, String location, EventSources externalLinks) {
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

    private List<PresentationEvent> refreshAppleEvent() {
        final String location = "apple.com";
        final List<PresentationEvent> events = new ArrayList<>();
        final Document doc = getDocument(url);
        if(doc != null) {
            final Element container = doc.selectFirst("div.section-content div.copy-container");
            if(container != null) {
                final Element header = container.selectFirst("h1");
                if(header != null) {
                    final String title = header.text();
                    final Element paragraph = container.selectFirst("p");
                    if(paragraph != null) {
                        final String text = paragraph.text(), textLowercase = text.toLowerCase();
                        final String[] prefixes = {
                                "watch on ",
                                "watch the event on ",
                                "watch tomorrow at "
                        };
                        for(String prefix : prefixes) {
                            if(textLowercase.startsWith(prefix)) {
                                final boolean isTomorrow = textLowercase.contains("tomorrow");
                                final int year, day;
                                final Month month;
                                final LocalDate now = LocalDate.now();
                                LocalDate localDate = null;
                                if(isTomorrow) {
                                    localDate = now.plusDays(1);
                                } else {
                                    final String[] values = textLowercase.substring(prefix.length()).split(" ");
                                    if(values[0].contains("/")) {
                                        final String[] dateValues = values[0].split("/");
                                        if(dateValues[0].matches("[0-9]+") && dateValues[1].matches("[0-9]+")) {
                                            year = now.getYear();
                                            month = Month.of(Integer.parseInt(dateValues[0]));
                                            day = Integer.parseInt(dateValues[1]);
                                            localDate = LocalDate.of(year, month, day);
                                        }
                                    }
                                }

                                if(localDate != null) {
                                    final String description = "View online at apple.com or on the Apple TV app.";
                                    // u = 21
                                    final String imageURL = "https://www.apple.com/v/apple-events/home/u/images/overview/hero__d6adldydsqye_xlarge_2x.jpg";
                                    final EventDate date = new EventDate(localDate);
                                    final PresentationEvent event = new PresentationEvent(date, title, description, imageURL, location, null, null);
                                    events.add(event);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshAppleEventWWDC() {
        final String title = "WWDC", imageURL = defaultImageURL;
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("table.wikitable");
        if(table != null) {
            final Elements elements = table.select("tbody tr");
            elements.remove(0);
            String previousLocation = null;
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final int size = tds.size();
                if(size >= 2) {
                    if(size == 3) {
                        previousLocation = tds.get(2).text();
                    }
                    final int year = Integer.parseInt(tds.get(0).text());
                    final String dates = tds.get(1).text();
                    final List<EventDate> eventDates = parseDatesFrom(year, dates);
                    iterateDays(eventDates, events, title, description, imageURL, previousLocation, externalLinks);
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshBlizzCon() {
        final String title = "BlizzCon", location = "Anaheim, California, United States";
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
            iterateDays(dates, events, title, description, defaultImageURL, location, externalLinks);
        }
        return events;
    }
    private List<PresentationEvent> refreshCoachella() {
        final String title = "Coachella";
        final WikipediaDocument doc = new WikipediaDocument(url);
        final List<PresentationEvent> events = new ArrayList<>();
        final Element table = doc.selectFirst("h2 + table.wikitable");
        if(table != null) {
            final EventSources externalLinks = new EventSources(
                    new EventSource("Twitter: Coachella", "https://twitter.com/coachella")
            );
            final EventSources docExternalLinks = doc.getExternalLinks();
            if(docExternalLinks != null) {
                externalLinks.addAll(docExternalLinks);
            }
            final String location = "Indio, California, United States";
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
                iterateDays(dates, events, title, description, defaultImageURL, location, externalLinks);
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshE3() {
        final String title = "E3";
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
                    final int maxElement = tds.size();
                    final Element presentersElement = tds.get(maxElement-2), notesElement = tds.get(maxElement-1);
                    final String presenters = LocalServer.removeWikipediaReferences(presentersElement.text()), notes = LocalServer.removeWikipediaReferences(notesElement.text());
                    final EventDate lastDate = dates.get(dates.size()-1);
                    boolean isFirst = true;
                    for(EventDate date : dates) {
                        final String tag = title + ", " + (isFirst ? "BEGINS TODAY" : date.equals(lastDate) ? "ENDS TODAY" : "CONTINUED");
                        final PresentationEvent event = new PresentationEvent(date, title, description, defaultImageURL, location, tag, externalLinks);
                        events.add(event);
                        isFirst = false;
                    }
                }
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshGameDevelopersConference() {
        final String title = "Game Developers Conference";
        final WikipediaDocument doc = new WikipediaDocument(url);
        final String description = doc.getDescription();
        final EventSources externalLinks = doc.getExternalLinks();
        final List<PresentationEvent> events = new ArrayList<>();
        final Elements elements = doc.select("table.wikitable tbody tr");
        elements.removeIf(element -> element.select("td").size() != 4);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String[] conferenceValues = tds.get(0).text().split(" ");
            final int year = Integer.parseInt(conferenceValues[conferenceValues.length-1]);
            final String location = tds.get(1).text();
            final String dateValue = LocalServer.removeWikipediaReferences(tds.get(2).text());
            final List<EventDate> dates = parseDatesFrom(year, dateValue);
            iterateDays(dates, events, title, description, defaultImageURL, location, externalLinks);
        }
        return events;
    }
    private List<PresentationEvent> refreshGoldenGlobeAwards() {
        final String title = "Golden Globe Awards";
        final WikipediaDocument doc = new WikipediaDocument(url);
        final WikipediaDocument page = new WikipediaDocument("https://en.wikipedia.org/wiki/Golden_Globe_Awards");
        final String description = page.getDescription(), location = "California, United States";
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
                final PresentationEvent event = new PresentationEvent(date, title, description, defaultImageURL, location, null, externalLinks);
                events.add(event);
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshGoogleIO() {
        final String title = "Google I/O", location = "California, United States";
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
                iterateDays(dates, events, title, description, defaultImageURL, location, externalLinks);
            }
        }
        return events;
    }
    private List<PresentationEvent> refreshMetGala() {
        final String title = "Met Gala", location = "Fifth Avenue, Manhattan, New York City, New York, United States";
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
                    final PresentationEvent event = new PresentationEvent(date, title, description, defaultImageURL, location, null, externalLinks);
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
                    final PresentationEvent event = new PresentationEvent(date, title, description, defaultImageURL, location, null, externalLinks);
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
            addEventsFromTwitchCon(events, tables.get(i), description, defaultImageURL, externalLinks, locationSuffix);
        }
        return events;
    }
    private void addEventsFromTwitchCon(List<PresentationEvent> events, Element table, String description, String imageURL, EventSources externalLinks, String locationSuffix) {
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
                iterateDays(dates, events, title, description, imageURL, location, externalLinks);
            }
        }
    }
}
