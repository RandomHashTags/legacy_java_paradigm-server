package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.country.subdivisions.*;

import java.util.HashSet;

public enum WLSubdivisions {
    ;

    private static HashSet<WLCountry> SUPPORTED_COUNTRIES;

    public static HashSet<WLCountry> getSupportedCountries() {
        if(SUPPORTED_COUNTRIES == null) {
            SUPPORTED_COUNTRIES = new HashSet<>();
            for(WLCountry country : WLCountry.values()) {
                if(get(country) != null) {
                    SUPPORTED_COUNTRIES.add(country);
                }
            }
        }
        return SUPPORTED_COUNTRIES;
    }

    public static SovereignStateSubdivision valueOfString(String string) {
        for(WLCountry country : getSupportedCountries()) {
            final SovereignStateSubdivision subdivision = country.valueOfSovereignStateSubdivision(string);
            if(subdivision != null) {
                return subdivision;
            }
        }
        return null;
    }

    public static SovereignStateSubdivision[] get(WLCountry country) {
        switch (country) {
            case ALGERIA: return SubdivisionsAlgeria.values();
            case ARGENTINA: return SubdivisionsArgentina.values();
            case AUSTRALIA: return SubdivisionsAustralia.values();
            case AZERBAIJAN: return SubdivisionsAzerbaijan.values();
            case BRAZIL: return SubdivisionsBrazil.values();
            case BULGARIA: return SubdivisionsBulgaria.values();
            case CANADA: return SubdivisionsCanada.values();
            case CHILE: return SubdivisionsChile.values();
            case COSTA_RICA: return SubdivisionsCostaRica.values();
            case DOMINICAN_REPUBLIC: return SubdivisionsDominicanRepublic.values();
            case EGYPT: return SubdivisionsEgypt.values();
            case FIJI: return SubdivisionsFiji.values();
            case FRANCE: return SubdivisionsFrance.values();
            case GERMANY: return SubdivisionsGermany.values();
            case GREECE: return SubdivisionsGreece.values();
            case HAITI: return SubdivisionsHaiti.values();
            case INDIA: return SubdivisionsIndia.values();
            case INDONESIA: return SubdivisionsIndonesia.values();
            case IRAN: return SubdivisionsIran.values();
            case IRELAND: return SubdivisionsIreland.values();
            case ITALY: return SubdivisionsItaly.values();
            case JAMAICA: return SubdivisionsJamaica.values();
            case JAPAN: return SubdivisionsJapan.values();
            case LIBYA: return SubdivisionsLibya.values();
            case MADAGASCAR: return SubdivisionsMadagascar.values();
            case MEXICO: return SubdivisionsMexico.values();
            case NEPAL: return SubdivisionsNepal.values();
            case NEW_ZEALAND: return SubdivisionsNewZealand.values();
            case NICARAGUA: return SubdivisionsNicaragua.values();
            case NIGERIA: return SubdivisionsNigeria.values();
            case NORWAY: return SubdivisionsNorway.values();
            case PAKISTAN: return SubdivisionsPakistan.values();
            case PANAMA: return SubdivisionsPanama.values();
            case PAPUA_NEW_GUINEA: return SubdivisionsPapuaNewGuinea.values();
            case PERU: return SubdivisionsPeru.values();
            case POLAND: return SubdivisionsPoland.values();
            case ROMANIA: return SubdivisionsRomania.values();
            case SWITZERLAND: return SubdivisionsSwitzerland.values();
            case TONGA: return SubdivisionsTonga.values();
            case UKRAINE: return SubdivisionsUkraine.values();
            case UNITED_STATES: return SubdivisionsUnitedStates.values();
            case UZBEKISTAN: return SubdivisionsUzbekistan.values();
            case VANUATU: return SubdivisionsVanuatu.values();
            default: return null;
        }
    }
}
