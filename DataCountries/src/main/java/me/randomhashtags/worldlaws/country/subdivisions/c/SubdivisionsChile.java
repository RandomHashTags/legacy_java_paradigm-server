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
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            case ANTOFAGASTA: return "c/cc/Flag_of_Antofagasta_Region%2C_Chile.svg";
            case ARAUCANIA: return "f/fd/Flag_of_La_Araucan%C3%ADa_Region.svg";
            case ARICA_AND_PARINACOTA: return "f/fa/Flag_of_Arica_y_Parinacota%2C_Chile.svg";
            case ATACAMA: return "3/38/Flag_of_Atacama%2C_Chile.svg";
            case AYSEN: return "0/0d/Flag_of_Aysen%2C_Chile.svg";
            case BIOBIO: return "c/cd/Flag_of_Biob%C3%ADo_Region%2C_Chile.svg";
            case COQUIMBO: return "d/d7/Flag_of_Coquimbo_Region%2C_Chile.svg";
            case LOS_LAGOS: return "1/16/Flag_of_Los_Lagos_Region%2C_Chile.svg";
            case LOS_RIOS: return "a/ac/Flag_of_Los_R%C3%ADos%2C_Chile.svg";
            case MAGALLANES: return "8/8d/Flag_of_Magallanes%2C_Chile.svg";
            case MAULE: return "6/6a/Flag_of_Maule%2C_Chile.svg";
            case METROPOLITAN: return "8/8d/Flag_of_the_Metropolitan_Region%2C_Chile.svg";
            case NUBLE: return "3/30/Flag_of_Ñuble_Region%2C_Chile.svg";
            case O_HIGGINES: return "2/26/Flag_of_O%27Higgins_Region%2C_Chile.svg";
            case TARAPACA: return "8/87/Flag_of_Tarapaca%2C_Chile.svg";
            case VALPARAISO: return "5/50/Flag_of_Valparaiso_Region%2C_Chile.svg";
            default: return null;
        }
    }
}
