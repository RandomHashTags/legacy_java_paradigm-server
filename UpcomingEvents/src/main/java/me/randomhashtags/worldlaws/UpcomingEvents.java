package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.observances.Holidays;
import me.randomhashtags.worldlaws.politics.Elections;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.entertainment.Movies;
import me.randomhashtags.worldlaws.upcoming.entertainment.MusicAlbums;
import me.randomhashtags.worldlaws.upcoming.entertainment.VideoGames;
import me.randomhashtags.worldlaws.upcoming.space.RocketLaunches;
import me.randomhashtags.worldlaws.upcoming.sports.UFC;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class UpcomingEvents implements WLServer {

    private static final HashSet<UpcomingEventController> CONTROLLERS = new HashSet<>() {{
        addAll(Arrays.asList(
                //MLB.INSTANCE,
                Movies.INSTANCE,
                //NASANeo.INSTANCE,
                //NFL.INSTANCE, // problem
                //MusicAlbums.INSTANCE,
                RocketLaunches.INSTANCE,
                //SpaceX.INSTANCE,
                UFC.INSTANCE,
                VideoGames.INSTANCE
        ));
    }};

    public static void main(String[] args) {
        new UpcomingEvents();
    }

    private final HashMap<String, String> dates;

    UpcomingEvents() {
        dates = new HashMap<>();
        test();
        //load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.UPCOMING_EVENTS;
    }

    private void test() {
        MusicAlbums.INSTANCE.getEventsFromDate(new EventDate(Month.JULY, 30, 2021), new CompletionHandler() {
            @Override
            public void handleString(String string) {
                WLLogger.log(Level.INFO, "UpcomingEvents;test;string=" + string);
            }
        });
    }

    private UpcomingEventController valueOfEventType(String eventType) {
        final Optional<UpcomingEventController> test = CONTROLLERS.stream().filter(controller -> {
            return eventType.equalsIgnoreCase(controller.getType().name());
        }).findFirst();
        return test.orElse(null);
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "date":
                getEventsFromStringDate(values[1], handler);
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
                    controller.getUpcomingEvent(values[1], handler);
                } else {
                    WLLogger.log(Level.ERROR, "UpcomingEvent - failed to getResponse for key \"" + key + "\"!");
                }
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "holidays/near",
                "weekly_events",
                "recent_events",
                "elections"
        };
    }

    private void setupAutoUpdates() {
        final long interval = WLUtilities.UPCOMING_EVENTS_UPDATE_INTERVAL;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dates.clear();
                refreshHome(null, null);
            }
        }, interval, interval);
    }
    private void refreshEventsFromThisWeek(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final LocalDate now = WLUtilities.getNowUTC();
        final int targetYear = now.getYear();
        final long epochDay = now.toEpochDay();
        final Folder folder = Folder.UPCOMING_EVENTS;
        folder.setCustomFolderName(folder.getFolderName(false).replace("%year%", Integer.toString(targetYear)).replace("%day%", Long.toString(epochDay)));
        getJSONObject(folder, "weekly", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final HashSet<String> dates = new HashSet<>();
                for(int i = -1; i < 7; i++) {
                    dates.add(getEventStringForDate(now.plusDays(i)));
                }

                final int max = dates.size();
                final HashSet<String> eventValues = new HashSet<>();
                final AtomicInteger completed = new AtomicInteger(0);
                for(String eventDate : dates) {
                    getEventsFromStringDate(eventDate, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            if(string != null) {
                                final String eventDateValue = "\"" + eventDate + "\":" + string;
                                eventValues.add(eventDateValue);
                            }
                            if(completed.addAndGet(1) == max) {
                                if(handler != null) {
                                    final StringBuilder builder = new StringBuilder("{");
                                    boolean isFirst = true;
                                    for(String eventValue : eventValues) {
                                        builder.append(isFirst ? "" : ",").append(eventValue);
                                        isFirst = false;
                                    }
                                    builder.append("}");
                                    handler.handleString(builder.toString());
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                folder.resetCustomFolderName();
                WLLogger.log(Level.INFO, "UpcomingEvent - refreshed events from this week (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleString(json.toString());
            }
        });
    }
    private String getEventStringForDate(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private void getEventsFromStringDate(String targetDate, CompletionHandler handler) {
        if(dates.containsKey(targetDate)) {
            handler.handleString(dates.get(targetDate));
        } else {
            final String[] valueDates = targetDate.split("-");
            final Month month = Month.of(Integer.parseInt(valueDates[0]));
            final int day = Integer.parseInt(valueDates[2]), year = Integer.parseInt(valueDates[1]);
            getEventsFromDate(new EventDate(month, day, year), new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    dates.put(targetDate, string);
                    handler.handleString(string);
                }
            });
        }
    }
    private void getEventsFromDate(EventDate date, CompletionHandler handler) {
        final int max = CONTROLLERS.size();
        final List<String> values = new ArrayList<>();
        final AtomicInteger completed = new AtomicInteger(0);
        CONTROLLERS.parallelStream().forEach(controller -> {
            controller.getEventsFromDate(date, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        final String value = "\"" + controller.getType().name().toLowerCase() + "\":" + string;
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
            });
        });
    }
}
