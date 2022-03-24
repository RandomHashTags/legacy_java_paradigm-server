package me.randomhashtags.worldlaws.country.history;

import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryService;

public interface SovereignStateHistory extends NewCountryService {
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.HISTORY;
    }

    @Override
    default JSONObjectTranslatable loadData() {
        return null;
    }
}
