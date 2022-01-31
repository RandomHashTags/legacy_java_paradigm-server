package me.randomhashtags.worldlaws.country.subdivisions.m;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsMadagascar implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Madagascar
    ALAOTRA_MANGORO,
    AMORON_I_MANIA,
    ANALAMANGA,
    ANALANJIROFO,
    ANDROY,
    ANOSY,
    ATSIMO_ANDREFANA,
    ATSIMO_ATSINANANA,
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
    VATOVAVY_FITOVINANY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.MADAGASCAR;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ALAOTRA_MANGORO: return "Alaotra-Mangoro";
            case AMORON_I_MANIA: return "Amoron'i Mania";
            case ATSIMO_ANDREFANA: return "Atsimo-Andrefana";
            case ATSIMO_ATSINANANA: return "Atsimo-Atsinanana";
            case VATOVAVY_FITOVINANY: return "Vatovavy-Fitovinany";
            default: return null;
        }
    }

    @Override
    public String getISOAlpha2() {
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
}
