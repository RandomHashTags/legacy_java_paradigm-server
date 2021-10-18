package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public enum WLCountry {
    ABKHAZIA,
    AFGHANISTAN,
    ALBANIA,
    ALGERIA,
    AMERICAN_SAMOA,
    ANDORRA,
    ANGOLA,
    ANGUILLA,
    ANTIGUA_AND_BARBUDA,
    ARGENTINA,
    ARMENIA,
    ARUBA,
    ARTSAKH,
    AUSTRALIA,
    AUSTRIA,
    AZERBAIJAN,

    BAHAMAS,
    BAHRAIN,
    BANGLADESH,
    BARBADOS,
    BELARUS,
    BELGIUM,
    BELIZE,
    BENIN,
    BERMUDA,
    BHUTAN,
    BOLIVIA,
    BOSNIA_AND_HERZEGOVINA,
    BOTSWANA,
    BRAZIL,
    BRITISH_VIRGIN_ISLANDS,
    BRUNEI,
    BULGARIA,
    BURKINA_FASO,
    BURUNDI,

    CAMBODIA,
    CAMEROON,
    CANADA,
    CAPE_VERDE,
    CAYMAN_ISLANDS,
    CENTRAL_AFRICAN_REPUBLIC,
    CHAD,
    CHILE,
    CHINA,
    COLOMBIA,
    COMOROS,
    COOK_ISLANDS,
    COSTA_RICA,
    IVORY_COAST("Côte d'Ivoire"),
    CROATIA,
    CUBA,
    CYPRUS,
    CZECH_REPUBLIC,

    DEMOCRATIC_REPUBLIC_OF_THE_CONGO,
    DENMARK,
    DJIBOUTI,
    DOMINICA,
    DOMINICAN_REPUBLIC,

    ECUADOR,
    EGYPT,
    EL_SALVADOR,
    ERITREA,
    ESTONIA,
    ESWATINI("Swaziland"),
    ETHIOPIA,

    FALKLAND_ISLANDS,
    FAROE_ISLANDS,
    FIJI,
    FINLAND,
    FRANCE,

    GABON,
    GAMBIA,
    GEORGIA,
    GERMANY,
    GHANA,
    GIBRALTAR,
    GREECE,
    GREENLAND,
    GRENADA,
    GUADELOUPE,
    GUAM,
    GUATEMALA,
    GUERNSEY,
    GUINEA,
    GUINEA_BISSAU("Guinea-Bissau"),
    GUYANA,

    HAITI,
    HONDURAS,
    HONG_KONG,
    HUNGARY,

    ICELAND,
    INDIA,
    INDONESIA,
    IRAN,
    IRAQ,
    IRELAND,
    ISRAEL,
    ITALY,

    JAMAICA,
    JAPAN,
    JERSEY,
    JORDAN,

    KAZAKHSTAN,
    KENYA,
    KIRIBATI,
    KOSOVO,
    KUWAIT,
    KYRGYZSTAN,

    LAOS,
    LATVIA,
    LEBANON,
    LESOTHO,
    LIBERIA,
    LIBYA,
    LIECHTENSTEIN,
    LITHUANIA,
    LUXEMBOURG,

    MACAU,
    MADAGASCAR,
    MALAWI,
    MALAYSIA,
    MALDIVES,
    MALI,
    MALTA,
    MARSHALL_ISLANDS,
    MAURITANIA,
    MAURITIUS,
    MEXICO,
    MICRONESIA,
    MOLDOVA,
    MONACO,
    MONGOLIA,
    MONTENEGRO,
    MONTSERRAT,
    MOROCCO,
    MOZAMBIQUE,
    MYANMAR("Burma"),

    NAMIBIA,
    NAURU,
    NEPAL,
    NETHERLANDS,
    NEW_ZEALAND,
    NICARAGUA,
    NIGER,
    NIGERIA,
    NIUE,
    NORFOLK_ISLAND,
    NORTH_KOREA,
    NORTH_MACEDONIA,
    NORTHERN_MARIANA_ISLANDS,
    NORTHERN_CYPRUS,
    NORWAY,

    OMAN,

    PAKISTAN,
    PALAU,
    PALESTINE,
    PANAMA,
    PAPUA_NEW_GUINEA,
    PARAGUAY,
    PERU,
    PHILIPPINES,
    POLAND,
    PORTUGAL,

    QATAR,

    REPUBLIC_OF_THE_CONGO,
    ROMANIA,
    RUSSIA,
    RWANDA,

    SAINT_BARTHELEMY("Saint Barthélemy"),
    SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA("Saint Helena, Ascension and Tristan da Cunha"),
    SAINT_KITTS_AND_NEVIS,
    SAINT_LUCIA,
    SAINT_MARTIN,
    SAINT_PIERRE_AND_MIQUELON,
    SAINT_VINCENT_AND_THE_GRENADINES,

    SAMOA,
    SAN_MARINO,
    SAO_TOME_AND_PRINCIPE("São Tomé and Príncipe"),
    SAUDI_ARABIA,
    SCOTLAND,
    SENEGAL,
    SERBIA,
    SEYCHELLES,
    SIERRA_LEONE,
    SINGAPORE,
    SLOVAKIA,
    SLOVENIA,
    SOLOMON_ISLANDS,
    SOMALIA,
    SOMALILAND,
    SOUTH_AFRICA,
    SOUTH_KOREA,
    SOUTH_SUDAN,
    SPAIN,
    SRI_LANKA,
    SUDAN,
    SURINAME,
    SWEDEN,
    SWITZERLAND,
    SYRIA,

    TAIWAN,
    TAJIKISTAN,
    TANZANIA,
    THAILAND,
    TIMOR_LESTE(
            "East Timor"
    ),
    TOGO,
    TOKELAU,
    TONGA,
    TRANSNISTRIA,
    TRINIDAD_AND_TOBAGO,
    TUNISIA,
    TURKEY,
    TURKMENISTAN,
    TURKS_AND_CAICOS_ISLANDS,
    TUVALU,

    UGANDA,
    UKRAINE,
    UNITED_ARAB_EMIRATES,
    UNITED_KINGDOM,
    UNITED_STATES,
    URUGUAY,
    UZBEKISTAN,

    VANUATU,
    VATICAN_CITY,
    VENEZUELA,
    VIETNAM,

    WALLIS_AND_FUTUNA,
    WESTERN_SAHARA,

    YEMEN,

    ZAMBIA,
    ZIMBABWE,


    PUERTO_RICO,
    ;

    private final HashSet<String> aliases;

    WLCountry(String...aliases) {
        this.aliases =  aliases != null ? new HashSet<>(Arrays.asList(aliases)) : null;
    }

    public static WLCountry valueOfBackendID(String backendID) {
        final WLCountry[] countries = values();
        final Stream<WLCountry> test = Arrays.stream(countries).parallel().filter(country -> {
            if(backendID.equalsIgnoreCase(country.getBackendID())) {
                return true;
            }
            final HashSet<String> aliases = country.getAliases();
            if(aliases != null) {
                for(String alias : country.getAliases()) {
                    if(backendID.equalsIgnoreCase(alias.replace(" ", ""))) {
                        return true;
                    }
                }
            }
            return false;
        });
        return test.findFirst().orElse(null);
    }

    public String getBackendID() {
        return name().toLowerCase().replace("_", "");
    }
    public String getShortName() {
        return LocalServer.toCorrectCapitalization(name(), "and", "the", "da", "of");
    }
    public HashSet<String> getAliases() {
        return aliases;
    }

    public String getGovernmentWebsite() {
        return WLGovernmentWebsite.INSTANCE.get(this);
    }

    public WLConstitution getConstitution() {
        return WLConstitution.get(this);
    }
    public String getISOAlpha2() {
        return WLCountryISOAlpha2.INSTANCE.get(this);
    }
    public static WLCountry valueOfISOAlpha2(String isoAlpha2) {
        final WLCountry[] countries = WLCountry.values();
        final Stream<WLCountry> target = Arrays.stream(countries).filter(country -> {
            final String alpha2 = country.getISOAlpha2();
            return alpha2 != null && alpha2.equalsIgnoreCase(isoAlpha2);
        });
        return target.findFirst().orElse(null);
    }

    public String getFlagEmoji() {
        return WLFlagEmoji.INSTANCE.get(this);
    }
    public SovereignStateSubdivision[] getSubdivisions() {
        return WLSubdivisions.INSTANCE.get(this);
    }
    public SovereignStateSubdivision valueOfSovereignStateSubdivision(String name) {
        if(name != null) {
            final SovereignStateSubdivision[] subdivisions = getSubdivisions();
            if(subdivisions != null) {
                for(SovereignStateSubdivision subdivision : subdivisions) {
                    if(name.equalsIgnoreCase(subdivision.name()) || name.equalsIgnoreCase(subdivision.getBackendID()) || name.equalsIgnoreCase(subdivision.getName()) || name.equalsIgnoreCase(subdivision.getPostalCodeAbbreviation())) {
                        return subdivision;
                    }
                }
            }
        }
        return null;
    }

    public WLTimeZone[] getTimeZones() {
        return WLTimeZone.getFromCountry(this);
    }
}