package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.iap.InAppPurchases;
import me.randomhashtags.worldlaws.observances.Holidays;
import me.randomhashtags.worldlaws.politics.Elections;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.entertainment.Movies;
import me.randomhashtags.worldlaws.upcoming.entertainment.MusicAlbums;
import me.randomhashtags.worldlaws.upcoming.entertainment.TVShows;
import me.randomhashtags.worldlaws.upcoming.entertainment.VideoGames;
import me.randomhashtags.worldlaws.upcoming.space.RocketLaunches;
import me.randomhashtags.worldlaws.upcoming.space.SpaceEvents;
import me.randomhashtags.worldlaws.upcoming.sports.Championships;
import me.randomhashtags.worldlaws.upcoming.sports.UFC;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class UpcomingEvents implements WLServer {
    public static final UpcomingEvents INSTANCE = new UpcomingEvents();
    private static final HashSet<UpcomingEventController> CONTROLLERS = new HashSet<>() {{
        addAll(Arrays.asList(
                Championships.INSTANCE,
                //MLB.INSTANCE,
                Movies.INSTANCE,
                //NASANeo.INSTANCE,
                //NFL.INSTANCE, // problem
                MusicAlbums.INSTANCE,
                RocketLaunches.INSTANCE,
                SpaceEvents.INSTANCE,
                //SpaceX.INSTANCE,
                TVShows.INSTANCE,
                UFC.INSTANCE,
                VideoGames.INSTANCE
        ));
    }};

    public static void main(String[] args) {
        INSTANCE.initialize();
    }

    private String typesJSON;

    private void initialize() {
        test();
        //load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.UPCOMING_EVENTS;
    }

    private void test() {
        final String ids = InAppPurchases.getProductIDs(APIVersion.v1);
        WLLogger.log(Level.INFO, "UpcomingEvents;test;ids=" + ids);
    }

    private UpcomingEventController valueOfEventType(String eventType) {
        final Optional<UpcomingEventController> test = CONTROLLERS.stream().filter(controller -> eventType.equalsIgnoreCase(controller.getType().name())).findFirst();
        return test.orElse(null);
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "event_types":
                handler.handleString(getEventTypesJSON());
                break;
            case "happeningnow":
                //StreamingEvents.TWITCH.getUpcomingEvents(handler);
                break;
            case "holidays":
                final String value = target.substring(key.length()+1);
                Holidays.INSTANCE.getResponse(value, handler);
                break;

            case "weekly_events":
                refreshEventsFromThisWeek(handler);
                break;
            case "recent_events":
                RecentEvents.INSTANCE.refresh(handler);
                break;
            case "elections":
                Elections.INSTANCE.refresh(handler);
                break;

            default:
                final UpcomingEventController controller = valueOfEventType(key);
                if(controller != null) {
                    controller.getResponse(target.substring(key.length()+1), handler);
                } else {
                    WLLogger.log(Level.WARN, "UpcomingEvent - failed to getResponse for key \"" + key + "\", target=\"" + target + "\"!");
                    handler.handleString(null);
                }
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "event_types",
                "holidays/near",
                "weekly_events",
                "recent_events",
                "elections"
        };
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return new AutoUpdateSettings(WLUtilities.UPCOMING_EVENTS_UPDATE_INTERVAL, null);
    }

    private String getEventTypesJSON() {
        if(typesJSON == null) {
            typesJSON = UpcomingEventType.getTypesJSON();
        }
        return typesJSON;
    }

    private void refreshEventsFromThisWeek(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final LocalDate now = WLUtilities.getNowUTC();
        final int targetYear = now.getYear(), day = now.getDayOfYear();
        final Folder folder = Folder.UPCOMING_EVENTS_YEAR_DAY;
        final String fileName = "weekly";
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%year%", Integer.toString(targetYear)).replace("%day%", Integer.toString(day)));
        getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final HashSet<String> dates = new HashSet<>();
                for(int i = -1; i < 7; i++) {
                    dates.add(getEventStringForDate(now.plusDays(i)));
                }

                getEventsFromDates(dates, handler);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                WLLogger.log(Level.INFO, "UpcomingEvent - refreshed events from this week (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleString(json.toString());
            }
        });
    }
    private String getEventStringForDate(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private void getEventsFromDates(HashSet<String> dateStrings, CompletionHandler handler) {
        final int max = CONTROLLERS.size();
        final List<String> values = new ArrayList<>();
        final AtomicInteger completed = new AtomicInteger(0);
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleStringValue(String key, String string) {
                if(string != null) {
                    final String value = "\"" + key + "\":" + string;
                    values.add(value);
                }
                if(completed.addAndGet(1) == max) {
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
                    handler.handleString(stringValue);
                }
            }
        };
        CONTROLLERS.parallelStream().forEach(controller -> controller.getEventsFromDates(dateStrings, completionHandler));
    }
}
