package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsTonga implements SovereignStateSubdivision {
    TONGATAPU,
    VAVA_U, // Vava'u
    HA_APAI, // Ha'apai
    _EUA, // 'Eua
    ONGO_NIUA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.TONGA;
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
