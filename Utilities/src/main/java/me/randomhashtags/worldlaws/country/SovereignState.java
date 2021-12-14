package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import org.json.JSONArray;

import java.util.Arrays;

public interface SovereignState extends Jsoupable, Jsonable {
    default String getBackendID() {
        return getShortName().toLowerCase().replace(" ", "");
    }

    String getShortName();
    String getName();
    String getISOAlpha2();
    String getFlagURL();
    default WLTimeZone[] getTimeZones() {
        return null;
    }
    default WLTimeZone[] collectTimeZones(WLTimeZone...timezones) {
        return timezones;
    }
    void getInformation(APIVersion version, CompletionHandler handler);

    default JSONArray getTimeZonesJSONArray(WLTimeZone...timezones) {
        final JSONArray array = new JSONArray();
        Arrays.stream(timezones).parallel().forEach(timezone -> {
            array.put(timezone.getIdentifier());
        });
        return array;
    }
}
