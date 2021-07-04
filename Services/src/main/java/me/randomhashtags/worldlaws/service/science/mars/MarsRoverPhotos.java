package me.randomhashtags.worldlaws.service.science.mars;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;

public enum MarsRoverPhotos implements WLService {
    INSTANCE;

    private String cache;

    @Override
    public SovereignStateInfo getInfo() {
        return null;
    }

    private String[] getRovers() {
        return new String[] { "curiosity", "opportunity", "spirit" };
    }

    public void get(APIVersion version, CompletionHandler handler) {
        if(cache != null) {
            handler.handleString(cache);
        } else {
            final LocalDate today = LocalDate.now().minusDays(1);
            final int year = today.getYear(), month = today.getMonthValue(), day = today.getDayOfMonth();
            final String rover = "curiosity";
            requestJSONObject("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?earth_date=" + year + "-" + month + "-" + day + "&api_key=" + NASA_API_KEY, RequestMethod.GET, CONTENT_HEADERS, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    final JSONArray photos = json.getJSONArray("photos");
                    if(!photos.isEmpty()) {
                        for(Object obj : photos) {
                            final JSONObject photoJSON = (JSONObject) obj;
                            photoJSON.remove("earth_date");
                            photoJSON.remove("rover");
                        }
                    }
                    cache = json.toString();
                    handler.handleString(cache);
                }
            });
        }
    }
}
