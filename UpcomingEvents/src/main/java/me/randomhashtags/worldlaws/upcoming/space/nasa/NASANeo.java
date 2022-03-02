package me.randomhashtags.worldlaws.upcoming.space.nasa;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.service.NASAService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
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
        refreshNeo(today);
    }

    private void refreshNeo(LocalDate startDate) {
        final String startDateString = getURLFormattedDateString(startDate), endDateString = getURLFormattedDateString(startDate.plusWeeks(1)), apiKey = NASAService.getNASA_APIKey();

        final String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + startDateString + "&end_date=" + endDateString + "&detailed=true&api_key=" + apiKey;
        final JSONObject json = requestJSONObject(url);
        if(json != null) {
            final UpcomingEventType type = getType();
            final String formattedDateString = getFormattedDateString(startDate);
            final JSONArray nearEarthObjects = json.getJSONObject("near_earth_objects").getJSONArray(formattedDateString);
            final List<Object> list = nearEarthObjects.toList();
            final EventSources sources = new EventSources(new EventSource("NASA: Center for Near Earth Object Studies", "https://cneos.jpl.nasa.gov"));
            new CompletableFutures<HashMap<String, Object>>().stream(list, hashmap -> {
                final JSONObject mapJSON = new JSONObject(hashmap);
                final String name = mapJSON.getString("name");
                final boolean isPotentiallyHazardousAsteroid = mapJSON.getBoolean("is_potentially_hazardous_asteroid");

                final JSONObject estimatedDiameter = mapJSON.getJSONObject("estimated_diameter").getJSONObject("meters");
                final float estimatedDiameterMin = estimatedDiameter.getFloat("estimated_diameter_min"), estimatedDiameterMax = estimatedDiameter.getFloat("estimated_diameter_max");

                final JSONObject closeApproach = mapJSON.getJSONArray("close_approach_data").getJSONObject(0);
                final long closeApproachEpoch = closeApproach.getLong("epoch_date_close_approach");
                final String relativeVelocity = closeApproach.getJSONObject("relative_velocity").getString("kilometers_per_hour");

                final String id = getEventIdentifier(startDate) + "." + name.replace(" ", "");
                final NearEarthObjectEvent neo = new NearEarthObjectEvent(name, closeApproachEpoch, isPotentiallyHazardousAsteroid, estimatedDiameterMin, estimatedDiameterMax, relativeVelocity, sources);
                putLoadedPreUpcomingEvent(id, neo.toPreUpcomingEventJSON(type, id, null));
                putUpcomingEvent(id, neo.toString());
            });
        }
    }
    private String getURLFormattedDateString(LocalDate date) {
        final int year = date.getYear(), month = date.getMonthValue(), day = date.getDayOfMonth();
        return year + "-" + month + "-" + day;
    }
    private String getFormattedDateString(LocalDate date) {
        final int year = date.getYear(), month = date.getMonthValue(), day = date.getDayOfMonth();
        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : "" + day);
    }
    private String getEventIdentifier(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getMonthValue();
    }

    @Override
    public String loadUpcomingEvent(String id) {
        return null;
    }
}
