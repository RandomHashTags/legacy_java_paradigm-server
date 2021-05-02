package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum RocketLaunches implements EventController {
    INSTANCE;

    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    @Override
    public HashMap<String, NewPreUpcomingEvent> getPreEventURLs() {
        return null;
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
        upcomingEvents = new HashMap<>();
        preUpcomingEvents = new HashMap<>();

        requestJSONObject("https://ll.thespacedevs.com/2.0.0/launch/upcoming/?format=json&limit=50&mode=detailed&offset=0", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
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
                        final String dateString = date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
                        final String id = dateString + "." + name.replace(" ", "").replace("/", "-");
                        final RocketLaunch launch = new RocketLaunch(name, status, location, exactDay, exactTime, probability, rocketImageURL, mission, windowStart, windowEnd, sources);
                        final String string = launch.toJSON();
                        upcomingEvents.put(id, string);
                        final String preUpcomingEventString = new PreUpcomingEvent(id, name, location, rocketImageURL).toString();
                        preUpcomingEvents.put(id, preUpcomingEventString);
                    }
                    handler.handle(null);
                }
            }
        });
    }

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        final String value = upcomingEvents.getOrDefault(id, "{}");
        handler.handle(value);
    }
}
