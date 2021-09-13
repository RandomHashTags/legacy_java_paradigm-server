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
            case AUSTRALIA: return SubdivisionsAustralia.values();
            case CANADA: return SubdivisionsCanada.values();
            case ITALY: return SubdivisionsItaly.values();
            case MEXICO: return SubdivisionsMexico.values();
            case NEW_ZEALAND: return SubdivisionsNewZealand.values();
            case UNITED_STATES: return SubdivisionsUnitedStates.values();
            case VANUATU: return SubdivisionsVanuatu.values();
            default: return null;
        }
    }
}
