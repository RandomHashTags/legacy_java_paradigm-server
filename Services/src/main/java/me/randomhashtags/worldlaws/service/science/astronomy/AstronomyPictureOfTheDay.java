package me.randomhashtags.worldlaws.service.science.astronomy;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.service.NASAService;
import org.json.JSONObject;

import java.util.HashMap;

public enum AstronomyPictureOfTheDay implements WLService {
    INSTANCE;

    private final HashMap<APIVersion, String> cache;
    private String apiKey;

    AstronomyPictureOfTheDay() {
        cache = new HashMap<>();
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_NASA_ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    private String getNASA_APIKey() {
        if(apiKey == null) {
            apiKey = Jsonable.getSettingsPrivateValuesJSON().getJSONObject("nasa").getString("api_key");
        }
        return apiKey;
    }

    public void get(APIVersion version, CompletionHandler handler) {
        if(cache.containsKey(version)) {
            handler.handleString(cache.get(version));
        } else {
            final long started = System.currentTimeMillis();
            final String apiKey = NASAService.getNASA_APIKey();
            requestJSONObject("https://api.nasa.gov/planetary/apod?api_key=" + apiKey, RequestMethod.GET, CONTENT_HEADERS, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    json.remove("date");
                    json.remove("service_version");
                    final String string = json.toString();
                    cache.put(version, string);
                    WLLogger.logInfo("AstronomyPictureOfTheDay - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(string);
                }
            });
        }
    }
}
