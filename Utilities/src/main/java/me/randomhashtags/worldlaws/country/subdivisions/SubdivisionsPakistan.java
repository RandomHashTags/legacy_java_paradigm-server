package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsPakistan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_units_of_Pakistan
    AZAD_JAMMU_AND_KASHMIR,
    BALOCHISTAN,
    GILGIT_BALTISTAN,
    ISLAMABAD_CAPITAL_TERRITORY,
    KHYBER_PAKHTUNKHWA,
    PUNJAB,
    SINDH,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PAKISTAN;
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
}
