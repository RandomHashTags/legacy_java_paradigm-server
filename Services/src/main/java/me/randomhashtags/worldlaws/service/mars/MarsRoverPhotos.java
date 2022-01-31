package me.randomhashtags.worldlaws.service.mars;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.service.NASAService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;

public enum MarsRoverPhotos implements WLService {
    INSTANCE;

    private String cache;

    private String[] getRovers() {
        return new String[] { "curiosity", "opportunity", "spirit" };
    }

    public String get(APIVersion version) {
        if(cache == null) {
            final LocalDate today = LocalDate.now().minusDays(1);
            final int year = today.getYear(), month = today.getMonthValue(), day = today.getDayOfMonth();
            final String rover = "curiosity", apiKey = NASAService.getNASA_APIKey();
            final JSONObject json = requestJSONObject("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?earth_date=" + year + "-" + month + "-" + day + "&api_key=" + apiKey, RequestMethod.GET, CONTENT_HEADERS);
            final JSONArray photos = json.getJSONArray("photos");
            if(!photos.isEmpty()) {
                for(Object obj : photos) {
                    final JSONObject photoJSON = (JSONObject) obj;
                    photoJSON.remove("earth_date");
                    photoJSON.remove("rover");
                }
            }
            cache = json.toString();
        }
        return cache;
    }
}
