package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsUkraine implements SovereignStateSubdivision {
    CRIMERA,

    CHERKASY,
    CHERNIHIV,
    DNIPROPETROVSK,
    DONETSK,
    IVANO_FRANKISVK, // Ivano-Frankivsk
    KHARKIV,
    KHERSON,
    KHMELNYTSKYI,
    KIROVOHRAD,
    LUHANSK,
    LVIV,
    MYKOLAIV,
    ODESSA,
    POLTAVA,
    RIVNE,
    SUMY,
    TERNOPIL,
    VINNYTSIA,
    VOLYN,
    ZAKARPATTIA,
    ZAPORIZHZHIA,
    ZHYTOMYR,

    KYIV,
    SEVASTOPOL,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UKRAINE;
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
