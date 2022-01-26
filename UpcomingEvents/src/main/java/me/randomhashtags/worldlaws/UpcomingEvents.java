package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.observances.Holidays;
import me.randomhashtags.worldlaws.politics.Elections;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.education.WordOfTheDay;
import me.randomhashtags.worldlaws.upcoming.entertainment.*;
import me.randomhashtags.worldlaws.upcoming.entertainment.movies.Movies;
import me.randomhashtags.worldlaws.upcoming.entertainment.music.MusicAlbums;
import me.randomhashtags.worldlaws.upcoming.science.AstronomyPictureOfTheDay;
import me.randomhashtags.worldlaws.upcoming.space.RocketLaunches;
import me.randomhashtags.worldlaws.upcoming.space.SpaceEvents;
import me.randomhashtags.worldlaws.upcoming.sports.Championships;
import me.randomhashtags.worldlaws.upcoming.sports.MLB;
import me.randomhashtags.worldlaws.upcoming.sports.UFC;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
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
                //NASANeo.INSTANCE,
                //NFL.INSTANCE, // problem
                new ProfessionalWrestling(),
                new RocketLaunches(),
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

    private String typesJSON;

    private void initialize() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.UPCOMING_EVENTS;
    }

    private void test() {
        final RecentEvents events = RecentEvents.INSTANCE;
        events.refresh(60);
        try {
            Thread.sleep(1000*5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        events.refresh(7);
    }

    private UpcomingEventController valueOfEventType(String eventType) {
        final Optional<UpcomingEventController> test = CONTROLLERS.stream().filter(controller -> eventType.equalsIgnoreCase(controller.getType().name())).findFirst();
        return test.orElse(null);
    }

    @Override
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "event_types":
                return getEventTypesJSON();
            case "happeningnow":
                //StreamingEvents.TWITCH.getUpcomingEvents(handler);
                return null;
            case "holidays":
                final String value = target.substring(key.length()+1);
                return Holidays.INSTANCE.getResponse(value);

            case "weekly_events":
                return refreshEventsFromThisWeek().toString();
            case "music_artists":
                return null;
            //case "video_games":
            //    return VideoGameUpdates.INSTANCE.getAllVideoGames();

            case "recent_events":
                return RecentEvents.INSTANCE.refresh(7);
            case "elections":
                return Elections.INSTANCE.refresh();

            default:
                final UpcomingEventController controller = valueOfEventType(key);
                if(controller != null) {
                    return controller.getResponse(target.substring(key.length()+1));
                } else {
                    WLLogger.logError(this, "getServerResponse - failed to get controller using key \"" + key + "\" with target \"" + target + "\"!");
                }
                return null;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "event_types",
                "holidays/near",
                "weekly_events",
                "recent_events",
                //"elections"
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        registerFixedTimer(WLUtilities.UPCOMING_EVENTS_NEAR_HOLIDAYS_UPDATE_INTERVAL, Holidays.INSTANCE::refreshNearHolidays);
        return WLUtilities.UPCOMING_EVENTS_HOME_UPDATE_INTERVAL;
    }

    private String getEventTypesJSON() {
        if(typesJSON == null) {
            typesJSON = UpcomingEventType.getTypesJSON();
        }
        return typesJSON;
    }
    private HashSet<String> getWeeklyEventDateStrings(LocalDate now) {
        final HashSet<String> dates = new HashSet<>();
        for(int i = -1; i < 7; i++) {
            dates.add(getEventStringForDate(now.plusDays(i)));
        }
        return dates;
    }

    private JSONObject refreshEventsFromThisWeek() {
        final long started = System.currentTimeMillis();
        final LocalDate now = LocalDate.now();
        final int targetYear = now.getYear(), day = now.getDayOfMonth();
        final Month month = now.getMonth();
        final Folder folder = Folder.UPCOMING_EVENTS_YEAR_MONTH_DAY;
        final String fileName = "weekly";
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%year%", Integer.toString(targetYear)).replace("%month%", month.name()).replace("%day%", Integer.toString(day)));
        return getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                final HashSet<String> dates = getWeeklyEventDateStrings(now);
                ParallelStream.stream(CONTROLLERS, controllerObj -> {
                    final UpcomingEventController controller = (UpcomingEventController) controllerObj;
                    controller.refresh();
                });

                final JSONObject json = getEventsFromDates(dates);
                WLLogger.logInfo("UpcomingEvents - refreshed events from this week (took " + (System.currentTimeMillis()-started) + "ms)");
                return json;
            }
        });
    }
    private String getEventStringForDate(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private JSONObject getEventsFromDates(HashSet<String> dateStrings) {
        final HashSet<String> values = new HashSet<>();
        ParallelStream.stream(CONTROLLERS, controllerObj -> {
            final UpcomingEventController controller = (UpcomingEventController) controllerObj;
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
        return stringValue != null ? new JSONObject(stringValue) : null;
    }
}
