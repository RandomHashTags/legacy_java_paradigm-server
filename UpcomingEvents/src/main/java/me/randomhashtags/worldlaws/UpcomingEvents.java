package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.observances.Holidays;
import me.randomhashtags.worldlaws.politics.Elections;
import me.randomhashtags.worldlaws.recent.ScienceYearReview;
import me.randomhashtags.worldlaws.recent.VideoGameUpdates;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeUpcomingEvents;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.education.WordOfTheDay;
import me.randomhashtags.worldlaws.upcoming.entertainment.*;
import me.randomhashtags.worldlaws.upcoming.entertainment.movies.MovieProductionCompanies;
import me.randomhashtags.worldlaws.upcoming.entertainment.movies.Movies;
import me.randomhashtags.worldlaws.upcoming.entertainment.music.MusicAlbums;
import me.randomhashtags.worldlaws.upcoming.entertainment.music.MusicSpotify;
import me.randomhashtags.worldlaws.upcoming.science.AstronomyPictureOfTheDay;
import me.randomhashtags.worldlaws.upcoming.space.RocketLaunches;
import me.randomhashtags.worldlaws.upcoming.space.SpaceEvents;
import me.randomhashtags.worldlaws.upcoming.sports.Championships;
import me.randomhashtags.worldlaws.upcoming.sports.MLB;
import me.randomhashtags.worldlaws.upcoming.sports.UFC;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public final class UpcomingEvents implements WLServer {
    public static final UpcomingEvents INSTANCE = new UpcomingEvents();
    private static final HashSet<UpcomingEventController> CONTROLLERS = new HashSet<>() {{
        addAll(Arrays.asList(
                new Championships(),
                new JokeOfTheDay(),
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
                new WordOfTheDay()
        ));
    }};

    public static void main(String[] args) {
        INSTANCE.initialize();
    }

    private String weeklyEvents;
    private final HashMap<ServerRequestTypeUpcomingEvents, String> typeJSONs;

    UpcomingEvents() {
        typeJSONs = new HashMap<>();
    }

    private void initialize() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.UPCOMING_EVENTS;
    }

    private void test() {
        final long started = System.currentTimeMillis();
        final Presentations presentations = new Presentations();
        presentations.refresh();
        final String string = presentations.getEventsFromDates(getWeeklyEventDateStrings(LocalDate.now()));
        WLLogger.logInfo("UpcomingEvents;test;string=" + string + ";took " + WLUtilities.getElapsedTime(started));
    }

    private UpcomingEventController valueOfEventType(String eventType) {
        final Optional<UpcomingEventController> test = CONTROLLERS.stream().filter(controller -> eventType.equalsIgnoreCase(controller.getType().name())).findFirst();
        return test.orElse(null);
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeUpcomingEvents type = (ServerRequestTypeUpcomingEvents) request.getType();
        final String target = request.getTarget();
        final String[] values = target != null ? target.split("/") : null;
        if(type == null) {
            final String key = values[0];
            final UpcomingEventController controller = valueOfEventType(key);
            if(controller != null) {
                return controller.getResponse(target.substring(key.length()+1));
            } else {
                WLLogger.logError(this, "getServerResponse - failed to get controller using key \"" + key + "\" with target \"" + target + "\"!");
            }
            return null;
        }
        switch (type) {
            case ELECTIONS:
                return Elections.INSTANCE.refresh();
            case EVENT_TYPES:
            case MOVIE_PRODUCTION_COMPANIES:
            case PRESENTATIONS:
            case VIDEO_GAMES:
                return getTypeJSON(type);
            case HAPPENING_NOW:
                //StreamingEvents.TWITCH.getUpcomingEvents(handler);
                return null;
            case HOLIDAYS:
                return Holidays.INSTANCE.getResponse(target);
            case MUSIC_ARTISTS:
                return null;
            case RECENT_EVENTS:
                return RecentEvents.INSTANCE.refresh(7);
            case WEEKLY_EVENTS:
                return getWeeklyEvents();
            default:
                WLLogger.logError(this, "getServerResponse - failed to get response using type \"" + type.name() + "\" with target \"" + target + "\"!");
                return null;
        }
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                //new ServerRequest(ServerRequestTypeUpcomingEvents.ELECTIONS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.EVENT_TYPES),
                //new ServerRequest(ServerRequestTypeUpcomingEvents.HAPPENING_NOW),
                new ServerRequest(ServerRequestTypeUpcomingEvents.HOLIDAYS, "near"),
                new ServerRequest(ServerRequestTypeUpcomingEvents.MOVIE_PRODUCTION_COMPANIES),
                //new ServerRequest(ServerRequestTypeUpcomingEvents.MUSIC_ARTISTS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.PRESENTATIONS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.RECENT_EVENTS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.WEEKLY_EVENTS),
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        return 0;
    }

    private String getTypeJSON(ServerRequestTypeUpcomingEvents type) {
        if(!typeJSONs.containsKey(type)) {
            String string = null;
            switch (type) {
                case EVENT_TYPES:
                    string = UpcomingEventType.getTypesJSON();
                    break;
                case PRESENTATIONS:
                    string = PresentationType.getTypesJSON();
                    break;
                case MOVIE_PRODUCTION_COMPANIES:
                    string = MovieProductionCompanies.getTypesJSON();
                    break;
                case VIDEO_GAMES:
                    string = VideoGameUpdates.getTypesJSON();
                    break;
                default:
                    break;
            }
            if(string != null) {
                typeJSONs.put(type, string);
            }
        }
        return typeJSONs.get(type);
    }
    private HashSet<String> getWeeklyEventDateStrings(LocalDate now) {
        final HashSet<String> dates = new HashSet<>();
        for(int i = -1; i < 7; i++) {
            dates.add(getEventStringForDate(now.plusDays(i)));
        }
        return dates;
    }

    private String getWeeklyEvents() {
        if(weeklyEvents == null) {
            refreshEventsFromThisWeek(true);
        }
        return weeklyEvents;
    }
    private void refreshData() {
        Holidays.INSTANCE.refreshNearHolidays();
        refreshEventsFromThisWeek(false);
        final String serverName = "UpcomingEvents";
        autoRefreshHome(serverName, System.currentTimeMillis(), serverName, TargetServer.UPCOMING_EVENTS, APIVersion.getLatest());
    }
    private void refreshEventsFromThisWeek(boolean registerAutoUpdates) {
        final long started = System.currentTimeMillis();
        if(registerAutoUpdates) {
            final LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(10)
                    .withNano(0);
            registerFixedTimer(tomorrow, UpdateIntervals.UpcomingEvents.WEEKLY_EVENTS, this::refreshData);
        }
        final LocalDate now = LocalDate.now();
        final HashSet<String> dates = getWeeklyEventDateStrings(now);
        new CompletableFutures<UpcomingEventController>().stream(CONTROLLERS, UpcomingEventController::refresh);
        final JSONObject json = getEventsFromDates(dates);
        if(json != null) {
            weeklyEvents = json.toString();
        }
        WLLogger.logInfo("UpcomingEvents - " + (registerAutoUpdates ? "" : "auto-") + "refreshed events from this week (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private String getEventStringForDate(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private JSONObject getEventsFromDates(HashSet<String> dateStrings) {
        final HashSet<String> values = new HashSet<>();
        new CompletableFutures<UpcomingEventController>().stream(CONTROLLERS, controller -> {
            final String string = controller.getEventsFromDates(dateStrings);
            if(string != null) {
                final String key = controller.getType().name().toLowerCase();
                final String value = "\"" + key + "\":" + string;
                values.add(value);
            }
        });

        String stringValue = null;
        if(!values.isEmpty()) {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(String value : values) {
                builder.append(isFirst ? "" : ",").append(value);
                isFirst = false;
            }
            stringValue = builder.append("}").toString();
        }
        if(stringValue != null) {
            try {
                return new JSONObject(stringValue);
            } catch (Exception e) {
                final String stackTrace = WLUtilities.getThrowableStackTrace(e);
                WLUtilities.saveLoggedError("UpcomingEvents", "failed parsing string to JSONObject\n\n" + stringValue + "\n\n" + stackTrace);
            }
        }
        return null;
    }
}
