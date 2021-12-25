package me.randomhashtags.worldlaws.country.subdivisions.f;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsFrance implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_France
    AUVERGNE_RHONE_ALPES,
    BURGUNDY_FREE_COUNTY,
    BRITTANY,
    CENTRE_LOIRE_VALLEY,
    CORSICA,
    GREAT_EAST,
    UPPER_FRANCE,
    ISLAND_OF_FRANCE,
    NORMANDY,
    NEW_AQUITAINE,
    OCCITANIA,
    LOIRE_COUNTRIES,
    PROVENCE_ALPS_AZURE_COAST,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.FRANCE;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AUVERGNE_RHONE_ALPES: return "Auvergne-Rhône-Alps";
            case BURGUNDY_FREE_COUNTY: return "Burgundy-Free County";
            case CENTRE_LOIRE_VALLEY: return "Centre-Loire Valley";
            case PROVENCE_ALPS_AZURE_COAST: return "Provence-Alpes-Côte d'Azur";
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
