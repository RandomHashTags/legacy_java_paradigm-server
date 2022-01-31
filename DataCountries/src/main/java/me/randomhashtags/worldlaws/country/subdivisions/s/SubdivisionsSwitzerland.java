package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsSwitzerland implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Cantons_of_Switzerland
    AARGAU,
    APPENZELL_AUSSERRHODEN,
    APPENZELL_INNERRHODEN,
    BASLE_LANDSCHAFT,
    BASLE_STADT,
    BERN,
    FRIBOURG,
    GENEVA,
    GLARUS,
    GRISONS,
    JURA,
    LUCERNE,
    NEUCHATEL,
    NIDWALDEN,
    OBWALDEN,
    SCHAFFHAUSEN,
    SCHWYZ,
    SOLOTHURN,
    ST_GALLEN,
    THURGAU,
    TICINO,
    URI,
    VALAIS,
    VAUD,
    ZUG,
    ZURICH,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SWITZERLAND;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.CANTONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BASLE_LANDSCHAFT: return "Basel-Landschaft";
            case BASLE_STADT: return "Basel-Stadt";
            case NEUCHATEL: return "Neuchâtel";
            case ST_GALLEN: return "St. Gallen";
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
