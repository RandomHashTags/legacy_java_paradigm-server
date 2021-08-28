package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.country.subdivisions.*;

public enum WLSubdivisions {
    INSTANCE;

    public WLCountry[] getSupportedCountries() {
        return new WLCountry[] {
                WLCountry.AUSTRALIA,
                WLCountry.CANADA,
                WLCountry.ITALY,
                WLCountry.MEXICO,
                WLCountry.NEW_ZEALAND,
                WLCountry.UNITED_STATES,
                WLCountry.VANUATU
        };
    }

    public SovereignStateSubdivision valueOfString(String string) {
        for(WLCountry country : getSupportedCountries()) {
            final SovereignStateSubdivision subdivision = country.valueOfSovereignStateSubdivision(string);
            if(subdivision != null) {
                return subdivision;
            }
        }
        return null;
    }

    public SovereignStateSubdivision[] get(WLCountry country) {
        switch (country) {
            case AUSTRALIA: return AustraliaSubdivisions.values();
            case CANADA: return CanadaSubdivisions.values();
            case ITALY: return ItalySubdivisions.values();
            case MEXICO: return MexicoSubdivisions.values();
            case NEW_ZEALAND: return NewZealandSubdivisions.values();
            case UNITED_STATES: return UnitedStatesSubdivisions.values();
            case VANUATU: return VanuatuSubdivisions.values();
            default: return null;
        }
    }
}
