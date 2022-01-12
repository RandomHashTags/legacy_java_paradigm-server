package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.info.service.CountryService;

public interface SovereignStateHistory extends CountryService {
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.HISTORY;
    }

    @Override
    default String loadData() {
        return null;
    }
}
