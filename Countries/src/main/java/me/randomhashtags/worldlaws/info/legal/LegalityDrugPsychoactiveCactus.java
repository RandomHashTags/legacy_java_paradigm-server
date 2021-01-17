package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum LegalityDrugPsychoactiveCactus implements LegalityDrug {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_DRUG_PSYCHOACTIVE_CACTUS;
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
        return "https://en.wikipedia.org/wiki/Legal_status_of_psychoactive_cactus_by_country";
    }

    @Override
    public String getSiteName() {
        return "Wikipedia: Legal status of psychoactive cactus by country";
    }
}
