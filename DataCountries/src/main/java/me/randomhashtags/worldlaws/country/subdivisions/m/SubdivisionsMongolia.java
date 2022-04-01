package me.randomhashtags.worldlaws.country.subdivisions.m;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsMongolia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Mongolia
    ARKHANGAI,
    BAYAN_OLGII,
    BAYANKHONGOR,
    BULGAN,
    DARKHAN_UUL,
    DORNOD,
    DORNOGOVI,
    DUNDGOVI,
    GOVI_ALTAI,
    GOVISUMBER,
    KHENTII,
    KHOVD,
    KHOVSGOL,
    OMNOGOVI,
    ORKHON,
    OVORKHANGAI,
    SELENGE,
    SUKHBAATAR,
    TOV,
    ULAANBAATAR,
    UVS,
    ZAVKHAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.MONGOLIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case ULAANBAATAR: return SubdivisionType.MUNICIPALITIES;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BAYAN_OLGII: return "Bayan-Ölgii";
            case DARKHAN_UUL: return "Darkhan-Uul";
            case GOVI_ALTAI: return "Govi-Altai";
            case GOVISUMBER: return "Govisümber";
            case KHOVSGOL: return "Khövsgöl";
            case OMNOGOVI: return "Ömnögovi";
            case OVORKHANGAI: return "Övörkhangai";
            case SUKHBAATAR: return "Sükhbaatar";
            case TOV: return "Töv";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case ULAANBAATAR: return null;
            default: return "_Province";
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
