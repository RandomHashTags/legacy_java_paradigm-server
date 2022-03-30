package me.randomhashtags.worldlaws.country.subdivisions.q;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsQatar implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Municipalities_of_Qatar
    AD_DAWHAH,
    AL_DAAYEN,
    AL_KHOR,
    AL_RAYYAN,
    AL_SHAMAL,
    AL_WAKRAH,
    AL_SHAHANIYA,
    UMM_SALAL,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.QATAR;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.MUNICIPALITIES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AD_DAWHAH: return "Ad-Dawhah";
            case AL_SHAHANIYA: return "Al-Shahanya";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case AD_DAWHAH: return "_(municipality)";
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

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
