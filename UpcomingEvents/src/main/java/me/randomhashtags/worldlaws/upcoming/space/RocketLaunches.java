package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.RocketLaunchEvent;
import me.randomhashtags.worldlaws.upcoming.events.RocketLaunchMission;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;

public final class RocketLaunches extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public void load() {
        final UpcomingEventType eventType = getType();
        final JSONObject json = requestJSONObject("https://ll.thespacedevs.com/2.0.0/launch/upcoming/?format=json&limit=50&mode=detailed&offset=0");
        if(json != null) {
            final JSONArray launches = json.getJSONArray("results");
            final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
            new CompletableFutures<JSONObject>().stream(launches, launchJSON -> {
                final JSONObject rocketConfigurationJSON = launchJSON.getJSONObject("rocket").getJSONObject("configuration"), padJSON = launchJSON.getJSONObject("pad");
                final JSONObject serviceProvider = launchJSON.getJSONObject("launch_service_provider");
                final String windowStartString = launchJSON.getString("window_start");
                final long windowStart = WLUtilities.parseDateFormatToMilliseconds(DateTimeFormatter.ISO_INSTANT, windowStartString);
                final String windowEndString = launchJSON.getString("window_end");
                final long windowEnd = WLUtilities.parseDateFormatToMilliseconds(DateTimeFormatter.ISO_INSTANT, windowEndString);

                final JSONArray videoURLsArray = launchJSON.optJSONArray("vidURLs");
                String videoURL = null;
                if(videoURLsArray != null) {
                    for(Object obj : videoURLsArray) {
                        final JSONObject videoJSON = (JSONObject) obj;
                        final String url = videoJSON.optString("url", null);
                        if(url != null) {
                            videoURL = url;
                            break;
                        }
                    }
                }

                final int probability = launchJSON.optInt("probability", -1);
                final boolean exactDay = launchJSON.getBoolean("tbddate"), exactTime = launchJSON.getBoolean("tbdtime");
                final String name = launchJSON.getString("name");
                final String status = launchJSON.getJSONObject("status").getString("name").toUpperCase();
                final String location = padJSON.getJSONObject("location").getString("name");
                final String rocketImageURL = rocketConfigurationJSON.optString("image_url", null);

                final JSONObject missionJSON = launchJSON.optJSONObject("mission", null);
                RocketLaunchMission mission = null;
                if(missionJSON != null) {
                    final String missionName = missionJSON.getString("name"), description = missionJSON.getString("description"), missionType = missionJSON.getString("type");
                    mission = new RocketLaunchMission(missionName, description, missionType);
                }

                final String identifier = getEventDateIdentifier(windowStart, name);
                final RocketLaunchEvent launch = new RocketLaunchEvent(windowStart, windowEnd, name, status, location, exactDay, exactTime, probability, rocketImageURL, mission, windowStartString, windowEndString, videoURL, sources);
                putLoadedPreUpcomingEvent(launch.toPreUpcomingEventJSON(eventType, identifier, location, windowEnd));
                putUpcomingEvent(identifier, launch);
            });
        }
    }

    @Override
    public boolean isExactTime() {
        return true;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new RocketLaunchEvent(json);
    }
}
