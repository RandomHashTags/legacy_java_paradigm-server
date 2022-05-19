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
                //new LunarEclipses(),
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

    private static final HashMap<ServerRequestTypeUpcomingEvents, JSONObjectTranslatable> CACHE_JSONS = new HashMap<>();

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
            case WEEKLY_EVENTS:
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
            return getUpcomingEvent(path);
        };
    }
    public static JSONObjectTranslatable getUpcomingEvent(String path) {
        final String[] values = path.split("/");
        final String key = values[1];
        final UpcomingEventController controller = valueOfEventType(key);
        if(controller != null) {
            return controller.getUpcomingEvent(path.substring(values[0].length() + key.length() + 2));
        } else {
            WLLogger.logError("ServerRequestTypeUpcomingEvents", "getUpcomingEvent - failed to get controller using key \"" + key + "\" with path \"" + path + "\"!");
        }
        return null;
    }

    JSONObjectTranslatable getTypeJSON() {
        if(!CACHE_JSONS.containsKey(this)) {
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

                case WEEKLY_EVENTS:
                    json = refreshEventsFromThisWeek(true);
                    break;

                default:
                    break;
            }
            if(json != null) {
                CACHE_JSONS.put(this, json);
            }
        }
        return CACHE_JSONS.get(this);
    }

    private HashSet<String> getWeeklyEventDateStrings(LocalDate now) {
        final HashSet<String> dates = new HashSet<>();
        for(int i = -1; i < 7; i++) {
            dates.add(EventDate.getDateString(now.plusDays(i)));
        }
        return dates;
    }

    private void refreshData() {
        Holidays.INSTANCE.refreshNearHolidays();
        final JSONObjectTranslatable json = refreshEventsFromThisWeek(false);
        CACHE_JSONS.put(ServerRequestTypeUpcomingEvents.WEEKLY_EVENTS, json);
        final String serverName = "UpcomingEvents";
        UpcomingEvents.INSTANCE.autoRefreshHome(serverName, System.currentTimeMillis(), serverName, APIVersion.getLatest());
    }
    private JSONObjectTranslatable refreshEventsFromThisWeek(boolean registerAutoUpdates) {
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
        WLLogger.logInfo("UpcomingEvents - " + (registerAutoUpdates ? "" : "auto-") + "refreshed events from this week (took " + WLUtilities.getElapsedTime(started) + ")");
        return json;
    }
    private JSONObjectTranslatable getEventsFromDates(HashSet<String> dateStrings) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        new CompletableFutures<UpcomingEventController>().stream(CONTROLLERS, controller -> {
            final String typeIdentifier = controller.getType().name().toLowerCase();
            final JSONObjectTranslatable controllerJSON = new JSONObjectTranslatable("dates", "exactTimes");
            final ConcurrentHashMap<String, Collection<LoadedPreUpcomingEvent>> eventsFromDates = controller.getEventsFromDates(dateStrings);
            if(eventsFromDates != null) {
                final JSONObjectTranslatable datesJSON = new JSONObjectTranslatable();
                for(Map.Entry<String, Collection<LoadedPreUpcomingEvent>> map : eventsFromDates.entrySet()) {
                    final JSONObjectTranslatable dateStringJSON = new JSONObjectTranslatable();
                    final String dateString = map.getKey();
                    final Collection<LoadedPreUpcomingEvent> events = map.getValue();
                    for(LoadedPreUpcomingEvent event : events) {
                        final String id = event.getID();
                        dateStringJSON.put(id, event.getJSONObject());
                        dateStringJSON.addTranslatedKey(id);
                    }
                    datesJSON.put(dateString, dateStringJSON);
                    datesJSON.addTranslatedKey(dateString);
                }
                controllerJSON.put("dates", datesJSON);
            }

            final Collection<LoadedPreUpcomingEvent> exactTimeEvents = controller.getExactTimeEvents();
            if(exactTimeEvents != null) {
                final JSONObjectTranslatable exactTimesJSON = new JSONObjectTranslatable();
                for(LoadedPreUpcomingEvent event : exactTimeEvents) {
                    exactTimesJSON.put(event.getIdentifier(), event.getJSONObject());
                }
                controllerJSON.put("exactTimes", exactTimesJSON);
            }
            if(!controllerJSON.isEmpty()) {
                json.put(typeIdentifier, controllerJSON);
                json.addTranslatedKey(typeIdentifier);
            }
        });
        return json.isEmpty() ? null : json;
    }
}
