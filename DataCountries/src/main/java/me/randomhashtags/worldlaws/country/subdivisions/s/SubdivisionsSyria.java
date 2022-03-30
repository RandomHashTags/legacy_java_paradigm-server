package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsSyria implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Governorates_of_Tunisia
    AL_HASAKA,
    ALEPPO,
    AS_SUWAYDA,
    DAMASCUS,
    DARAA,
    DEIR_EZ_ZOR,
    HAMA,
    HOMS,
    IDLIB,
    LATAKIA,
    QUNEITRA,
    RAQQA,
    RIF_DIMASHQ,
    TARTUS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SYRIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.GOVERNORATES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AL_HASAKA: return "Al-Hasakah";
            case AS_SUWAYDA: return "As-Suwayda";
            case DEIR_EZ_ZOR: return "Deir ez-Zor";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        return "_Governorate";
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
