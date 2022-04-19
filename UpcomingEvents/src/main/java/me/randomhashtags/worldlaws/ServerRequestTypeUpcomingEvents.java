package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.observances.Holidays;
import me.randomhashtags.worldlaws.politics.Elections;
import me.randomhashtags.worldlaws.recent.ScienceYearReview;
import me.randomhashtags.worldlaws.recent.VideoGameUpdates;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.education.WordOfTheDay;
import me.randomhashtags.worldlaws.upcoming.entertainment.*;
import me.randomhashtags.worldlaws.upcoming.entertainment.movies.MovieProductionCompanies;
import me.randomhashtags.worldlaws.upcoming.entertainment.movies.Movies;
import me.randomhashtags.worldlaws.upcoming.entertainment.music.MusicAlbums;
import me.randomhashtags.worldlaws.upcoming.entertainment.music.MusicSpotify;
import me.randomhashtags.worldlaws.upcoming.events.LoadedPreUpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.science.AstronomyPictureOfTheDay;
import me.randomhashtags.worldlaws.upcoming.science.WikipediaTodaysFeaturedPicture;
import me.randomhashtags.worldlaws.upcoming.space.RocketLaunches;
import me.randomhashtags.worldlaws.upcoming.space.SpaceEvents;
import me.randomhashtags.worldlaws.upcoming.sports.Championships;
import me.randomhashtags.worldlaws.upcoming.sports.MLB;
import me.randomhashtags.worldlaws.upcoming.sports.UFC;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum ServerRequestTypeUpcomingEvents implements ServerRequestType {
    ELECTIONS,
    EVENT_TYPES,
    HAPPENING_NOW,
    HOLIDAYS,
    MOVIE_PRODUCTION_COMPANIES,
    MUSIC_ARTISTS,
    PRESENTATIONS,
    RECENT_EVENTS,
    VIDEO_GAMES,
    WEEKLY_EVENTS,
    ;

    private static final HashSet<UpcomingEventController> CONTROLLERS = new HashSet<>() {{
        addAll(Arrays.asList(
                new Championships(),
                //new JokeOfTheDay(), // TODO: find better Joke of the Day service.
                new MLB(),
                new Movies(),
                new MusicAlbums(),
                new MusicSpotify(),
                //NASANeo.INSTANCE,
                //NFL.INSTANCE, // problem
                new Presentations(),
                new ProfessionalWrestling(),
                new RocketLaunches(),
                new ScienceYearReview(),
                new SpaceEvents(),
                //SpaceX.INSTANCE,
                new TVShows(),
                new UFC(),
                new VideoGames(),

                new Ticketmaster.Music(),

                new AstronomyPictureOfTheDay(),
                new WordOfTheDay(),
                new WikipediaTodaysFeaturedPicture()
        ));
    }};

    private JSONObjectTranslatable weeklyEvents;
    private static final HashMap<ServerRequestTypeUpcomingEvents, JSONObjectTranslatable> TYPE_JSONS = new HashMap<>();

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (this) {
            case ELECTIONS:
                return httpExchange -> {
                    return Elections.INSTANCE.refresh();
                };
            case EVENT_TYPES:
            case MOVIE_PRODUCTION_COMPANIES:
            case PRESENTATIONS:
            case VIDEO_GAMES:
                return httpExchange -> getTypeJSON();
            case HAPPENING_NOW:
                //StreamingEvents.TWITCH.getUpcomingEvents(handler);
                return null;
            case HOLIDAYS:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return Holidays.INSTANCE.getResponse(values);
                };
            case MUSIC_ARTISTS:
                return null;
            case RECENT_EVENTS:
                return httpExchange -> RecentEvents.INSTANCE.refresh(7);
            case WEEKLY_EVENTS:
                return httpExchange -> getWeeklyEvents();
            default:
                return httpExchange -> {
                    WLLogger.logError(this, "getServerResponse - failed to get response using type \"" + name() + "\" with target \"" + httpExchange.getPath() + "\"!");
                    return null;
                };
        }
    }

    private static UpcomingEventController valueOfEventType(String eventType) {
        final Optional<UpcomingEventController> test = CONTROLLERS.stream().filter(controller -> eventType.equalsIgnoreCase(controller.getType().name())).findFirst();
        return test.orElse(null);
    }

    public static WLHttpHandler getDefaultHandler() {
        return httpExchange -> {
            final String path = httpExchange.getPath();
            final String[] values = httpExchange.getPathValues();
            final String key = values[1];
            final UpcomingEventController controller = valueOfEventType(key);
            if(controller != null) {
                return controller.getUpcomingEvent(path.substring(values[0].length() + key.length() + 2));
            } else {
                WLLogger.logError("ServerRequestTypeUpcomingEvents", "getDefaultHandler - failed to get controller using key \"" + key + "\" with path \"" + path + "\"!");
            }
            return null;
        };
    }

    private JSONObjectTranslatable getTypeJSON() {
        if(!TYPE_JSONS.containsKey(this)) {
            JSONObjectTranslatable json = null;
            switch (this) {
                case EVENT_TYPES:
                    json = UpcomingEventType.getTypesJSON();
                    break;
                case PRESENTATIONS:
                    json = PresentationType.getTypesJSON();
                    break;
                case MOVIE_PRODUCTION_COMPANIES:
                    json = MovieProductionCompanies.getTypesJSON();
                    break;
                case VIDEO_GAMES:
                    json = VideoGameUpdates.getTypesJSON();
                    break;
                default:
                    break;
            }
            if(json != null) {
                TYPE_JSONS.put(this, json);
            }
        }
        return TYPE_JSONS.get(this);
    }

    private HashSet<String> getWeeklyEventDateStrings(LocalDate now) {
        final HashSet<String> dates = new HashSet<>();
        for(int i = -1; i < 7; i++) {
            dates.add(getEventStringForDate(now.plusDays(i)));
        }
        return dates;
    }

    private JSONObjectTranslatable getWeeklyEvents() {
        if(weeklyEvents == null) {
            refreshEventsFromThisWeek(true);
        }
        return weeklyEvents;
    }
    private void refreshData() {
        Holidays.INSTANCE.refreshNearHolidays();
        refreshEventsFromThisWeek(false);
        final String serverName = "UpcomingEvents";
        UpcomingEvents.INSTANCE.autoRefreshHome(serverName, System.currentTimeMillis(), serverName, APIVersion.getLatest());
    }
    private void refreshEventsFromThisWeek(boolean registerAutoUpdates) {
        final long started = System.currentTimeMillis();
        if(registerAutoUpdates) {
            final LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(10)
                    .withNano(0);
            UpcomingEvents.INSTANCE.registerFixedTimer(tomorrow, UpdateIntervals.UpcomingEvents.WEEKLY_EVENTS, this::refreshData);
        }
        final LocalDate now = LocalDate.now();
        final HashSet<String> dates = getWeeklyEventDateStrings(now);
        new CompletableFutures<UpcomingEventController>().stream(CONTROLLERS, UpcomingEventController::refresh);
        final JSONObjectTranslatable json = getEventsFromDates(dates);
        if(json != null) {
            weeklyEvents = json;
        }
        WLLogger.logInfo("UpcomingEvents - " + (registerAutoUpdates ? "" : "auto-") + "refreshed events from this week (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private String getEventStringForDate(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private JSONObjectTranslatable getEventsFromDates(HashSet<String> dateStrings) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        new CompletableFutures<UpcomingEventController>().stream(CONTROLLERS, controller -> {
            final ConcurrentHashMap<String, List<LoadedPreUpcomingEvent>> eventsFromDates = controller.getEventsFromDates(dateStrings);
            if(eventsFromDates != null) {
                final JSONObjectTranslatable controllerJSON = new JSONObjectTranslatable();
                for(Map.Entry<String, List<LoadedPreUpcomingEvent>> map : eventsFromDates.entrySet()) {
                    final JSONObjectTranslatable dateStringJSON = new JSONObjectTranslatable();
                    final String dateString = map.getKey();
                    final List<LoadedPreUpcomingEvent> events = map.getValue();
                    for(LoadedPreUpcomingEvent event : events) {
                        final String id = event.getID();
                        dateStringJSON.put(id, event.getJSONObject());
                        dateStringJSON.addTranslatedKey(id);
                    }
                    controllerJSON.put(dateString, dateStringJSON);
                    controllerJSON.addTranslatedKey(dateString);
                }
                final String key = controller.getType().name().toLowerCase();
                json.put(key, controllerJSON);
                json.addTranslatedKey(key);
            }
        });
        return json.isEmpty() ? null : json;
    }
}