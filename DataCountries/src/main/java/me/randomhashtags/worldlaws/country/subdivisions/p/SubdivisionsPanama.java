package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsPanama implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Panama
    BORCAS_DEL_TORO,
    CHIRIQUI,
    COCLE,
    COLON,
    DARIEN,
    HERRERA,
    LOS_SANTOS,
    PANAMA,
    PANAMA_OESTE,
    VERAGUAS,

    EMBERA,
    GUNA_YALA,
    NASO_TJER_DI,
    NGABE_BUGLE,

    MADUNGANDI,
    WARGANDI,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PANAMA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case EMBERA:
            case GUNA_YALA:
            case NASO_TJER_DI:
            case NGABE_BUGLE:
                return SubdivisionType.INDIGENOUS_PROVINCE;

            case MADUNGANDI:
            case WARGANDI:
                return SubdivisionType.MUNICIPALITIES;
            default:
                return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case CHIRIQUI: return "Chiriquí";
            case COCLE: return "Coclé";
            case COLON: return "Colón";
            case DARIEN: return "Darién";
            case PANAMA: return "Panamá";
            case PANAMA_OESTE: return "Panamá Oeste";

            case EMBERA: return "Emberá-Wounaan";
            case NGABE_BUGLE: return "Ngäbe-Buglé";
            case NASO_TJER_DI: return "Naso Tjër Di";

            case MADUNGANDI: return "Madungandí";
            case WARGANDI: return "Wargandí";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case EMBERA:
            case NGABE_BUGLE:
            case NASO_TJER_DI:
                return "Comarca";
            case GUNA_YALA:
            case MADUNGANDI:
            case WARGANDI:
                return "";
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
