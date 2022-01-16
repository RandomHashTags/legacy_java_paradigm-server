package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public final class SpaceX extends USAUpcomingEventController {
    private Timer autoUpdateTimer;
    private HashMap<String, String> launchpads;

    @Override
    public UpcomingEventType getType() {
        return null; // UpcomingEventType.SPACE_X
    }

    @Override
    public void load() {
        launchpads = new HashMap<>();
        if(autoUpdateTimer == null) {
            final long EVERY_HOUR = 1000*60*60;
            autoUpdateTimer = new Timer();
            autoUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    refreshUpcomingLaunches();
                }
            }, EVERY_HOUR, EVERY_HOUR);
        }
        refreshUpcomingLaunches();
    }

    private void refreshUpcomingLaunches() {
        final String url = "https://api.spacexdata.com/v4/launches/upcoming";
        final JSONArray array = requestJSONArray(url, RequestMethod.GET);
        for(Object obj : array) {
            final JSONObject json = (JSONObject) obj;
            final String title = json.getString("name");

            final String description = json.get("details") instanceof String ? json.getString("details") : "null";
            final long dateUnix = json.getLong("date_unix");
            final EventDate date = new EventDate(dateUnix*1000);

            final String dateString =  date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
            final String id = dateString + "." + title.replace(" ", "");
            final String launchpadID = json.getString("launchpad");
            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, launchpadID, description);
            putPreUpcomingEvent(id, preUpcomingEvent);
        }
    }
    private void getLaunchpad(String id, CompletionHandler handler) {
        if(launchpads.containsKey(id)) {
            handler.handleString(launchpads.get(id));
        } else {
            final String launchpadURL = "https://api.spacexdata.com/v4/launchpads/" + id;
            final JSONObject json = requestJSONObject(launchpadURL, RequestMethod.GET);
            final String string = json.toString();
            launchpads.put(id, string);
            handler.handleString(string);
        }
    }

    @Override
    public String loadUpcomingEvent(String id) {
        return null;
        /*final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.get(id);
        final String launchpadID = preUpcomingEvent.getURL(), description = preUpcomingEvent.getTag();
        final String title = preUpcomingEvent.getTitle();
        getLaunchpad(launchpadID, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                final EventSource source = new EventSource("SpaceX GitHub", "https://github.com/r-spacex/SpaceX-API");
                final EventSources sources = new EventSources(source);

                final JSONObject launchpadJSON = new JSONObject(string);
                final String location = launchpadJSON.getString("locality") + ", " + launchpadJSON.getString("region");
                final SpaceEvent event = new SpaceEvent(title, description, location, sources);
                final String value = event.toString();
                upcomingEvents.put(id, value);
                handler.handleString(value);
            }
        });*/
    }
}
