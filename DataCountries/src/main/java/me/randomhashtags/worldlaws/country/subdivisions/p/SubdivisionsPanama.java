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

    GUNA_DE_MADUNGANDI,
    GUNA_DE_WARGANDI,
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

            case GUNA_DE_MADUNGANDI:
            case GUNA_DE_WARGANDI:
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

            case EMBERA: return "Emberá";
            case NGABE_BUGLE: return "Ngäbe-Buglé";
            case NASO_TJER_DI: return "Naso Tjër Di";

            case GUNA_DE_MADUNGANDI: return "Guna de Madungandí";
            case GUNA_DE_WARGANDI: return "Guna de Wargandí";
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
