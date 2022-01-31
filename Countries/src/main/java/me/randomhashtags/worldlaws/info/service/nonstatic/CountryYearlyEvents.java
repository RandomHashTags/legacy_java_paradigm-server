package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.CountryService;

public enum CountryYearlyEvents implements CountryService {
    INSTANCE;

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_STATIC;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_COUNTRY_YEARLY_EVENTS;
    }

    @Override
    public String loadData() {
        return null;
    }
}
