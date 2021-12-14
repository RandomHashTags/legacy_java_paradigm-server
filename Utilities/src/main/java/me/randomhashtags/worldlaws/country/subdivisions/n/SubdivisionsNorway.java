package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsNorway implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Norway
    AGDER,
    INNLANDET,
    MORE_OG_ROMSDAL,
    NORDLAND,
    OSLO,
    ROGALAND,
    TROMS_OG_FINNMARK,
    TRONDELAG,
    VESTFOLD_OF_TELEMARK,
    VESTLAND,
    VIKEN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NORWAY;
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
