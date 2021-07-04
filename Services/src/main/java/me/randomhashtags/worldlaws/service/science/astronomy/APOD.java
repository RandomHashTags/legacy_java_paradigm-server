package me.randomhashtags.worldlaws.service.science.astronomy;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import me.randomhashtags.worldlaws.service.ServiceInfo;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.util.HashMap;

public enum APOD implements WLService { // Astronomy Picture of the Day
    INSTANCE;

    private final HashMap<APIVersion, String> cache;

    APOD() {
        cache = new HashMap<>();
    }

    @Override
    public SovereignStateInfo getInfo() {
        return ServiceInfo.NASA_ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    public void get(APIVersion version, CompletionHandler handler) {
        if(cache.containsKey(version)) {
            handler.handleString(cache.get(version));
        } else {
            final long started = System.currentTimeMillis();
            requestJSONObject("https://api.nasa.gov/planetary/apod?api_key=" + NASA_API_KEY, RequestMethod.GET, CONTENT_HEADERS, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    json.remove("date");
                    json.remove("service_version");
                    final String string = json.toString();
                    cache.put(version, string);
                    WLLogger.log(Level.INFO, "APOD - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(string);
                }
            });
        }
    }
}
