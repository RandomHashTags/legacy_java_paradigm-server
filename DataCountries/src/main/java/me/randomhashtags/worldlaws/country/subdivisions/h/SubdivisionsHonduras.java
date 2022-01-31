package me.randomhashtags.worldlaws.country.subdivisions.h;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsHonduras implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Departments_of_Honduras
    ATLANTIDA,
    CHOLUTECA,
    COLON,
    COMAYAGUA,
    COPAN,
    CORTES,
    EL_PARAISO,
    FRANCISCO_MORAZAN,
    GRACIAS_A_DIOS,
    INTIBUCA,
    ISLAS_DE_LA_BAHIA,
    LA_PAZ,
    LEMPIRA,
    OCOTEPEQUE,
    OLANCHO,
    SANTA_BARBARA,
    VALLE,
    YORO,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.HONDURAS;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DEPARTMENTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ATLANTIDA: return "Atlántida";
            case COLON: return "Colón";
            case COPAN: return "Copán";
            case CORTES: return "Cortés";
            case EL_PARAISO: return "El Paraíso";
            case FRANCISCO_MORAZAN: return "Francisco Morazán";
            case GRACIAS_A_DIOS: return "Gracias a Dios";
            case INTIBUCA: return "Intibucá";
            case ISLAS_DE_LA_BAHIA: return "Islas de la Bahía";
            case SANTA_BARBARA: return "Santa Bárbara";
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
