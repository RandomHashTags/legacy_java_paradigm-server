package me.randomhashtags.worldlaws.country.subdivisions.g;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsGermany implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/States_of_Germany
    BADEN_WURTTEMBERG,
    BAVARIA,
    BERLIN,
    BRANDENBURG,
    BREMEN,
    HAMBURG,
    HESSE,
    LOWER_SAXONY,
    MECKLENBURG_WESTERN_POMERANIA,
    NORTH_RHINE_WESTPHALIA,
    RHINELAND_PALATINATE,
    SAARLAND,
    SAXONY,
    SAXONY_ANHALT,
    SCHLESWIG_HOLSTEIN,
    THURINGIA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.GERMANY;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case MECKLENBURG_WESTERN_POMERANIA: return "Mecklenburg-Vorpommern";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BADEN_WURTTEMBERG: return "Baden-Württemberg";
            case MECKLENBURG_WESTERN_POMERANIA: return "Mecklenburg-Western Pomerania";
            case NORTH_RHINE_WESTPHALIA: return "North Rhine-Westphalia";
            case RHINELAND_PALATINATE: return "Rhineland-Palatinate";
            case SAXONY_ANHALT: return "Saxony-Anhalt";
            case SCHLESWIG_HOLSTEIN: return "Schleswig-Holstein";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BREMEN:
                return "(state)";
            default:
                return "";
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
}
