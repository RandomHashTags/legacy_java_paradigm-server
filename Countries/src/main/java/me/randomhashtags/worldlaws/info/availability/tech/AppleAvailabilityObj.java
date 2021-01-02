package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.CountryInfo;

public final class AppleAvailabilityObj implements AppleAvailability {

    private final CountryInfo info;

    public AppleAvailabilityObj(CountryInfo info) {
        this.info = info;
    }

    @Override
    public CountryInfo getInfo() {
        return info;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        getAppleValue(countryBackendID, handler);
    }
}
