package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNiger implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Niger
    AGADEZ,
    DIFFA,
    DOSSO,
    MARADI,
    NIAMEY,
    TAHOUA,
    TILLABERI,
    ZINDER,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NIGER;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case TILLABERI: return "Tillabéri";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case NIAMEY:
                return null;
            default:
                return "_Region";
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
