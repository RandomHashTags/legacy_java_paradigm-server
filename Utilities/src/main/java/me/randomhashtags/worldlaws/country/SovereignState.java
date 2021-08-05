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
    void getInformation(APIVersion version, CompletionHandler handler);
}
