package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.debt.DebtUS;
import me.randomhashtags.worldlaws.debt.ICountryDebt;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;

import java.util.logging.Level;

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
    public void getRankedJSON(CompletionHandler handler) {
        handler.handle(null);
    }

    @Override
    public void getValue(String country, CompletionHandler handler) {
        final ICountryDebt debt = valueOfCountryBackendID(country);
        if(debt != null) {
            debt.getCurrentJSON(handler);
        } else {
            WLLogger.log(Level.WARNING, "DebtCurrent - missing for country \"" + country + "\"!");
            handler.handle("null");
        }
    }

    public ICountryDebt valueOfCountryBackendID(String backendID) {
        for(ICountryDebt country : COUNTRIES) {
            if(backendID.equals(country.getCountryBackendID().getValue())) {
                return country;
            }
        }
        return null;
    }
}
