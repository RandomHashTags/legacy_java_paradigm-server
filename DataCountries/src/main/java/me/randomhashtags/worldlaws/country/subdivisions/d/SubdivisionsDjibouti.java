package me.randomhashtags.worldlaws.country.subdivisions.d;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsDjibouti implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Djibouti
    ALI_SABIEH,
    ARTA,
    DIKHIL,
    DJBOUTI,
    OBOCK,
    TADJOURAH,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.DJIBOUTI;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
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
