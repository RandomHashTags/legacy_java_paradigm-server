package me.randomhashtags.worldlaws.service;

public interface NewCountryServiceCentralData extends NewCountryService {
    @Override
    default boolean dataContainsAllCountryData() {
        return true;
    }
}
