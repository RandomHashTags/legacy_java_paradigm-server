package me.randomhashtags.worldlaws.event.space.nasa;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;

public enum NASANeo implements USAEventController {
    INSTANCE;

    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_NEAR_EARTH_OBJECT;
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
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();

        final LocalDate today = LocalDate.now();
        final int year = today.getYear(), month = today.getMonthValue(), day = today.getDayOfMonth();
        refreshNeo(year, month, day, handler);
    }

    private void refreshNeo(int year, int month, int day, CompletionHandler handler) {
        final String API_KEY = "***REMOVED***";
        final String date = year + "-" + month + "-" + day;
        final String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + date + "&end_date=" + date + "&detailed=true&api_key=" + API_KEY;
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                final String formattedDateString = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : "" + day);
                final JSONArray nearEarthObjects = jsonobject.getJSONObject("near_earth_objects").getJSONArray(formattedDateString);
                for(Object obj : nearEarthObjects) {
                    final JSONObject json = (JSONObject) obj;
                    final String name = json.getString("name");
                    final boolean isPotentiallyHazardousAsteroid = json.getBoolean("is_potentially_hazardous_asteroid");

                    final JSONObject estimatedDiameter = json.getJSONObject("estimated_diameter").getJSONObject("meters");
                    final float estimatedDiameterMin = estimatedDiameter.getFloat("estimated_diameter_min"), estimatedDiameterMax = estimatedDiameter.getFloat("estimated_diameter_max");

                    final JSONObject closeApproach = json.getJSONArray("close_approach_data").getJSONObject(0);
                    final long closeApproachEpoch = closeApproach.getLong("epoch_date_close_approach");
                    final String relativeVelocity = closeApproach.getJSONObject("relative_velocity").getString("kilometers_per_hour");

                    final String id = (month + "-" + year + "-" + day) + "." + name.replace(" ", "");
                    final NearEarthObject neo = new NearEarthObject(name, closeApproachEpoch, isPotentiallyHazardousAsteroid, estimatedDiameterMin, estimatedDiameterMax, relativeVelocity);
                    upcomingEvents.put(id, neo.toJSON());
                    final String preUpcomingEventString = new PreUpcomingEvent(id, name, "NEO: " + name, null).toString();
                    preUpcomingEvents.put(id, preUpcomingEventString);
                }
                handler.handle(null);
            }
        });
    }

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        final String value = upcomingEvents.getOrDefault(id, null);
        handler.handle(value);
    }

    private final class NearEarthObject implements UpcomingEvent {
        private final String name, relativeVelocity;
        private final boolean potentiallyHazardous;
        private final float estimatedDiameterMin, estimatedDiameterMax;
        private final EventDate date;

        public NearEarthObject(String name, long closeApproachEpoch, boolean potentiallyHazardous, float estimatedDiameterMin, float estimatedDiameterMax, String relativeVelocity) {
            this.name = name;
            this.potentiallyHazardous = potentiallyHazardous;
            this.estimatedDiameterMin = estimatedDiameterMin;
            this.estimatedDiameterMax = estimatedDiameterMax;
            this.relativeVelocity = relativeVelocity;
            this.date = new EventDate(closeApproachEpoch);
        }

        @Override
        public EventDate getDate() {
            return date;
        }

        @Override
        public String getTitle() {
            return "NEO: " + name;
        }

        @Override
        public String getDescription() {
            return "Near earth object description???";
        }

        @Override
        public String getImageURL() {
            return null;
        }

        @Override
        public String getLocation() {
            return null;
        }

        @Override
        public EventSources getSources() {
            return new EventSources(new EventSource("NASA", "https://cneos.jpl.nasa.gov"));
        }

        @Override
        public String getPropertiesJSONObject() {
            return "{" +
                    "\"potentiallyHazardous\":" + potentiallyHazardous + "," +
                    "\"estimatedDiameterMin\":" + estimatedDiameterMin + "," +
                    "\"estimatedDiameterMax\":" + estimatedDiameterMax + "," +
                    "\"relativeVelocity\":" + relativeVelocity +
                    "}";
        }
    }
}
