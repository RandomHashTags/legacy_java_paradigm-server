package me.randomhashtags.worldlaws.country.subdivisions.a;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsArgentina implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Argentina
    AUTONOMOUS_CITY_OF_BUENOS_AIRES,
    BUENOS_AIRES,
    CATAMARCA,
    CHACO,
    CHUBUT,
    CORDOBA,
    CORRIENTES,
    ENTRE_RIOS,
    FORMOSA,
    JUJUY,
    LA_PAMPA,
    LA_RIOJA,
    MENDOZA,
    MISIONES,
    NEUQUEN,
    RIO_NEGRO,
    SALTA,
    SAN_JUAN,
    SAN_LUIS,
    SANTA_CRUZ,
    SANTA_FE,
    SANTIAGO_DEL_ESTERO,
    TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR,
    TUCUMAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ARGENTINA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BUENOS_AIRES: return SubdivisionType.AUTONOMOUS_CITIES;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case CORDOBA: return "Córdoba";
            case NEUQUEN: return "Neuquén";
            case RIO_NEGRO: return "Río Negro";
            case TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR: return "Tierra del Fuego, Antártida e Islas del Atlántico Sur";
            case TUCUMAN: return "Tucumán";
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
