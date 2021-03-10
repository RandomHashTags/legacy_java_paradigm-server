package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum LegalityDrugs implements LegalityDrug {
    AYAHUASCA("https://en.wikipedia.org/wiki/Legal_status_of_ayahuasca_by_country"),
    COCAINE("https://en.wikipedia.org/wiki/Legal_status_of_cocaine", true),
    IBOGAINE("https://en.wikipedia.org/wiki/Legal_status_of_ibogaine_by_country"),
    METH("https://en.wikipedia.org/wiki/Legal_status_of_methamphetamine", true, "Production"),
    PSILOCYBIN_MUSHROOMS("https://en.wikipedia.org/wiki/Legal_status_of_psilocybin_mushrooms", true),
    PSYCHOACTIVE_CACTUS("https://en.wikipedia.org/wiki/Legal_status_of_psychoactive_cactus_by_country"),
    SALVIA_DIVINORUM("https://en.wikipedia.org/wiki/Legal_status_of_Salvia_divinorum", true),
    ;

    private final String url, cultivationTitle;
    private final boolean doesRemoveLastElement;
    private HashMap<String, String> countries;

    LegalityDrugs(String url) {
        this(url, false);
    }
    LegalityDrugs(String url, boolean doesRemoveLastElement) {
        this(url, doesRemoveLastElement, "Cultivation");
    }
    LegalityDrugs(String url, boolean doesRemoveLastElement, String cultivationTitle) {
        this.url = url;
        this.doesRemoveLastElement = doesRemoveLastElement;
        this.cultivationTitle = cultivationTitle;
    }

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.valueOf("LEGALITY_DRUG_" + name());
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
        return url;
    }

    @Override
    public String getCultivationTitle() {
        return cultivationTitle;
    }

    @Override
    public boolean doesRemoveLastElement() {
        return doesRemoveLastElement;
    }
}
