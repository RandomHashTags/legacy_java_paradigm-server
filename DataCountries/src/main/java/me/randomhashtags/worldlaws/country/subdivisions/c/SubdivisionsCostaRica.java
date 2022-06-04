package me.randomhashtags.worldlaws.country.subdivisions.c;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsCostaRica implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Costa_Rica
    ALAJUELA,
    CARTAGO,
    GUANACASTE,
    HEREDIA,
    LIMON,
    PUNTARENAS,
    SAN_JOSE,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.COSTA_RICA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case LIMON: return "Limón";
            case SAN_JOSE: return "San José";
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
            case ALAJUELA: return "9/91/Bandera_de_la_Provincia_de_Alajuela.svg";
            case CARTAGO: return "0/06/Bandera_de_la_Provincia_de_Cartago.svg";
            case GUANACASTE: return "0/04/Bandera_de_la_Provincia_de_Guanacaste.svg";
            case HEREDIA: return "3/35/Bandera_de_la_Provincia_de_Heredia.svg";
            case LIMON: return "3/38/Bandera_de_la_Provincia_de_Limón.svg";
            case PUNTARENAS: return "e/e1/Bandera_de_la_Provincia_de_Puntarenas.svg";
            case SAN_JOSE: return "e/ee/Bandera_de_la_Provincia_de_San_José.svg";
            default: return null;
        }
    }
}
