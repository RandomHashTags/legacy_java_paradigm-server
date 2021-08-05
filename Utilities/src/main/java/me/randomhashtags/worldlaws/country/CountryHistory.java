package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.history.UnitedStatesHistory;
import org.json.JSONObject;

import java.util.List;

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
                final List<CountryHistorySection> history = loadHistory(country);
                if(history != null && !history.isEmpty()) {
                    final StringBuilder builder = new StringBuilder("{");
                    boolean isFirst = true;
                    for(CountryHistorySection section : history) {
                        builder.append(isFirst ? "" : ",").append(section.toString());
                        isFirst = false;
                    }
                    builder.append("}");
                    handler.handleString(builder.toString());
                } else {
                    handler.handleString(null);
                }
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(json != null ? json.toString() : null);
            }
        });
    }

    private List<CountryHistorySection> loadHistory(WLCountry country) {
        final ICountryHistory history = getCountryHistory(country);
        return history != null ? history.get() : null;
    }
    private ICountryHistory getCountryHistory(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return UnitedStatesHistory.INSTANCE;
            default: return null;
        }
    }
}
