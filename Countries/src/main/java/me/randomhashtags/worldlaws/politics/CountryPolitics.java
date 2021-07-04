package me.randomhashtags.worldlaws.politics;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CountryInformationType;

public enum CountryPolitics implements CountryService {
    INSTANCE;

    @Override
    public CountryInformationType getInformationType() {
        return CountryInformationType.POLITICS;
    }

    @Override
    public CountryInfo getInfo() {
        return null;
    }

    @Override
    public void loadData(CompletionHandler handler) {
    }
}
