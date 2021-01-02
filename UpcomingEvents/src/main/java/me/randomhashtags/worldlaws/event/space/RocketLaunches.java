package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.PreUpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEventType;
import me.randomhashtags.worldlaws.location.CountryBackendID;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.logging.Level;

public enum RocketLaunches implements EventController {
    INSTANCE;

    private String jsonPreUpcomingEvents;
    private HashMap<String, String> preEvents, events;

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(jsonPreUpcomingEvents != null) {
            handler.handle(jsonPreUpcomingEvents);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(jsonPreUpcomingEvents);
                }
            });
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

    @Override
    public CountryBackendID getCountryBackendID() {
        return null;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        preEvents = new HashMap<>();
        events = new HashMap<>();
        requestJSON("https://ll.thespacedevs.com/2.0.0/launch/upcoming/?format=json&limit=50&mode=detailed&offset=0", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;

                final JSONObject json = new JSONObject(object.toString());
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

                    final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(launch.getType(), date, name, location, rocketImageURL);
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
        });
    }

    private final class RocketLaunchMission {

        private final String name, description, type;

        RocketLaunchMission(String name, String description, String type) {
            this.name = LocalServer.fixEscapeValues(name);
            this.description = description;
            this.type = type;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"name\":\"" + name + "\"," +
                    "\"description\":\"" + description + "\"," +
                    "\"type\":\"" + type + "\"" +
                    "}";
        }
    }

    private final class RocketLaunch implements UpcomingEvent {

        private final EventDate date;
        private final String name, status, location, rocketImageURL, windowStart, windowEnd;
        private final boolean exactDay, exactTime;
        private final int probability;
        private final RocketLaunchMission mission;
        private final EventSources sources;

        RocketLaunch(EventDate date, String name, String status, String location, boolean exactDay, boolean exactTime, int probability, String rocketImageURL, RocketLaunchMission mission, String windowStart, String windowEnd, EventSources sources) {
            this.date = date;
            this.name = LocalServer.fixEscapeValues(name);
            this.status = status;
            this.location = location;
            this.exactDay = exactDay;
            this.exactTime = exactTime;
            this.probability = probability;
            this.rocketImageURL = rocketImageURL;
            this.mission = mission;
            this.windowStart = LocalServer.fixEscapeValues(windowStart);
            this.windowEnd = LocalServer.fixEscapeValues(windowEnd);
            this.sources = sources;
        }

        @Override
        public UpcomingEventType getType() {
            return UpcomingEventType.SPACE_ROCKET_LAUNCH;
        }

        @Override
        public EventDate getDate() {
            return date;
        }

        @Override
        public String getTitle() {
            return name;
        }

        @Override
        public String getDescription() {
            return mission != null ? mission.description : "Mission information about this launch is currently unknown";
        }

        @Override
        public String getImageURL() {
            return rocketImageURL;
        }

        @Override
        public String getLocation() {
            return location;
        }

        @Override
        public EventSources getSources() {
            return sources;
        }

        @Override
        public String getPropertiesJSONObject() {
            return "{" +
                    "\"windowStart\":\"" + windowStart + "\"," +
                    "\"windowEnd\":\"" + windowEnd + "\"," +
                    "\"exactDay\":" + exactDay + "," +
                    "\"exactTime\":" + exactTime + "," +
                    "\"status\":\"" + status + "\"," +
                    "\"probability\":" + probability + "," +
                    "\"mission\":" + (mission != null ? mission.toString() : "null") +
                    "}";
        }
    }
}
