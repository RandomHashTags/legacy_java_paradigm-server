package me.randomhashtags.worldlaws.event.space.nasa;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.*;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;

public enum NASANeo implements USAEventController {
    INSTANCE;

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>> years;
    private final HashMap<String, String> preEvents, events;

    NASANeo() {
        years = new HashMap<>();
        preEvents = new HashMap<>();
        events = new HashMap<>();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_NEAR_EARTH_OBJECT;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        final LocalDate today = LocalDate.now();
        final int year = today.getYear(), month = today.getMonthValue(), day = today.getDayOfMonth();
        if(years.containsKey(year) && years.get(year).containsKey(month) && years.get(year).get(month).containsKey(day)) {
            handler.handle(years.get(year).get(month).get(day));
        } else {
            refreshNeo(year, month, day, handler);
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

    private void refreshNeo(int year, int month, int day, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String API_KEY = "***REMOVED***";
        final String date = year + "-" + month + "-" + day;
        final String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + date + "&end_date=" + date + "&detailed=true&api_key=" + API_KEY;
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                final UpcomingEventType type = getType();
                final String dateString = year + "-" + month + "-" + (day < 10 ? "0" + day : "" + day);
                final JSONArray nearEarthObjects = jsonobject.getJSONObject("near_earth_objects").getJSONArray(dateString);
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Object obj : nearEarthObjects) {
                    final JSONObject json = (JSONObject) obj;
                    final String name = json.getString("name");
                    final boolean isPotentiallyHazardousAsteroid = json.getBoolean("is_potentially_hazardous_asteroid");

                    final JSONObject estimatedDiameter = json.getJSONObject("estimated_diameter").getJSONObject("meters");
                    final float estimatedDiameterMin = estimatedDiameter.getFloat("estimated_diameter_min"), estimatedDiameterMax = estimatedDiameter.getFloat("estimated_diameter_max");

                    final JSONObject closeApproach = json.getJSONArray("close_approach_data").getJSONObject(0);
                    final long closeApproachEpoch = closeApproach.getLong("epoch_date_close_approach");
                    final String relativeVelocity = closeApproach.getJSONObject("relative_velocity").getString("kilometers_per_hour");

                    final NearEarthObject neo = new NearEarthObject(name, closeApproachEpoch, isPotentiallyHazardousAsteroid, estimatedDiameterMin, estimatedDiameterMax, relativeVelocity);
                    final EventDate neoDate = neo.getDate();
                    final String neoTitle = neo.getTitle();
                    final String identifier = getEventIdentifier(neoDate, neoTitle);
                    events.put(identifier, neo.toJSON());

                    final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(type, neoDate, neoTitle, "Near Earth Object description???", null);
                    final String string = preUpcomingEvent.toString();
                    preEvents.put(identifier, string);
                    builder.append(isFirst ? "" : ",").append(string);
                    isFirst = false;
                }
                builder.append("]");
                if(!years.containsKey(year)) {
                    years.put(year, new HashMap<>());
                }
                if(!years.get(year).containsKey(month)) {
                    years.get(year).put(month, new HashMap<>());
                }
                final String string = builder.toString();
                years.get(year).get(month).put(day, string);
                WLLogger.log(Level.INFO, "NASANeo - refreshed " + dateString + " (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handle(string);
            }
        });
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
        public UpcomingEventType getType() {
            return UpcomingEventType.SPACE_NASA;
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
