package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsGermany implements SovereignStateSubdivision {
    BADEN_WURTTEMBERG, // Baden-Württemberg
    BAVARIA,
    BERLIN,
    BRANDENBURG,
    HAMBURG,
    HESSE,
    LOWER_SAXONY,
    MECKLENBURG_WESTERN_POMERANIA, // Mecklenburg-Western Pomerania
    NORTH_RHINE_WESTPHALIA, // North Rhine-Westphalia
    RHINELAND_PALATINATE, // Rhineland-Palatinate
    SAARLAND,
    SAXONY_ANHALT, // Saxony-Anhalt
    SAXONY,
    SCHLESWIG_HOLSTEIN, // Schleswig-Holstein
    THURINGIA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.GERMANY;
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

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
