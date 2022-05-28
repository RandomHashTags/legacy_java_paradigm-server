package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

public interface SovereignState extends Jsoupable, Jsonable {
    String FLAG_URL_PREFIX = "https://upload.wikimedia.org/wikipedia/";

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
    JSONObjectTranslatable getInformation(APIVersion version);

    default JSONArray getTimeZonesJSONArray(WLTimeZone...timezones) {
        final JSONArray array = new JSONArray();
        for(WLTimeZone timezone : timezones) {
            array.put(timezone.getIdentifier());
        }
        return array;
    }
}
