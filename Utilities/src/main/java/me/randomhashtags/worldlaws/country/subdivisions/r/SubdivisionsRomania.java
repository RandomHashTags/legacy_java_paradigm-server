package me.randomhashtags.worldlaws.country.subdivisions.r;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsRomania implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Romania
    ALBA,
    ARAD,
    ARGES,
    BACAU,
    BIHOR,
    BISTRITA_NASAUD,
    BOTOSANI,
    BRAILA,
    BRASOV,
    BUCHAREST,
    BUZAU,
    CALARASI,
    CARAS_SEVERIN,
    CLUJ,
    CONSTANTA,
    COVASNA,
    DAMBOVITA,
    DOLJ,
    GALATI,
    GIURGIU,
    GORJ,
    HARGHITA,
    HUNEDOARA,
    IALOMITA,
    IASI,
    ILFOV,
    MARAMURES,
    MEHEDINTI,
    MURES,
    NEAMT,
    OLT,
    PRAHOVA,
    SALAJ,
    SATU_MARE,
    SIBIU,
    SUCEAVA,
    TELEORMAN,
    TIMIS,
    TULCEA,
    VALCEA,
    VASLUI,
    VRANCEA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ROMANIA;
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
