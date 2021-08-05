package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum RocketLaunches implements UpcomingEventController {
    INSTANCE;

    private HashMap<String, String> upcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return null;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        refresh(handler);
    }

    private void refresh(CompletionHandler handler) {
        upcomingEvents = new HashMap<>();

        requestJSONObject("https://ll.thespacedevs.com/2.0.0/launch/upcoming/?format=json&limit=50&mode=detailed&offset=0", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final LocalDate endingDate = LocalDate.now();
                    final JSONArray launches = json.getJSONArray("results");
                    final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
                    final AtomicInteger completed = new AtomicInteger(0);
                    final int max = launches.length();
                    StreamSupport.stream(launches.spliterator(), true).forEach(obj -> {
                        final JSONObject launchJSON = (JSONObject) obj;
                        final JSONObject rocketConfigurationJSON = launchJSON.getJSONObject("rocket").getJSONObject("configuration"), padJSON = launchJSON.getJSONObject("pad");
                        final JSONObject serviceProvider = launchJSON.getJSONObject("launch_service_provider");
                        final String windowStart = launchJSON.getString("window_start"), windowEnd = launchJSON.getString("window_end");
                        final int probability = launchJSON.get("probability") instanceof Integer ? launchJSON.getInt("probability") : -1;
                        final boolean exactDay = launchJSON.getBoolean("tbddate"), exactTime = launchJSON.getBoolean("tbdtime");
                        final String name = launchJSON.getString("name");
                        final String status = launchJSON.getJSONObject("status").getString("name").toUpperCase();
                        final String location = padJSON.getJSONObject("location").getString("name");
                        final String rocketImageURL = rocketConfigurationJSON.get("image_url") instanceof String ? rocketConfigurationJSON.getString("image_url") : null;

                        final JSONObject missionJSON = launchJSON.get("mission") instanceof JSONObject ? launchJSON.getJSONObject("mission") : null;
                        RocketLaunchMission mission = null;
                        if(missionJSON != null) {
                            final String missionName = missionJSON.getString("name"), description = missionJSON.getString("description"), missionType = missionJSON.getString("type");
                            mission = new RocketLaunchMission(missionName, description, missionType);
                        }

                        final EventDate date = new EventDate(windowStart);
                        final String dateString = getEventDateString(date), id = getEventDateIdentifier(dateString, name);
                        final RocketLaunch launch = new RocketLaunch(name, status, location, exactDay, exactTime, probability, rocketImageURL, mission, windowStart, windowEnd, sources);
                        final String string = launch.toJSON();
                        if(date.getLocalDate().isEqual(endingDate)) {
                            saveUpcomingEventToJSON(id, string);
                        }
                        upcomingEvents.put(id, string);

                        if(completed.addAndGet(1) == max) {
                            handler.handleString(null);
                        }
                    });
                } else {
                    handler.handleString(null);
                }
            }
        });
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
    }
}
