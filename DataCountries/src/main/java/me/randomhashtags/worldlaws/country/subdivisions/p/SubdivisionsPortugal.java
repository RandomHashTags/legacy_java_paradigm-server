package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsPortugal implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Districts_of_Portugal
    ACORES,
    AVEIRO,
    BEJA,
    BRAGA,
    BRAGANCA,
    CASTELO_BRANCO,
    COIMBRA,
    EVORA,
    FARO,
    GUARDA,
    LEIRIA,
    LISBOA,
    MADERIA,
    PORTALEGRE,
    PORTO,
    SANTAREM,
    SETUBAL,
    VIANA_DO_CASTELO,
    VILA_REAL,
    VISEU,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PORTUGAL;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DISTRICTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ACORES: return "Açores";
            case BRAGANCA: return "Bragança";
            case EVORA: return "Évora";
            case SANTAREM: return "Santarém";
            case SETUBAL: return "Setúbal";
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
