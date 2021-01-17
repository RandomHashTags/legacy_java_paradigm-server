package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.PreUpcomingEvent;
import me.randomhashtags.worldlaws.event.USAEventController;
import me.randomhashtags.worldlaws.event.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public enum SpaceX implements USAEventController {
    INSTANCE;

    private String json;
    private boolean isFirst;
    private HashMap<String, String> launchpads, preEvents, events;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_X;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            final long EVERY_HOUR = 1000*60*60;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    refreshUpcomingLaunches(null);
                }
            }, EVERY_HOUR, EVERY_HOUR);
            refreshUpcomingLaunches(handler);
        }
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return preEvents;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return events;
    }

    private void refreshUpcomingLaunches(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        preEvents = new HashMap<>();
        events = new HashMap<>();
        final String url = "https://api.spacexdata.com/v4/launches/upcoming";
        requestJSONArray(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                final UpcomingEventType type = getType();
                final StringBuilder builder = new StringBuilder("[");
                final EventSource source = new EventSource("SpaceX GitHub", "https://github.com/r-spacex/SpaceX-API");
                final EventSources sources = new EventSources(source);
                final Object last = array.get(array.length()-1);
                isFirst = true;
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String title = json.getString("name");
                    final String description = json.get("details") instanceof String ? json.getString("details") : "null";
                    final long dateUnix = json.getLong("date_unix");
                    final EventDate date = new EventDate(dateUnix*1000);
                    final String launchpadID = json.getString("launchpad");
                    getLaunchpad(launchpadID, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final JSONObject launchpadJSON = new JSONObject(object.toString());
                            final String location = launchpadJSON.getString("locality") + ", " + launchpadJSON.getString("region");
                            final SpaceEvent event = new SpaceEvent(type, date, title, description, location, sources);
                            final String identifier = getEventIdentifier(date, title);
                            events.put(identifier, event.toJSON());

                            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(type, date, title, location, event.getImageURL());
                            final String string = preUpcomingEvent.toString();
                            preEvents.put(identifier, string);
                            builder.append(isFirst ? "" : ",").append(preUpcomingEvent.toString());
                            isFirst = false;

                            if(obj.equals(last)) {
                                builder.append("]");
                                final String jsonString = builder.toString();
                                SpaceX.this.json = jsonString;
                                WLLogger.log(Level.INFO, "SpaceX - updated upcoming launches (took " + (System.currentTimeMillis()-started) + "ms)");
                                if(handler != null) {
                                    handler.handle(jsonString);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    private void getLaunchpad(String id, CompletionHandler handler) {
        if(launchpads == null) {
            launchpads = new HashMap<>();
        }
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
}
