package me.randomhashtags.worldlaws.country.subdivisions.u;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsUzbekistan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Uzbekistan
    ANDIJAN,
    BUKHARA,
    FERGANA,
    JIZZAKH,
    KARAKALPAKSTAN,
    NAMANGAN,
    NAVOIY,
    QASHQADARYO,
    SAMARQAND,
    SIRDARYO,
    SURXONDARYO,
    TASHKENT,
    TASHKENT_REGION,
    XORAZM,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UZBEKISTAN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case KARAKALPAKSTAN: return SubdivisionType.AUTONOMOUS_REPUBLICS;
            case TASHKENT: return SubdivisionType.INDEPENDENT_CITIES;
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case KARAKALPAKSTAN:
            case TASHKENT:
            case TASHKENT_REGION:
                return "";
            default:
                return null;
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
