package me.randomhashtags.worldlaws.location.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.location.WLCountry;

public enum VanuatuSubdivisions implements SovereignStateSubdivision {
    MALAMPA,
    PENAMA,
    SANMA,
    SHEFA,
    TAFEA,
    TORBA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.VANUATU;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            case MALAMPA: return "MAP";
            case PENAMA: return "PAM";
            case SANMA: return "SAM";
            case SHEFA: return "SEE";
            case TAFEA: return "TAE";
            case TORBA: return "TOB";
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentURL() {
        return null;
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
