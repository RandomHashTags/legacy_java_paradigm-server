package me.randomhashtags.worldlaws.country.ca;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.LawController;
import me.randomhashtags.worldlaws.location.WLCountry;

public enum CanadaLaws implements LawController {
    INSTANCE;

    // https://laws.justice.gc.ca/eng/AnnualStatutes/index2020.html

    @Override
    public WLCountry getCountry() {
        return WLCountry.CANADA;
    }

    @Override
    public void getRecentActivity(APIVersion version, CompletionHandler handler) {
        handler.handleString(null);
    }

    @Override
    public void getResponse(APIVersion version, String value, CompletionHandler handler) {
        handler.handleString(null);
    }
}
