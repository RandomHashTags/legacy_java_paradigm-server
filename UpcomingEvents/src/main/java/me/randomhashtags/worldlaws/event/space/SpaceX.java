package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.event.USAEventController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public enum SpaceX implements USAEventController {
    INSTANCE;

    private Timer autoUpdateTimer;
    private HashMap<String, String> launchpads;

    private HashMap<String, NewPreUpcomingEvent> preEventURLS;
    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_X;
    }

    @Override
    public HashMap<String, NewPreUpcomingEvent> getPreEventURLs() {
        return preEventURLS;
    }

    @Override
    public HashMap<String, String> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preEventURLS = new HashMap<>();
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();
        launchpads = new HashMap<>();

        if(autoUpdateTimer == null) {
            final long EVERY_HOUR = 1000*60*60;
            autoUpdateTimer = new Timer();
            autoUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    refreshUpcomingLaunches(null);
                }
            }, EVERY_HOUR, EVERY_HOUR);
        }
        refreshUpcomingLaunches(handler);
    }

    private void refreshUpcomingLaunches(CompletionHandler handler) {
        final String url = "https://api.spacexdata.com/v4/launches/upcoming";
        requestJSONArray(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String title = json.getString("name");

                    final String description = json.get("details") instanceof String ? json.getString("details") : "null";
                    final long dateUnix = json.getLong("date_unix");
                    final EventDate date = new EventDate(dateUnix*1000);

                    final String dateString =  date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
                    final String id = dateString + "." + title.replace(" ", "");
                    final String launchpadID = json.getString("launchpad");
                    final NewPreUpcomingEvent preUpcomingEvent = new NewPreUpcomingEvent(id, title, launchpadID, description);
                    preEventURLS.put(id, preUpcomingEvent);
                }
                handler.handle(null);
            }
        });
    }
    private void getLaunchpad(String id, CompletionHandler handler) {
        if(launchpads.containsKey(id)) {
            handler.handle(launchpads.get(id));
        } else {
            final String launchpadURL = "https://api.spacexdata.com/v4/launchpads/" + id;
            requestJSONObject(launchpadURL, RequestMethod.GET, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject object) {
                    final String string = object.toString();
                    launchpads.put(id, string);
                    handler.handle(string);
                }
            });
        }
    }

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        if(upcomingEvents.containsKey(id)) {
            handler.handle(upcomingEvents.get(id));
        } else {
            final NewPreUpcomingEvent preUpcomingEvent = preEventURLS.get(id);
            final String launchpadID = preUpcomingEvent.getURL(), description = preUpcomingEvent.getTag();
            final String title = preUpcomingEvent.getTitle();
            getLaunchpad(launchpadID, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final EventSource source = new EventSource("SpaceX GitHub", "https://github.com/r-spacex/SpaceX-API");
                    final EventSources sources = new EventSources(source);

                    final JSONObject launchpadJSON = new JSONObject(object.toString());
                    final String location = launchpadJSON.getString("locality") + ", " + launchpadJSON.getString("region");
                    final SpaceEvent event = new SpaceEvent(title, description, location, sources);
                    final String string = event.toString();
                    upcomingEvents.put(id, string);
                    handler.handle(string);
                }
            });
        }
    }
}
