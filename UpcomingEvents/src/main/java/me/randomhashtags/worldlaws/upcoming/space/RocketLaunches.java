package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.RocketLaunchEvent;
import me.randomhashtags.worldlaws.upcoming.events.RocketLaunchMission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;

public final class RocketLaunches extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public void load() {
        final UpcomingEventType eventType = getType();
        final JSONObject json = requestJSONObject("https://ll.thespacedevs.com/2.0.0/launch/upcoming/?format=json&limit=50&mode=detailed&offset=0", RequestMethod.GET);
        if(json != null) {
            final LocalDate today = LocalDate.now();
            final JSONArray launches = json.getJSONArray("results");
            final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
            new ParallelStream<JSONObject>().stream(launches.spliterator(), launchJSON -> {
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
                final RocketLaunchEvent launch = new RocketLaunchEvent(name, status, location, exactDay, exactTime, probability, rocketImageURL, mission, windowStart, windowEnd, sources);
                final String string = launch.toString();
                if(date.getLocalDate().isEqual(today)) {
                    saveUpcomingEventToJSON(id, string);
                }
                putLoadedPreUpcomingEvent(id, launch.toPreUpcomingEventJSON(eventType, id, location));
                putUpcomingEvent(id, string);
            });
        }
    }
}
