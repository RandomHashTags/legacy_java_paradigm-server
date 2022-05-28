package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNicaragua implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Departments_of_Nicaragua
    BOACO,
    CARAZO,
    CHINANDEGA,
    CHONTALES,
    ESTELI,
    GRANADA,
    JINOTEGA,
    LEON,
    MADRIZ,
    MANAGUA,
    MASAYA,
    MATAGALPA,
    NORTH_CARIBBEAN_COAST,
    NUEVA_SEGOVIA,
    RIO_SAN_JUAN,
    RIVAS,
    SOUTH_CARIBBEAN_COAST,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NICARAGUA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DEPARTMENTS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case NORTH_CARIBBEAN_COAST:
            case SOUTH_CARIBBEAN_COAST:
                return SubdivisionType.AUTONOMOUS_REGIONS;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case NORTH_CARIBBEAN_COAST: return "North Caribbean Coast Autonomous Region";
            case SOUTH_CARIBBEAN_COAST: return "South Caribbean Coast Autonomous Region";
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ESTELI: return "Estelí";
            case LEON: return "León";
            case RIO_SAN_JUAN: return "Río San Juan";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case NORTH_CARIBBEAN_COAST:
            case SOUTH_CARIBBEAN_COAST:
                return "";
            default:
                return null;
        }
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
}
