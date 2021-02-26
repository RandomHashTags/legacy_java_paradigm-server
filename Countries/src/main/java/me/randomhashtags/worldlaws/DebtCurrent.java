package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.debt.DebtUS;
import me.randomhashtags.worldlaws.debt.ICountryDebt;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

public enum DebtCurrent implements CountryRankingService {
    INSTANCE;

    private final ICountryDebt[] COUNTRIES = new ICountryDebt[] {
            DebtUS.INSTANCE
    };

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_DEBT_CURRENT;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return null;
    }

    @Override
    public String getRankedJSON() {
        return null;
    }

    @Override
    public void getValue(String country, CompletionHandler handler) {
        final ICountryDebt debt = valueOfCountryBackendID(country);
        if(debt != null) {
            debt.getCurrentJSON(handler);
        } else {
            WLLogger.log(Level.WARN, "DebtCurrent - missing for country \"" + country + "\"!");
            handler.handle("null");
        }
    }

    @Override
    public void refresh(CompletionHandler handler) {

    }

    public ICountryDebt valueOfCountryBackendID(String backendID) {
        for(ICountryDebt country : COUNTRIES) {
            if(backendID.equals(country.getCountryBackendID().getBackendID())) {
                return country;
            }
        }
        return null;
    }
}
