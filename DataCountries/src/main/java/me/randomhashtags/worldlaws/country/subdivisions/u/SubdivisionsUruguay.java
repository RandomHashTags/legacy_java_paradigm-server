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
        switch (this) {
            case ARTIGAS: return "https://upload.wikimedia.org/wikipedia/commons/b/b7/Flag_of_Artigas_Department.svg";
            case CANELONES: return "https://upload.wikimedia.org/wikipedia/commons/6/6f/Flag_of_Canelones_Department.svg";
            case CERRO_LARGO: return "https://upload.wikimedia.org/wikipedia/commons/2/2f/Flag_of_Cerro_Largo_Department.svg";
            case COLONIA: return "https://upload.wikimedia.org/wikipedia/commons/5/5c/Flag_of_Colonia_Department.svg";
            case DURAZNO: return "https://upload.wikimedia.org/wikipedia/commons/5/5c/Flag_of_Durazno_Department.svg";
            case FLORES: return "https://upload.wikimedia.org/wikipedia/commons/1/11/Flag_of_Flores_Department.png";
            case FLORDIA: return "https://upload.wikimedia.org/wikipedia/commons/7/76/Flag_of_Florida_Department.png";
            case LAVALLEJA: return "https://upload.wikimedia.org/wikipedia/commons/6/60/Flag_of_Lavalleja_Department.svg";
            case MALDONADO: return "https://upload.wikimedia.org/wikipedia/commons/5/52/Flag_of_Maldonado_Department.png";
            case MONTEVIDEO: return "https://upload.wikimedia.org/wikipedia/commons/f/fc/Coat_of_arms_of_Montevideo_Department.svg";
            case PAYSANDU: return "https://upload.wikimedia.org/wikipedia/commons/2/2a/Flag_of_Paysandú_Department.svg";
            case RIO_NEGRO: return "https://upload.wikimedia.org/wikipedia/commons/c/c9/Flag_of_Rio_Negro_Department.svg";
            case RIVERA: return "https://upload.wikimedia.org/wikipedia/commons/f/f4/Flag_of_Rivera_Department.png";
            case ROCHA: return "https://upload.wikimedia.org/wikipedia/commons/7/7b/Flag_of_Rocha_Department.svg";
            case SALTO: return "https://upload.wikimedia.org/wikipedia/commons/3/32/Flag_of_Salto_Department.svg";
            case SAN_JOSE: return "https://upload.wikimedia.org/wikipedia/commons/1/12/Flag_of_San_José_Department.svg";
            case SORIANO: return "https://upload.wikimedia.org/wikipedia/commons/b/b4/Flag_of_Soriano_Department.svg";
            case TACUAREMBO: return "https://upload.wikimedia.org/wikipedia/commons/7/72/Coat_of_arms_of_Tacuarembó_Department.png";
            case TREINTA_Y_TRES: return "https://upload.wikimedia.org/wikipedia/commons/3/3c/Flag_of_Treinta_y_Tres_Department.svg";
            default: return null;
        }
    }
}
