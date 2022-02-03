package me.randomhashtags.worldlaws.upcoming.space.nasa;

import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.service.NASAService;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.NearEarthObjectEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public final class NASANeo extends USAUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_NEAR_EARTH_OBJECT;
    }

    @Override
    public void load() {
        final LocalDate today = LocalDate.now();
        final int year = today.getYear(), month = today.getMonthValue(), day = today.getDayOfMonth();
        refreshNeo(year, month, day);
    }

    private void refreshNeo(int year, int month, int day) {
        final String date = year + "-" + month + "-" + day, apiKey = NASAService.getNASA_APIKey();
        final String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + date + "&end_date=" + date + "&detailed=true&api_key=" + apiKey;
        final JSONObject json = requestJSONObject(url, RequestMethod.GET);
        if(json != null) {
            final UpcomingEventType type = getType();
            final String formattedDateString = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : "" + day);
            final JSONArray nearEarthObjects = json.getJSONObject("near_earth_objects").getJSONArray(formattedDateString);
            final List<Object> list = nearEarthObjects.toList();
            new ParallelStream<HashMap<String, Object>>().stream(list, hashmap -> {
                final JSONObject mapJSON = new JSONObject(hashmap);
                final String name = mapJSON.getString("name");
                final boolean isPotentiallyHazardousAsteroid = mapJSON.getBoolean("is_potentially_hazardous_asteroid");

                final JSONObject estimatedDiameter = mapJSON.getJSONObject("estimated_diameter").getJSONObject("meters");
                final float estimatedDiameterMin = estimatedDiameter.getFloat("estimated_diameter_min"), estimatedDiameterMax = estimatedDiameter.getFloat("estimated_diameter_max");

                final JSONObject closeApproach = mapJSON.getJSONArray("close_approach_data").getJSONObject(0);
                final long closeApproachEpoch = closeApproach.getLong("epoch_date_close_approach");
                final String relativeVelocity = closeApproach.getJSONObject("relative_velocity").getString("kilometers_per_hour");

                final String id = (month + "-" + year + "-" + day) + "." + name.replace(" ", "");
                final NearEarthObjectEvent neo = new NearEarthObjectEvent(name, closeApproachEpoch, isPotentiallyHazardousAsteroid, estimatedDiameterMin, estimatedDiameterMax, relativeVelocity);
                putLoadedPreUpcomingEvent(id, neo.toPreUpcomingEventJSON(type, id, null));
                putUpcomingEvent(id, neo.toString());
            });
        }
    }

    @Override
    public String loadUpcomingEvent(String id) {
        return null;
    }
}
