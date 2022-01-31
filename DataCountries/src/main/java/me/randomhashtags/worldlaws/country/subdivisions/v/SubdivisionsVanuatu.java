package me.randomhashtags.worldlaws.country.subdivisions.v;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsVanuatu implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Vanuatu
    MALAMPA,
    PENAMA,
    SANMA,
    SHEFA,
    TAFEA,
    TORBA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.VANUATU;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case MALAMPA: return "MAP";
            case PENAMA: return "PAM";
            case SANMA: return "SAM";
            case SHEFA: return "SEE";
            case TAFEA: return "TAE";
            case TORBA: return "TOB";
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
