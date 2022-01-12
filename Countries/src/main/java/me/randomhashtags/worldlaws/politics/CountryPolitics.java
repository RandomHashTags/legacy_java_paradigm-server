package me.randomhashtags.worldlaws.politics;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;

public enum CountryPolitics implements CountryService {
    INSTANCE;

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.POLITICS;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return null;
    }

    @Override
    public String loadData() {
        return null;
    }
}
