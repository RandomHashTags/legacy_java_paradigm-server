package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;

public interface SovereignState extends Jsoupable, Jsonable {
    default String getBackendID() {
        return getShortName().toLowerCase().replace(" ", "");
    }

    String getShortName();
    String getName();
    String getFlagURL();
    default WLTimeZone[] getTimeZones() {
        return null;
    }
    default WLTimeZone[] collectTimeZones(WLTimeZone...timezones) {
        return timezones;
    }
    void getInformation(APIVersion version, CompletionHandler handler);

    default String getTimeZonesJSON(WLTimeZone...timezones) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(WLTimeZone timezone : timezones) {
            builder.append(isFirst ? "" : ",").append("\"").append(timezone.getIdentifier()).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
}
