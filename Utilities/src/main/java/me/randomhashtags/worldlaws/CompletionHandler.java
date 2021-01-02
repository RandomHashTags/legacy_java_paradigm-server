package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.location.CustomCountry;
import me.randomhashtags.worldlaws.location.Territory;
import me.randomhashtags.worldlaws.people.Politician;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface CompletionHandler {
    default void handle(Object object) { }
    default void handleCollection(Collection<?> collection) { }
    default void handleClient(@NotNull WLClient client) { }
    default void handleCustomCountry(CustomCountry country) { }
    default void handleCountryTerritories(HashSet<Territory> territories) { }
    default void handlePolitician(Politician politician) { }
    default void handlePoliticianCosponsors(List<Politician> cosponsors) { }
}
