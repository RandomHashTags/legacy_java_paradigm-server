package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum LegalityDrugMeth implements LegalityDrug {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_DRUG_METH;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }
    @Override
    public void setCountries(HashMap<String, String> countries) {
        this.countries = countries;
    }

    @Override
    public String getURL() {
        return "https://en.wikipedia.org/wiki/Legal_status_of_methamphetamine";
    }

    @Override
    public String getSiteName() {
        return "Wikipedia: Legal status of methamphetamine";
    }

    @Override
    public String getCultivationTitle() {
        return "Production";
    }

    @Override
    public boolean doesRemoveLastElement() {
        return true;
    }
}
