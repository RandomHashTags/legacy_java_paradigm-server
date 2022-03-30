package me.randomhashtags.worldlaws.country.subdivisions.u;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsUruguay implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Departments_of_Uruguay
    ARTIGAS,
    CANELONES,
    CERRO_LARGO,
    COLONIA,
    DURAZNO,
    FLORES,
    FLORDIA,
    LAVALLEJA,
    MALDONADO,
    MONTEVIDEO,
    PAYSANDU,
    RIO_NEGRO,
    RIVERA,
    ROCHA,
    SALTO,
    SAN_JOSE,
    SORIANO,
    TACUAREMBO,
    TREINTA_Y_TRES,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.URUGUAY;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DEPARTMENTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case PAYSANDU: return "Paysandú";
            case RIO_NEGRO: return "Río Negro";
            case SAN_JOSE: return "San José";
            case TACUAREMBO: return "Tacuarembó";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            default:
                return "_Department";
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case ARTIGAS: return "AR";
            case CANELONES: return "CA";
            case CERRO_LARGO: return "CL";
            case COLONIA: return "CO";
            case DURAZNO: return "DU";
            case FLORES: return "FS";
            case FLORDIA: return "FD";
            case LAVALLEJA: return "LA";
            case MALDONADO: return "MA";
            case MONTEVIDEO: return "MO";
            case PAYSANDU: return "PA";
            case RIO_NEGRO: return "RN";
            case RIVERA: return "RV";
            case ROCHA: return "RO";
            case SALTO: return "SA";
            case SAN_JOSE: return "SJ";
            case SORIANO: return "SO";
            case TACUAREMBO: return "TA";
            case TREINTA_Y_TRES: return "TT";
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
