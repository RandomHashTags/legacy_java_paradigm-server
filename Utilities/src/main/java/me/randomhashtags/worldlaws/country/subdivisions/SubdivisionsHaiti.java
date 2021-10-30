package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsHaiti implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Departments_of_Haiti
    ARTIBONITE,
    CENTRE,
    GRAND_ANSE,
    NIPPES,
    NORD,
    NORD_EST,
    NORT_OUEST,
    OUEST,
    SUD,
    SUD_EST,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.HAITI;
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
