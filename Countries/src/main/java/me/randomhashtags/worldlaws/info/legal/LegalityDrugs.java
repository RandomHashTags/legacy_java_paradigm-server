package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum LegalityDrugs implements LegalityDrug {
    AYAHUASCA("https://en.wikipedia.org/wiki/Legal_status_of_ayahuasca_by_country", "Wikipedia: Legal status of ayahuasca by country"),
    COCAINE("https://en.wikipedia.org/wiki/Legal_status_of_cocaine", "Wikipedia: Legal status of cocaine", true),
    IBOGAINE("https://en.wikipedia.org/wiki/Legal_status_of_ibogaine_by_country", "Wikipedia: Legal status of ibogaine by country"),
    METH("https://en.wikipedia.org/wiki/Legal_status_of_methamphetamine", "Wikipedia: Legal status of methamphetamine", true, "Production"),
    PSILOCYBIN_MUSHROOMS("https://en.wikipedia.org/wiki/Legal_status_of_psilocybin_mushrooms", "Wikipedia: Legal status of psilocybin mushrooms", true),
    PSYCHOACTIVE_CACTUS("https://en.wikipedia.org/wiki/Legal_status_of_psychoactive_cactus_by_country", "Wikipedia: Legal status of psychoactive cactus by country"),
    SALVIA_DIVINORUM("https://en.wikipedia.org/wiki/Legal_status_of_Salvia_divinorum", "Wikipedia: Legal status of Salvia divinorum", true),
    ;

    private final String url, siteName, cultivationTitle;
    private final boolean doesRemoveLastElement;
    private HashMap<String, String> countries;

    LegalityDrugs(String url, String siteName) {
        this(url, siteName, false);
    }
    LegalityDrugs(String url, String siteName, boolean doesRemoveLastElement) {
        this(url, siteName, doesRemoveLastElement, "Cultivation");
    }
    LegalityDrugs(String url, String siteName, boolean doesRemoveLastElement, String cultivationTitle) {
        this.url = url;
        this.siteName = siteName;
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
    public String getSiteName() {
        return siteName;
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
