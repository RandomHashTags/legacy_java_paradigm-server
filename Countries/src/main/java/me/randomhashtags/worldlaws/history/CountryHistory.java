package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateHistory;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.history.country.HistoryUnitedStates;

public enum CountryHistory implements SovereignStateHistory {
    INSTANCE;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_COUNTRY_HISTORY;
    }

    public void getCountryValue(WLCountry country, CompletionHandler handler) {
        final ICountryHistory history = getCountryHistory(country);
        String string = null;
        if(history != null) {
            string = "{" + history.getEras().toString() +
                    ",\"sources\":" + history.getSources().toString() +
                    "}";
        }
        handler.handleServiceResponse(INSTANCE, string);
    }

    private ICountryHistory getCountryHistory(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return HistoryUnitedStates.INSTANCE;
            default: return null;
        }
    }
}
