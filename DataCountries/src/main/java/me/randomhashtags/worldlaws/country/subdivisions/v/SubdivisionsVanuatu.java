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
        switch (this) {
            case MALAMPA: return "https://upload.wikimedia.org/wikipedia/commons/d/dd/Flag_of_Malampa_Province.svg";
            case PENAMA: return "https://upload.wikimedia.org/wikipedia/commons/a/ac/Flag_of_Penama_Province.svg";
            case SANMA: return "https://upload.wikimedia.org/wikipedia/commons/d/d0/Flag_of_Sanma_Province.svg";
            case SHEFA: return "https://upload.wikimedia.org/wikipedia/commons/d/df/Flag_of_Shefa_Province.svg";
            case TAFEA: return "https://upload.wikimedia.org/wikipedia/commons/5/55/Tafea_Province_Flag.svg";
            case TORBA: return "https://upload.wikimedia.org/wikipedia/en/a/af/Flag_of_Torba_%28Vanuatu%29_Province.svg";
            default: return null;
        }
    }
}
