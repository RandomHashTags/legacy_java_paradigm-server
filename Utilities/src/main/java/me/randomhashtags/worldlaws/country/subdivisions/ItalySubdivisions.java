package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum ItalySubdivisions implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Italy
    ABRUZZO,
    AOSTA_VALLEY,
    APULIA,
    BASILICATA,
    CALABRIA,
    CAMPANIA,
    EMILIA_ROMAGNA,
    FRIULI_VENEZLA_GIULIA,
    LAZIO,
    LIGURIA,
    LOMBARDY,
    MARCHE,
    MOLISE,
    PIEDMONT,
    SARDINIA,
    SICILY,
    TRENTINO_SOUTH_TYROL,
    TUSCANY,
    UMBRIA,
    VENETO,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ITALY;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        return null;
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return null;
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
