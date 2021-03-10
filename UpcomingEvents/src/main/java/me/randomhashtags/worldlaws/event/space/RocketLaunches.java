package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.UpcomingEventType;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum RocketLaunches implements EventController {
    INSTANCE;

    private String jsonPreUpcomingEvents;
    private HashMap<String, String> preEvents, events;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        preEvents = new HashMap<>();
        events = new HashMap<>();
        requestJSONObject("https://ll.thespacedevs.com/2.0.0/launch/upcoming/?format=json&limit=50&mode=detailed&offset=0", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final StringBuilder builder = new StringBuilder("[");
                    boolean isFirst = true;

                    final JSONArray launches = json.getJSONArray("results");
                    final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
                    for(Object obj : launches) {
                        final JSONObject launchJSON = (JSONObject) obj;
                        final JSONObject rocketJSON = launchJSON.getJSONObject("rocket").getJSONObject("configuration"), padJSON = launchJSON.getJSONObject("pad");
                        final JSONObject serviceProvider = launchJSON.getJSONObject("launch_service_provider");
                        final String windowStart = launchJSON.getString("window_start"), windowEnd = launchJSON.getString("window_end");
                        final int probability = launchJSON.get("probability") instanceof Integer ? launchJSON.getInt("probability") : -1;
                        final boolean exactDay = launchJSON.getBoolean("tbddate"), exactTime = launchJSON.getBoolean("tbdtime");
                        final String name = launchJSON.getString("name");
                        final String status = launchJSON.getJSONObject("status").getString("name").toUpperCase();
                        final String location = padJSON.getJSONObject("location").getString("name");
                        final String rocketImageURL = rocketJSON.get("image_url") instanceof String ? rocketJSON.getString("image_url") : null;

                        final JSONObject missionJSON = launchJSON.get("mission") instanceof JSONObject ? launchJSON.getJSONObject("mission") : null;
                        RocketLaunchMission mission = null;
                        if(missionJSON != null) {
                            final String missionName = missionJSON.getString("name"), description = missionJSON.getString("description"), missionType = missionJSON.getString("type");
                            mission = new RocketLaunchMission(missionName, description, missionType);
                        }

                        final EventDate date = new EventDate(windowStart);
                        final RocketLaunch launch = new RocketLaunch(date, name, status, location, exactDay, exactTime, probability, rocketImageURL, mission, windowStart, windowEnd, sources);
                        final String identifier = getEventIdentifier(date, name);
                        events.put(identifier, launch.toJSON());

                        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(name, location, rocketImageURL);
                        final String string = preUpcomingEvent.toString();
                        preEvents.put(identifier, string);
                        builder.append(isFirst ? "" : ",").append(string);
                        isFirst = false;
                    }
                    builder.append("]");
                    final String string = builder.toString();
                    jsonPreUpcomingEvents = string;
                    WLLogger.log(Level.INFO, "RocketLaunches - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(string);
                }
            }
        });
    }

    @Override
    public String getCache() {
        return jsonPreUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return preEvents;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return events;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }
}
