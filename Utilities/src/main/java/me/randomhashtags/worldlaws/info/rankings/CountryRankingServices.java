package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class CountryRankingServices {
    public static List<CountryRankingService> SERVICES = new ArrayList<>();

    public static void getRanked(String serviceBackendID, CompletionHandler handler) {
        final CountryRankingService service = valueOf(serviceBackendID);
        if(service != null) {
            service.getRankedJSON(handler);
        }
    }
    public static void getValue(String name, String countryBackendID, CompletionHandler handler) {
        final CountryRankingService service = valueOf(name);
        if(service != null) {
            service.getValue(countryBackendID, handler);
        }
    }
    public static CountryRankingService valueOf(String backendID) {
        for(CountryRankingService service : SERVICES) {
            final CountryInfo info = service.getInfo();
            final String target = info.name().toLowerCase().replace("_", "");
            if(backendID.equals(target)) {
                return service;
            }
        }
        return null;
    }
}
