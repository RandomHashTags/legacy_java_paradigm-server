package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.location.subdivisions.*;

public enum WLSubdivisions {
    INSTANCE;

    public SovereignStateSubdivision[] get(WLCountry country) {
        switch (country) {
            case AUSTRALIA: return AustraliaSubdivisions.values();
            case CANADA: return CanadaSubdivisions.values();
            case MEXICO: return MexicoSubdivisions.values();
            case NEW_ZEALAND: return NewZealandSubdivisions.values();
            case UNITED_STATES: return UnitedStatesSubdivisions.values();
            case VANUATU: return VanuatuSubdivisions.values();
            default: return null;
        }
    }
}
