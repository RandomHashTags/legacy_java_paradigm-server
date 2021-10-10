package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsMadagascar implements SovereignStateSubdivision {
    ALAOTRA_MANGORO, // Alaotra-Mangoro
    AMORON_I_MANIA, // Amoron'i Mania
    ANALAMANGA,
    ANALANJIROFO,
    ANDROY,
    ANOSY,
    ATSIMO_ANDREFANA, // Atsimo-Andrefana
    ATSIMO_ATSINANANA, // Atsimo-Atsinanana
    ATSINANANA,
    BETSIBOKA,
    BOENY,
    BONGOLAVA,
    DIANA,
    IHOROMBE,
    ITASY,
    MATSIATRA_AMBONY,
    MELAKY,
    MENABE,
    SAVA,
    SOFIA,
    VAKINANKARATRA,
    VATOVAVY,
    VATOVAVY_FITOVINANY, // Vatovavy-Fitovinany
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.MADAGASCAR;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return null;
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
