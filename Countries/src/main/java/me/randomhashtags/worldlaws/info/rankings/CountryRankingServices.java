package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class CountryRankingServices {

    public static String getRanked(String serviceBackendID) {
        final CountryRankingService service = valueOf(serviceBackendID);
        return service != null ? service.getRankedJSON() : null;
    }

    public static Stream<CountryService> getRankingsServices() {
        return CountryServices.STATIC_SERVICES.parallelStream().filter(service -> service instanceof CountryRankingService);
    }
    private static CountryRankingService valueOf(String backendID) {
        final Optional<CountryService> optional = getRankingsServices().filter(service -> backendID.equals(service.getInfo().getTitle().toLowerCase().replace(" ", ""))).findFirst();
        return (CountryRankingService) optional.orElse(null);
    }
}
