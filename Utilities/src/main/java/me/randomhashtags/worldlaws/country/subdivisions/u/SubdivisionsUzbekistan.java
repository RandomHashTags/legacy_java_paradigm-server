package me.randomhashtags.worldlaws.country.subdivisions.u;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsUzbekistan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Uzbekistan
    ANDIJAN_REGION,
    BUKHARA_REGION,
    FERGANA_REGION,
    JIZZAKH_REGION,
    KARAKALPAKSTAN,
    NAMANGAN_REGION,
    NAVOIY_REGION,
    QASHQADARYO_REGION,
    SAMARQAND_REGION,
    SIRDARYO_REGION,
    SURXONDARYO_REGION,
    TASHKENT,
    TASHKENT_REGION,
    XORAZM_REGION,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UZBEKISTAN;
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
