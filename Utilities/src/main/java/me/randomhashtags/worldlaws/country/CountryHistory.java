package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.history.HistoryUnitedStates;
import org.json.JSONObject;

public enum CountryHistory implements SovereignStateHistory {
    INSTANCE;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_COUNTRY_HISTORY;
    }

    public void getCountryValue(WLCountry country, CompletionHandler handler) {
        getJSONObject(Folder.COUNTRIES_HISTORY, country.getBackendID(), new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final ICountryHistory history = getCountryHistory(country);
                String string = null;
                if(history != null) {
                    string = "{" + history.getEras().toString() +
                            ",\"sources\":" + history.getSources().toString() +
                            "}";
                }
                handler.handleString(string);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleServiceResponse(INSTANCE, json != null ? json.toString() : null);
            }
        });
    }

    private ICountryHistory getCountryHistory(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return HistoryUnitedStates.INSTANCE;
            default: return null;
        }
    }
}
