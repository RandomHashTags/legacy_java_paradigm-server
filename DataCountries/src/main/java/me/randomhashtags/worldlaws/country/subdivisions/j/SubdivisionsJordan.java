package me.randomhashtags.worldlaws.country.subdivisions.j;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsJordan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Governorates_of_Jordan
    AJLOUN,
    AMMAN,
    AQABA,
    BALQA,
    IRBID,
    JERASH,
    KARAK,
    MAAN,
    MADABA,
    MAFRAQ,
    TAFILAH,
    ZARQA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.JORDAN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.GOVERNORATES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case MAAN: return "Ma'an";
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
        switch (this) {
            default: return null;
        }
    }
}
