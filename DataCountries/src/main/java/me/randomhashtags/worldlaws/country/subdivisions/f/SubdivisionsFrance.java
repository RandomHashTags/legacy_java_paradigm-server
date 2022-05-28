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
    ISLE_OF_FRANCE,
    NORMANDY,
    NEW_AQUITAINE,
    OCCITANIA,
    LOIRE_COUNTRIES,
    PROVENCE_ALPS_FRENCH_RIVIERA,
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
    public String getConditionalName() {
        switch (this) {
            case BURGUNDY_FREE_COUNTY: return "Bourgogne-Franche-Comté";
            case GREAT_EAST: return "Grand Est";
            case UPPER_FRANCE: return "Hauts-de-France";
            case ISLE_OF_FRANCE: return "Île-de-France";
            case NEW_AQUITAINE: return "Nouvelle-Aquitaine";
            case LOIRE_COUNTRIES: return "Pays de la Loire";
            case PROVENCE_ALPS_FRENCH_RIVIERA: return "Provence-Alpes-Côte d'Azur";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AUVERGNE_RHONE_ALPES: return "Auvergne-Rhône-Alps";
            case BURGUNDY_FREE_COUNTY: return "Burgundy-Free County";
            case CENTRE_LOIRE_VALLEY: return "Centre-Loire Valley";
            case PROVENCE_ALPS_FRENCH_RIVIERA: return "Provence-Alps-French Riviera";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BRITTANY:
            case NORMANDY:
            case OCCITANIA:
                return "(administrative region)";
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
