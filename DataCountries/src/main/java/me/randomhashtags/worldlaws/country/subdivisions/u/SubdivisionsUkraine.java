package me.randomhashtags.worldlaws.country.subdivisions.u;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsUkraine implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Ukraine
    CRIMERA,

    CHERKASY,
    CHERNIHIV,
    DNIPROPETROVSK,
    DONETSK,
    IVANO_FRANKISVK,
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.OBLASTS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case CRIMERA:
                return SubdivisionType.AUTONOMOUS_REPUBLICS;
            case KYIV:
            case SEVASTOPOL:
                return SubdivisionType.SPECIAL_CITIES;
            default:
                return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case CRIMERA:
                return "Autonomous Republic of Crimea";
            default:
                return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case IVANO_FRANKISVK: return "Ivano-Frankivsk";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case KYIV:
            case SEVASTOPOL:
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
