package me.randomhashtags.worldlaws.country.history;

import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.CountryService;

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
