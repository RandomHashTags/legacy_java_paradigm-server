package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsNicaragua implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Departments_of_Nicaragua
    BOACO,
    CARAZO,
    CHINANDEGA,
    CHONTALES,
    ESTELI, // Estelí
    GRANADA,
    JINOTEGA,
    LEON, // León
    MADRIZ,
    MANAGUA,
    MASAYA,
    MATAGALPA,
    NORTH_CARIBBEAN_COAST,
    NUEVA_SEGOVIA,
    RIO_SAN_JUAN, // Río San Juan
    RIVAS,
    SOUTH_CARIBBEAN_COAST,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NICARAGUA;
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case BOACO: return "BO";
            case CARAZO: return "CA";
            case CHINANDEGA: return "CI";
            case CHONTALES: return "CO";
            case ESTELI: return "ES";
            case GRANADA: return "GR";
            case JINOTEGA: return "JI";
            case LEON: return "LE";
            case MADRIZ: return "MD";
            case MANAGUA: return "MN";
            case MASAYA: return "MS";
            case MATAGALPA: return "MT";
            case NORTH_CARIBBEAN_COAST: return "AN";
            case NUEVA_SEGOVIA: return "NS";
            case RIO_SAN_JUAN: return "SJ";
            case RIVAS: return "RI";
            case SOUTH_CARIBBEAN_COAST: return "AS";
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
