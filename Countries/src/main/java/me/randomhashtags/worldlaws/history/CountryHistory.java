package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.history.SovereignStateHistory;
import me.randomhashtags.worldlaws.history.country.HistoryUnitedStates;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public enum CountryHistory implements SovereignStateHistory {
    INSTANCE;

    @Override
    public Folder getFolder() {
        return null;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_COUNTRY_HISTORY;
    }

    @Override
    public JSONObjectTranslatable getJSONObject(WLCountry country) {
        final ICountryHistory history = getCountryHistory(country);
        JSONObjectTranslatable string = null;
        if(history != null) {
            string = new JSONObjectTranslatable();
            string.put("eras", history.getEras().toJSONObject());
            string.put("sources", history.getSources().toJSONObject());
        }
        return string;
    }

    @Override
    public JSONObjectTranslatable parseData(JSONObject json) {
        return null;
    }

    private ICountryHistory getCountryHistory(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return HistoryUnitedStates.INSTANCE;
            default: return null;
        }
    }
}
