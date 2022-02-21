package me.randomhashtags.worldlaws.country.subdivisions.g;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsGuatemala implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Departments_of_Guatemala
    ALTA_VERAPAZ,
    BAJA_VERAPAZ,
    CHIMALTENANGO,
    CHIQUIMULA,
    EL_PROGRESO,
    ESCUINTLA,
    GUATEMALA,
    HUEHUETENANGO,
    IZABAL,
    JALAPA,
    JUTIAPA,
    PETEN,
    QUETZALTENANGO,
    QUICHE,
    RETALHUEU,
    SACATEPEQUEZ,
    SAN_MARCOS,
    SANTA_ROSA,
    SOLOLA,
    SUCHITEPEQUEZ,
    TOTONICAPAN,
    ZACAPA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.GUATEMALA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DEPARTMENTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case PETEN: return "Petén";
            case QUICHE: return "Quiché";
            case SACATEPEQUEZ: return "Sacatepéquez";
            case SOLOLA: return "Sololá";
            case SUCHITEPEQUEZ: return "Suchitepéquez";
            case TOTONICAPAN: return "Totonicapán";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case SANTA_ROSA:
                return ",_Guatemala";
            default:
                return null;
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
