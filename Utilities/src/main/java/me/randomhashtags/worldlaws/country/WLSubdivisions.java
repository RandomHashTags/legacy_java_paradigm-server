package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.country.subdivisions.a.SubdivisionsAlgeria;
import me.randomhashtags.worldlaws.country.subdivisions.a.SubdivisionsArgentina;
import me.randomhashtags.worldlaws.country.subdivisions.a.SubdivisionsAustralia;
import me.randomhashtags.worldlaws.country.subdivisions.a.SubdivisionsAzerbaijan;
import me.randomhashtags.worldlaws.country.subdivisions.b.SubdivisionsBelarus;
import me.randomhashtags.worldlaws.country.subdivisions.b.SubdivisionsBrazil;
import me.randomhashtags.worldlaws.country.subdivisions.b.SubdivisionsBulgaria;
import me.randomhashtags.worldlaws.country.subdivisions.c.SubdivisionsCanada;
import me.randomhashtags.worldlaws.country.subdivisions.c.SubdivisionsChile;
import me.randomhashtags.worldlaws.country.subdivisions.c.SubdivisionsCostaRica;
import me.randomhashtags.worldlaws.country.subdivisions.c.SubdivisionsCuba;
import me.randomhashtags.worldlaws.country.subdivisions.d.SubdivisionsDenmark;
import me.randomhashtags.worldlaws.country.subdivisions.d.SubdivisionsDjibouti;
import me.randomhashtags.worldlaws.country.subdivisions.d.SubdivisionsDominica;
import me.randomhashtags.worldlaws.country.subdivisions.d.SubdivisionsDominicanRepublic;
import me.randomhashtags.worldlaws.country.subdivisions.e.SubdivisionsEcuador;
import me.randomhashtags.worldlaws.country.subdivisions.e.SubdivisionsEgypt;
import me.randomhashtags.worldlaws.country.subdivisions.e.SubdivisionsEthiopia;
import me.randomhashtags.worldlaws.country.subdivisions.f.SubdivisionsFiji;
import me.randomhashtags.worldlaws.country.subdivisions.f.SubdivisionsFrance;
import me.randomhashtags.worldlaws.country.subdivisions.g.SubdivisionsGermany;
import me.randomhashtags.worldlaws.country.subdivisions.g.SubdivisionsGreece;
import me.randomhashtags.worldlaws.country.subdivisions.g.SubdivisionsGuadeloupe;
import me.randomhashtags.worldlaws.country.subdivisions.g.SubdivisionsGuatemala;
import me.randomhashtags.worldlaws.country.subdivisions.h.SubdivisionsHaiti;
import me.randomhashtags.worldlaws.country.subdivisions.h.SubdivisionsHonduras;
import me.randomhashtags.worldlaws.country.subdivisions.h.SubdivisionsHungary;
import me.randomhashtags.worldlaws.country.subdivisions.i.*;
import me.randomhashtags.worldlaws.country.subdivisions.j.SubdivisionsJamaica;
import me.randomhashtags.worldlaws.country.subdivisions.j.SubdivisionsJapan;
import me.randomhashtags.worldlaws.country.subdivisions.j.SubdivisionsJordan;
import me.randomhashtags.worldlaws.country.subdivisions.k.SubdivisionsKazakhstan;
import me.randomhashtags.worldlaws.country.subdivisions.l.SubdivisionsLibya;
import me.randomhashtags.worldlaws.country.subdivisions.l.SubdivisionsLithuania;
import me.randomhashtags.worldlaws.country.subdivisions.m.SubdivisionsMadagascar;
import me.randomhashtags.worldlaws.country.subdivisions.m.SubdivisionsMalaysia;
import me.randomhashtags.worldlaws.country.subdivisions.m.SubdivisionsMexico;
import me.randomhashtags.worldlaws.country.subdivisions.m.SubdivisionsMontenegro;
import me.randomhashtags.worldlaws.country.subdivisions.n.*;
import me.randomhashtags.worldlaws.country.subdivisions.p.*;
import me.randomhashtags.worldlaws.country.subdivisions.r.SubdivisionsRomania;
import me.randomhashtags.worldlaws.country.subdivisions.s.SubdivisionsSenegal;
import me.randomhashtags.worldlaws.country.subdivisions.s.SubdivisionsSouthKorea;
import me.randomhashtags.worldlaws.country.subdivisions.s.SubdivisionsSwitzerland;
import me.randomhashtags.worldlaws.country.subdivisions.t.SubdivisionsTonga;
import me.randomhashtags.worldlaws.country.subdivisions.t.SubdivisionsTunisia;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUkraine;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUzbekistan;
import me.randomhashtags.worldlaws.country.subdivisions.v.SubdivisionsVanuatu;

