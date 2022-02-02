package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.history.SovereignStateHistory;
import me.randomhashtags.worldlaws.history.country.HistoryUnitedStates;

public enum CountryHistory implements SovereignStateHistory {
    INSTANCE;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_COUNTRY_HISTORY;
    }

    public String getCountryValue(WLCountry country) {
        final ICountryHistory history = getCountryHistory(country);
        String string = null;
        if(history != null) {
            string = "{" + history.getEras().toString() +
                    ",\"sources\":" + history.getSources().toString() +
                    "}";
        }
        return string;
    }

    private ICountryHistory getCountryHistory(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return HistoryUnitedStates.INSTANCE;
            default: return null;
        }
    }
}
