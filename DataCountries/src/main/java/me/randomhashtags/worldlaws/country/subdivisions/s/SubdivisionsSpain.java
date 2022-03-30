package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsSpain implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Autonomous_communities_of_Spain
    ANDALUSIA,
    ARAGON,
    ASTURIAS,
    BALEARIC_ISLANDS,
    BASQUE_COUNTRY,
    CANARY_ISLANDS,
    CANTABRIA,
    CASTILE_AND_LEON,
    CASTILLA_LA_MANCHA,
    CATALONIA,
    CEUTA,
    COMMUNITY_OF_MADRID,
    EXTREMADURA,
    GALICIA,
    LA_RIOJA,
    MELILLA,
    NAVARRE,
    REGION_OF_MURCIA,
    VALENCIAN_COMMUNITY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SPAIN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.AUTONOMOUS_COMMUNITIES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case CEUTA:
            case MELILLA:
                return SubdivisionType.AUTONOMOUS_CITIES;
            default:
                return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case CASTILLA_LA_MANCHA: return "Castilla-La Mancha";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BASQUE_COUNTRY: return "_(autonomous_community)";
            case GALICIA: return "_(Spain)";
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
