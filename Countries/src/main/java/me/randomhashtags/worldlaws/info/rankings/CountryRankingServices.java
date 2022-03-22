package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.CountryServices;
import me.randomhashtags.worldlaws.service.NewCountryService;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class CountryRankingServices {

    public static JSONObjectTranslatable getRanked(String serviceBackendID) {
        final CountryRankingService service = valueOf(serviceBackendID);
        return service != null ? service.getRankedJSON() : null;
    }

    public static HashSet<NewCountryService> getNewRankingsServices() {
        final HashSet<NewCountryService> services = new HashSet<>(CountryServices.STATIC_SERVICES);
        services.removeIf(service -> !(service instanceof CountryRankingService));
        return services;
    }
    public static Stream<NewCountryService> getRankingsServices() {
        return CountryServices.STATIC_SERVICES.stream().filter(service -> service instanceof CountryRankingService);
    }
    private static CountryRankingService valueOf(String backendID) {
        final Optional<NewCountryService> optional = getRankingsServices().filter(service -> backendID.equals(service.getInfo().getTitle().toLowerCase().replace(" ", ""))).findFirst();
        return (CountryRankingService) optional.orElse(null);
    }
}
