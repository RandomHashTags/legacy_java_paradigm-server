package me.randomhashtags.worldlaws.country.subdivisions.l;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsLibya implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Districts_of_Libya
    AL_BUTNAN,
    AL_JABAL_AL_AKHDAR,
    AL_JABAL_AL_GHARBI,
    AL_JAFARAH,
    AL_JUFRAH,
    AL_KUFRAH,
    AL_MARJ,
    AL_WAHAT,
    AN_NUQAT_AL_KHAMS,
    AZ_ZAWIYAH,
    BANGHAZI,
    DARNAH,
    MARQAB,
    MISRATAH,
    MURZUQ,
    NALUT,
    SABHA,
    SURT,
    TARABULUS,
    WADI_AL_HAYAT,
    WADI_ASH_SHATI,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.LIBYA;
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
