package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsSwitzerland implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Cantons_of_Switzerland
    APPENZELL_INNERRHODEN,
    APPENZELL_AUSSERRHODEN,
    AARGAU,
    BASLE_STADT,
    BASLE_LANDSCHAFT,
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
    TICINO,
    THURGAU,
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
    public String getPostalCodeAbbreviation() {
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
