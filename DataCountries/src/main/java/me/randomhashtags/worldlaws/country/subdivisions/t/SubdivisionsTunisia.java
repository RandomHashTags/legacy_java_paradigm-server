package me.randomhashtags.worldlaws.country.subdivisions.t;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsTunisia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Governorates_of_Tunisia
    ARIANA,
    BEJA,
    BEN_AROUS,
    BIZERTE,
    GABES,
    GAFSA,
    JENDOUBA,
    KAIROUAN,
    KASSERINE,
    KEBILI,
    KEF,
    MAHDIA,
    MANOUBA,
    MEDENINE,
    MONASTIR,
    NABEUL,
    SFAX,
    SIDI_BOUZID,
    SILIANA,
    SOUSSE,
    TATAOUINE,
    TOZEUR,
    TUNIS,
    ZAGHOUAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.TUNISIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.GOVERNORATES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BEJA: return "Béja";
            case GABES: return "Gabès";
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
}
