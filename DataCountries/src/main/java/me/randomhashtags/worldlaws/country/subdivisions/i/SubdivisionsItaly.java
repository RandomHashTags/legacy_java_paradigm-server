package me.randomhashtags.worldlaws.country.subdivisions.i;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsItaly implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Italy
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case AOSTA_VALLEY:
            case FRIULI_VENEZLA_GIULIA:
            case SARDINIA:
            case SICILY:
            case TRENTINO_SOUTH_TYROL:
                return SubdivisionType.AUTONOMOUS_REGIONS;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case TRENTINO_SOUTH_TYROL: return "Trentino-Alto_Adige/Südtirol";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case EMILIA_ROMAGNA: return "Emilia-Romagna";
            case FRIULI_VENEZLA_GIULIA: return "Friuli-Venezia Giulia";
            case TRENTINO_SOUTH_TYROL: return "Trentino-South Tyrol";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            default:
                return "";
        }
    }

    @Override
    public String getISOAlpha2() {
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
}