import java.util.HashSet;

public enum WLSubdivisions {
    ;

    private static final HashSet<WLCountry> SUPPORTED_COUNTRIES = loadSupportedCountries();

    public static HashSet<WLCountry> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }
    private static HashSet<WLCountry> loadSupportedCountries() {
        final HashSet<WLCountry> countries = new HashSet<>();
        for(WLCountry country : WLCountry.values()) {
            if(get(country) != null) {
                countries.add(country);
            }
        }
        return countries;
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

            case BELARUS: return SubdivisionsBelarus.values();
            case BRAZIL: return SubdivisionsBrazil.values();
            case BULGARIA: return SubdivisionsBulgaria.values();

            case CANADA: return SubdivisionsCanada.values();
            case CHILE: return SubdivisionsChile.values();
            case COSTA_RICA: return SubdivisionsCostaRica.values();
            case CUBA: return SubdivisionsCuba.values();

            case DENMARK: return SubdivisionsDenmark.values();
            case DJIBOUTI: return SubdivisionsDjibouti.values();
            case DOMINICA: return SubdivisionsDominica.values();
            case DOMINICAN_REPUBLIC: return SubdivisionsDominicanRepublic.values();

            case ECUADOR: return SubdivisionsEcuador.values();
            case EGYPT: return SubdivisionsEgypt.values();
            case ETHIOPIA: return SubdivisionsEthiopia.values();

            case FIJI: return SubdivisionsFiji.values();
            case FRANCE: return SubdivisionsFrance.values();

            case GERMANY: return SubdivisionsGermany.values();
            case GREECE: return SubdivisionsGreece.values();
            case GUADELOUPE: return SubdivisionsGuadeloupe.values();
            case GUATEMALA: return SubdivisionsGuatemala.values();

            case HAITI: return SubdivisionsHaiti.values();
            case HONDURAS: return SubdivisionsHonduras.values();
            case HUNGARY: return SubdivisionsHungary.values();

            case INDIA: return SubdivisionsIndia.values();
            case INDONESIA: return SubdivisionsIndonesia.values();
            case IRAN: return SubdivisionsIran.values();
            case IRELAND: return SubdivisionsIreland.values();
            case ITALY: return SubdivisionsItaly.values();

            case JAMAICA: return SubdivisionsJamaica.values();
            case JAPAN: return SubdivisionsJapan.values();
            case JORDAN: return SubdivisionsJordan.values();

            case KAZAKHSTAN: return SubdivisionsKazakhstan.values();

            case LIBYA: return SubdivisionsLibya.values();
            case LITHUANIA: return SubdivisionsLithuania.values();

            case MADAGASCAR: return SubdivisionsMadagascar.values();
            case MALAYSIA: return SubdivisionsMalaysia.values();
            case MEXICO: return SubdivisionsMexico.values();
            case MONTENEGRO: return SubdivisionsMontenegro.values();

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

            case SENEGAL: return SubdivisionsSenegal.values();
            case SOUTH_KOREA: return SubdivisionsSouthKorea.values();
            case SWITZERLAND: return SubdivisionsSwitzerland.values();

            case TONGA: return SubdivisionsTonga.values();
            case TUNISIA: return SubdivisionsTunisia.values();

            case UKRAINE: return SubdivisionsUkraine.values();
            case UNITED_STATES: return SubdivisionsUnitedStates.values();
            case UZBEKISTAN: return SubdivisionsUzbekistan.values();

            case VANUATU: return SubdivisionsVanuatu.values();
            default: return null;
        }
    }
}
