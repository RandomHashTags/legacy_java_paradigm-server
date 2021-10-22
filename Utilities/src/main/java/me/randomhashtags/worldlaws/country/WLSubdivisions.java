package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.country.subdivisions.*;

public enum WLSubdivisions {
    INSTANCE;

    private WLCountry[] getSupportedCountries() {
        return new WLCountry[] {
                WLCountry.ARGENTINA,
                WLCountry.AUSTRALIA,
                WLCountry.AZERBAIJAN,
                WLCountry.BRAZIL,
                WLCountry.CANADA,
                WLCountry.CHILE,
                WLCountry.COSTA_RICA,
                WLCountry.DOMINICAN_REPUBLIC,
                WLCountry.EGYPT,
                WLCountry.FIJI,
                WLCountry.GERMANY,
                WLCountry.GREECE,
                WLCountry.INDIA,
                WLCountry.INDONESIA,
                WLCountry.IRAN,
                WLCountry.IRELAND,
                WLCountry.ITALY,
                WLCountry.JAPAN,
                WLCountry.MADAGASCAR,
                WLCountry.MEXICO,
                WLCountry.NEPAL,
                WLCountry.NEW_ZEALAND,
                WLCountry.NICARAGUA,
                WLCountry.PAKISTAN,
                WLCountry.PANAMA,
                WLCountry.PAPUA_NEW_GUINEA,
                WLCountry.PERU,
                WLCountry.POLAND,
                WLCountry.TONGA,
                WLCountry.UKRAINE,
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
            case ARGENTINA: return SubdivisionsArgentina.values();
            case AUSTRALIA: return SubdivisionsAustralia.values();
            case AZERBAIJAN: return SubdivisionsAzerbaijan.values();
            case BRAZIL: return SubdivisionsBrazil.values();
            case CANADA: return SubdivisionsCanada.values();
            case CHILE: return SubdivisionsChile.values();
            case COSTA_RICA: return SubdivisionsCostaRica.values();
            case DOMINICAN_REPUBLIC: return SubdivisionsDominicanRepublic.values();
            case EGYPT: return SubdivisionsEgypt.values();
            case FIJI: return SubdivisionsFiji.values();
            case GERMANY: return SubdivisionsGermany.values();
            case GREECE: return SubdivisionsGreece.values();
            case INDIA: return SubdivisionsIndia.values();
            case INDONESIA: return SubdivisionsIndonesia.values();
            case IRAN: return SubdivisionsIran.values();
            case IRELAND: return SubdivisionsIreland.values();
            case ITALY: return SubdivisionsItaly.values();
            case JAPAN: return SubdivisionsJapan.values();
            case MADAGASCAR: return SubdivisionsMadagascar.values();
            case MEXICO: return SubdivisionsMexico.values();
            case NEPAL: return SubdivisionsNepal.values();
            case NEW_ZEALAND: return SubdivisionsNewZealand.values();
            case NICARAGUA: return SubdivisionsNicaragua.values();
            case PAKISTAN: return SubdivisionsPakistan.values();
            case PANAMA: return SubdivisionsPanama.values();
            case PAPUA_NEW_GUINEA: return SubdivisionsPapuaNewGuinea.values();
            case PERU: return SubdivisionsPeru.values();
            case POLAND: return SubdivisionsPoland.values();
            case TONGA: return SubdivisionsTonga.values();
            case UKRAINE: return SubdivisionsUkraine.values();
            case UNITED_STATES: return SubdivisionsUnitedStates.values();
            case VANUATU: return SubdivisionsVanuatu.values();
            default: return null;
        }
    }
}
