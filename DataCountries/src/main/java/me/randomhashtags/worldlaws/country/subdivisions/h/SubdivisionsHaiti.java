package me.randomhashtags.worldlaws.country.subdivisions.h;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DEPARTMENTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case GRAND_ANSE: return "Grand'Anse";
            case NORD_EST: return "Nord-Est";
            case NORT_OUEST: return "Nord-Ouest";
            case SUD_EST: return "Sud-Est";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case NORD:
                return "(Haitian department)";
            default:
                return "(department)";
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
