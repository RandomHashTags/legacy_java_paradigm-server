package me.randomhashtags.worldlaws.country.subdivisions.l;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsLibya implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Districts_of_Libya
    BUTNAN,
    JABAL_AL_AKHDAR,
    JABAL_AL_GHARBI,
    JAFARAH,
    JUFRA,
    KUFRAH,
    MARJ,
    AL_WALHAT,
    NUQAT_AL_KHAMS,
    ZAWIYA,
    BENGHAZI,
    DARNAH,
    GHAT,
    MURQUB,
    MISRATA,
    MURZUQ,
    NALUT,
    SABHA,
    SIRTE,
    TRIPOLI,
    WADI_AL_HAYAA,
    WADI_AL_SHATII,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.LIBYA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DISTRICTS;
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BENGHAZI:
            case JAFARAH:
                return "";
            case TRIPOLI:
                return suffix + ",_Libya";
            default:
                return null;
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
