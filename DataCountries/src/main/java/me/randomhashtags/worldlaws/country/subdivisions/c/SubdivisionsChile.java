package me.randomhashtags.worldlaws.country.subdivisions.c;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsChile implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Chile
    ANTOFAGASTA,
    ARAUCANIA,
    ARICA_AND_PARINACOTA,
    ATACAMA,
    AYSEN,
    BIOBIO,
    COQUIMBO,
    LOS_LAGOS,
    LOS_RIOS,
    MAGALLANES,
    MAULE,
    METROPOLITAN,
    NUBLE,
    O_HIGGINES,
    TARAPACA,
    VALPARAISO,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CHILE;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case METROPOLITAN:
                return "Santiago Metropolitan";
            default:
                return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ARAUCANIA: return "Araucanía";
            case AYSEN: return "Aysén";
            case BIOBIO: return "Biobío";
            case LOS_RIOS: return "Los Ríos";
            case NUBLE: return "Ñuble";
            case O_HIGGINES: return "O'Higgins";
            case TARAPACA: return "Tarapacá";
            case VALPARAISO: return "Valparaíso";
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
