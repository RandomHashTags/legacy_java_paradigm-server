package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case ISLAMABAD_CAPITAL_TERRITORY: return SubdivisionType.FEDERAL_TERRITORIES;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case AZAD_JAMMU_AND_KASHMIR: return "Azad Kashmir";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AZAD_JAMMU_AND_KASHMIR: return "Azad Jammu and Kashmir";
            case GILGIT_BALTISTAN: return "Gilgit-Baltistan";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BALOCHISTAN:
            case PUNJAB:
                return ",_Pakistan";
            default:
                return "";
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
