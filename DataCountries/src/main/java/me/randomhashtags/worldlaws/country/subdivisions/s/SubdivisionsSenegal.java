package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsSenegal implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Senegal
    DAKAR,
    DIOURBEL,
    FATICK,
    KAFFRINE,
    KAOLACK,
    KEDOUGOU,
    KOLDA,
    LOUGA,
    MATAM,
    SAINT_LOUIS,
    SEDHIOU,
    TAMBACOUNDA,
    THIES,
    ZIGUINCHOR,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SENEGAL;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case KEDOUGOU: return "Kédougou";
            case SAINT_LOUIS: return "Saint-Louis";
            case SEDHIOU: return "Sédhiou";
            case THIES: return "Thiès";
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
}
