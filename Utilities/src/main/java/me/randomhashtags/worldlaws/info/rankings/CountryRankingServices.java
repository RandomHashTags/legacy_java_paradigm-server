package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class CountryRankingServices {

    public static void getRanked(String serviceBackendID, CompletionHandler handler) {
        final CountryRankingService service = valueOf(serviceBackendID);
        if(service != null) {
            service.getRankedJSON(handler);
        }
    }

    public static Stream<CountryService> getRankingsServices() {
        return CountryServices.SERVICES.parallelStream().filter(service -> service instanceof CountryRankingService);
    }
    private static CountryRankingService valueOf(String backendID) {
        final Optional<CountryService> optional = getRankingsServices().filter(service -> backendID.equals(service.getInfo().getTitle().toLowerCase().replace(" ", ""))).findFirst();
        return (CountryRankingService) optional.orElse(null);
    }
}
