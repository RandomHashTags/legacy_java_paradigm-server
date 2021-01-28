package me.randomhashtags.worldlaws.country.ca;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.location.ICountry;

public enum CanadaLaws implements ICountry {
    INSTANCE;

    // https://laws.justice.gc.ca/eng/AnnualStatutes/index2020.html

    @Override
    public WLCountry getCountryBackendID() {
        return WLCountry.CANADA;
    }

    @Override
    public void getResponse(String value, CompletionHandler handler) {

    }
}
