package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public enum WLCountry {
    ABKHAZIA,
    AFGHANISTAN(true),
    ALBANIA(true),
    ALGERIA(true),
    AMERICAN_SAMOA,
    ANDORRA(true),
    ANGOLA(true),
    ANGUILLA,
    ANTIGUA_AND_BARBUDA,
    ARGENTINA(true),
    ARMENIA(true),
    ARUBA,
    ARTSAKH,
    AUSTRALIA(true),
    AUSTRIA(true),
    AZERBAIJAN(true),

    BAHAMAS(true),
    BAHRAIN(true),
    BANGLADESH(true),
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
    CANADA(true),
    CAPE_VERDE,
    CAYMAN_ISLANDS,
    CENTRAL_AFRICAN_REPUBLIC,
    CHAD,
    CHILE,
    CHINA(true),
    COLOMBIA,
    COMOROS,
    COOK_ISLANDS,
    COSTA_RICA,
    IVORY_COAST(false, "Côte d'Ivoire"),
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
    ESWATINI(false, "Swaziland"),
    ETHIOPIA,

    FALKLAND_ISLANDS,
    FAROE_ISLANDS,
    FIJI,
    FINLAND(true),
    FRANCE(true),

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
    GUINEA_BISSAU(false, "Guinea-Bissau"),
    GUYANA,

    HAITI,
    HONDURAS,
    HONG_KONG,
    HUNGARY,

    ICELAND,
    INDIA(true),
    INDONESIA(true),
    IRAN(true),
    IRAQ(true),
    IRELAND(true),
    ISRAEL,
    ITALY(true),

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
    MEXICO(true),
    MICRONESIA,
    MOLDOVA,
    MONACO,
    MONGOLIA,
    MONTENEGRO,
    MONTSERRAT,
    MOROCCO,
    MOZAMBIQUE,
    MYANMAR(false, "Burma"),

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

    OMAN(true),

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

    QATAR(true),

    REPUBLIC_OF_THE_CONGO,
    ROMANIA(true),
    RUSSIA(true),
    RWANDA(true),

    SAINT_BARTHELEMY(false, "Saint Barthélemy"),
    SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA(false, "Saint Helena, Ascension and Tristan da Cunha"),
    SAINT_KITTS_AND_NEVIS,
    SAINT_LUCIA,
    SAINT_MARTIN,
    SAINT_PIERRE_AND_MIQUELON,
    SAINT_VINCENT_AND_THE_GRENADINES,

    SAMOA,
    SAN_MARINO,
    SAO_TOME_AND_PRINCIPE(false, "São Tomé and Príncipe"),
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
    TAJIKISTAN(true),
    TANZANIA(true),
    THAILAND(true),
    TIMOR_LESTE(
            false,
            "East Timor"
    ),
    TOGO(true),
    TOKELAU,
    TONGA(true),
    TRANSNISTRIA,
    TRINIDAD_AND_TOBAGO(true),
    TUNISIA(true),
    TURKEY(true),
    TURKMENISTAN(true),
    TURKS_AND_CAICOS_ISLANDS,
    TUVALU(true),

    UGANDA(true),
    UKRAINE(true),
    UNITED_ARAB_EMIRATES(true),
    UNITED_KINGDOM,
    UNITED_STATES(true),
    URUGUAY,
    UZBEKISTAN(true),

    VANUATU(true),
    VATICAN_CITY,
    VENEZUELA(true),
    VIETNAM(true),

    WALLIS_AND_FUTUNA,
    WESTERN_SAHARA,

    YEMEN(true),

    ZAMBIA(true),
    ZIMBABWE(true),


    PUERTO_RICO,
    ;

    private final HashSet<String> aliases;
    private final boolean hasTerritories;

    WLCountry() {
        this(false);
    }
    WLCountry(boolean hasTerritories, String...aliases) {
        this.hasTerritories = hasTerritories;
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
        return name().toLowerCase().replace("_", "").replace("'", "");
    }
    public String getShortName() {
        final String name = name().toLowerCase();
        final StringBuilder shortName = new StringBuilder();
        boolean isFirst = true;
        for(String value : name.split("_")) {
            final boolean shouldBeLowercase = value.equals("and") || value.equals("of") || value.equals("the");
            final String string = shouldBeLowercase ? value : value.substring(0, 1).toUpperCase() + value.substring(1);
            shortName.append(isFirst ? "" : " ").append(string);
            isFirst = false;
        }
        return shortName.toString();
    }
    public HashSet<String> getAliases() {
        return aliases;
    }

    public String getGovernmentWebsite() {
        return WLGovernmentWebsite.INSTANCE.get(this);
    }
    public boolean hasTerritories() {
        return hasTerritories;
    }

    public static WLCountry valueOfAbbreviation(String input) { // https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements
        switch (input.toUpperCase()) {
            case "AF": return WLCountry.AFGHANISTAN;
            //case "AX": return WLCountry.ALAND_ISLANDS;
            case "AL": return WLCountry.ALBANIA;
            case "DZ": return WLCountry.ALGERIA;
            case "AS": return WLCountry.AMERICAN_SAMOA;
            case "AD": return WLCountry.ANDORRA;
            case "AO": return WLCountry.ANGOLA;
            case "AI": return WLCountry.ANGUILLA;
            case "AG": return WLCountry.ANTIGUA_AND_BARBUDA;
            case "AR": return WLCountry.ARGENTINA;
            case "AM": return WLCountry.ARMENIA;
            case "AW": return WLCountry.ARUBA;
            case "AU": return WLCountry.AUSTRALIA;
            case "AT": return WLCountry.AUSTRIA;
            case "AZ": return WLCountry.AZERBAIJAN;
            case "BS": return WLCountry.BAHAMAS;
            case "BH": return WLCountry.BAHRAIN;
            case "BD": return WLCountry.BANGLADESH;
            case "BB": return WLCountry.BARBADOS;
            case "BY": return WLCountry.BELARUS;
            case "BE": return WLCountry.BELGIUM;
            case "BZ": return WLCountry.BELIZE;
            case "BJ": return WLCountry.BENIN;
            case "BM": return WLCountry.BERMUDA;
            case "BT": return WLCountry.BHUTAN;
            case "BO": return WLCountry.BOLIVIA;
            case "BA": return WLCountry.BOSNIA_AND_HERZEGOVINA;
            case "BW": return WLCountry.BOTSWANA;
            case "BR": return WLCountry.BRAZIL;
            case "VG": return WLCountry.BRITISH_VIRGIN_ISLANDS;
            case "BG": return WLCountry.BULGARIA;
            case "BF": return WLCountry.BURKINA_FASO;
            case "BI": return WLCountry.BURUNDI;
            case "KH": return WLCountry.CAMBODIA;
            case "CM": return WLCountry.CAMEROON;
            case "CA": return WLCountry.CANADA;
            case "CV": return WLCountry.CAPE_VERDE;
            case "KY": return WLCountry.CAYMAN_ISLANDS;
            case "CF": return WLCountry.CENTRAL_AFRICAN_REPUBLIC;
            case "TD": return WLCountry.CHAD;
            case "CL": return WLCountry.CHILE;
            case "CN": return WLCountry.CHINA;
            case "CO": return WLCountry.COLOMBIA;
            case "KM": return WLCountry.COMOROS;
            case "CG": return WLCountry.REPUBLIC_OF_THE_CONGO;
            case "CD": return WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO;
            case "CK": return WLCountry.COOK_ISLANDS;
            case "CR": return WLCountry.COSTA_RICA;
            case "CI": return WLCountry.IVORY_COAST;
            case "HR": return WLCountry.CROATIA;
            case "CU": return WLCountry.CUBA;
            //case "CW": return WLCountry.CURACAO;
            case "CY": return WLCountry.CYPRUS;
            case "CZ": return WLCountry.CZECH_REPUBLIC;
            case "DK": return WLCountry.DENMARK;
            case "DJ": return WLCountry.DJIBOUTI;
            case "DM": return WLCountry.DOMINICA;
            case "DO": return WLCountry.DOMINICAN_REPUBLIC;
            case "EC": return WLCountry.ECUADOR;
            case "EG": return WLCountry.EGYPT;
            case "SV": return WLCountry.EL_SALVADOR;
            case "EE": return WLCountry.ESTONIA;
            case "ET": return WLCountry.ETHIOPIA;
            case "FK": return WLCountry.FALKLAND_ISLANDS;
            case "FO": return WLCountry.FAROE_ISLANDS;
            case "FJ": return WLCountry.FIJI;
            case "FI": return WLCountry.FINLAND;
            case "FR": return WLCountry.FRANCE;
            case "GA": return WLCountry.GABON;
            case "GM": return WLCountry.GAMBIA;
            case "GE": return WLCountry.GEORGIA;
            case "DE": return WLCountry.GERMANY;
            case "GH": return WLCountry.GHANA;
            case "GI": return WLCountry.GIBRALTAR;
            case "GR": return WLCountry.GREECE;
            case "GL": return WLCountry.GREENLAND;
            case "GD": return WLCountry.GRENADA;
            case "GP": return WLCountry.GUADELOUPE;
            case "GU": return WLCountry.GUAM;
            case "GT": return WLCountry.GUATEMALA;
            case "GG": return WLCountry.GUERNSEY;
            case "GN": return WLCountry.GUINEA;
            case "GW": return WLCountry.GUINEA_BISSAU;
            case "GY": return WLCountry.GUYANA;
            case "HT": return WLCountry.HAITI;
            case "HN": return WLCountry.HONDURAS;
            case "HK": return WLCountry.HONG_KONG;
            case "HU": return WLCountry.HUNGARY;
            case "IS": return WLCountry.ICELAND;
            case "IN": return WLCountry.INDIA;
            case "ID": return WLCountry.INDONESIA;
            case "IR": return WLCountry.IRAN;
            case "IQ": return WLCountry.IRAQ;
            case "IE": return WLCountry.IRELAND;
            case "IL": return WLCountry.ISRAEL;
            case "IT": return WLCountry.ITALY;
            case "JM": return WLCountry.JAMAICA;
            case "JP": return WLCountry.JAPAN;
            case "JO": return WLCountry.JORDAN;
            case "KZ": return WLCountry.KAZAKHSTAN;
            case "KE": return WLCountry.KENYA;
            case "KI": return WLCountry.KIRIBATI;
            case "KP": return WLCountry.NORTH_KOREA;
            case "KR": return WLCountry.SOUTH_KOREA;
            case "KW": return WLCountry.KUWAIT;
            case "KG": return WLCountry.KYRGYZSTAN;
            case "LA": return WLCountry.LAOS;
            case "LV": return WLCountry.LATVIA;
            case "LB": return WLCountry.LEBANON;
            case "LS": return WLCountry.LESOTHO;
            case "LR": return WLCountry.LIBERIA;
            case "LY": return WLCountry.LIBYA;
            case "LI": return WLCountry.LIECHTENSTEIN;
            case "LT": return WLCountry.LITHUANIA;
            case "LU": return WLCountry.LUXEMBOURG;
            case "MO": return WLCountry.MACAU;
            case "MG": return WLCountry.MADAGASCAR;
            case "MW": return WLCountry.MALAWI;
            case "MY": return WLCountry.MALAYSIA;
            case "MV": return WLCountry.MALDIVES;
            case "ML": return WLCountry.MALI;
            case "MT": return WLCountry.MALTA;
            case "MH": return WLCountry.MARSHALL_ISLANDS;
            //case "MQ": return WLCountry.MARINIQUE;
            case "MR": return WLCountry.MAURITANIA;
            case "MU": return WLCountry.MAURITIUS;
            //case "YT": return WLCountry.MAYOTTE;
            case "MX": return WLCountry.MEXICO;
            case "FM": return WLCountry.MICRONESIA;
            case "MD": return WLCountry.MOLDOVA;
            case "MC": return WLCountry.MONACO;
            case "MN": return WLCountry.MONGOLIA;
            case "ME": return WLCountry.MONTENEGRO;
            case "MS": return WLCountry.MONTSERRAT;
            case "MA": return WLCountry.MOROCCO;
            case "MZ": return WLCountry.MOZAMBIQUE;
            case "MM": return WLCountry.MYANMAR;
            case "NA": return WLCountry.NAMIBIA;
            case "NR": return WLCountry.NAURU;
            case "NP": return WLCountry.NEPAL;
            case "NL": return WLCountry.NETHERLANDS;
            case "NZ": return WLCountry.NEW_ZEALAND;
            case "NI": return WLCountry.NICARAGUA;
            case "NE": return WLCountry.NIGER;
            case "NG": return WLCountry.NIGERIA;
            case "NU": return WLCountry.NIUE;
            case "NF": return WLCountry.NORFOLK_ISLAND;
            case "MK": return WLCountry.NORTH_MACEDONIA;
            case "MP": return WLCountry.NORTHERN_MARIANA_ISLANDS;
            case "NO": return WLCountry.NORWAY;
            case "OM": return WLCountry.OMAN;
            case "PK": return WLCountry.PAKISTAN;
            case "PW": return WLCountry.PALAU;
            case "PS": return WLCountry.PALESTINE;
            case "PA": return WLCountry.PANAMA;
            case "PG": return WLCountry.PAPUA_NEW_GUINEA;
            case "PY": return WLCountry.PARAGUAY;
            case "PE": return WLCountry.PERU;
            case "PH": return WLCountry.PHILIPPINES;
            //case "PN": return WLCountry.PITCAIRN_ISLANDS;
            case "PL": return WLCountry.POLAND;
            case "PT": return WLCountry.PORTUGAL;
            case "PR": return WLCountry.PUERTO_RICO;
            case "QA": return WLCountry.QATAR;
            //case "RE": return WLCountry.REUNION;
            case "RO": return WLCountry.ROMANIA;
            case "RU": return WLCountry.RUSSIA;
            case "RW": return WLCountry.RWANDA;
            case "BL": return WLCountry.SAINT_BARTHELEMY;
            case "SH": return WLCountry.SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA;
            case "KN": return WLCountry.SAINT_KITTS_AND_NEVIS;
            case "LC": return WLCountry.SAINT_LUCIA;
            case "MF": return WLCountry.SAINT_MARTIN;
            case "PM": return WLCountry.SAINT_PIERRE_AND_MIQUELON;
            case "VC": return WLCountry.SAINT_VINCENT_AND_THE_GRENADINES;
            case "WS": return WLCountry.SAMOA;
            case "SM": return WLCountry.SAN_MARINO;
            case "ST": return WLCountry.SAO_TOME_AND_PRINCIPE;
            case "SA": return WLCountry.SAUDI_ARABIA;
            case "SN": return WLCountry.SENEGAL;
            case "RS": return WLCountry.SERBIA;
            case "SC": return WLCountry.SEYCHELLES;
            case "SL": return WLCountry.SIERRA_LEONE;
            case "SG": return WLCountry.SINGAPORE;
            //case "SX": return WLCountry.SINT_MAARTEN;
            case "SK": return WLCountry.SLOVAKIA;
            case "SI": return WLCountry.SLOVENIA;
            case "SB": return WLCountry.SOLOMON_ISLANDS;
            case "SO": return WLCountry.SOMALIA;
            case "ZA": return WLCountry.SOUTH_AFRICA;
            //case "GS": return WLCountry.SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS;
            case "SS": return WLCountry.SOUTH_SUDAN;
            case "ES": return WLCountry.SPAIN;
            case "LK": return WLCountry.SRI_LANKA;
            case "SD": return WLCountry.SUDAN;
            case "SR": return WLCountry.SURINAME;
            //
            case "SE": return WLCountry.SWEDEN;
            case "CH": return WLCountry.SWITZERLAND;
            case "SY": return WLCountry.SYRIA;
            case "TW": return WLCountry.TAIWAN;
            case "TJ": return WLCountry.TAJIKISTAN;
            case "TZ": return WLCountry.TANZANIA;
            case "TH": return WLCountry.THAILAND;
            case "TP": return WLCountry.TIMOR_LESTE;
            case "TG": return WLCountry.TOGO;
            case "TK": return WLCountry.TOKELAU;
            case "TO": return WLCountry.TONGA;
            case "TT": return WLCountry.TRINIDAD_AND_TOBAGO;
            case "TN": return WLCountry.TUNISIA;
            case "TR": return WLCountry.TURKEY;
            case "TM": return WLCountry.TURKMENISTAN;
            case "TC": return WLCountry.TURKS_AND_CAICOS_ISLANDS;
            case "TV": return WLCountry.TUVALU;
            case "UG": return WLCountry.UGANDA;
            case "UA": return WLCountry.UKRAINE;
            case "AE": return WLCountry.UNITED_ARAB_EMIRATES;
            case "GB": return WLCountry.UNITED_KINGDOM;
            //case "UM": return WLCountry.UNITED_STATES_MINOR_OUTLYING_ISLANDS;
            case "US": return WLCountry.UNITED_STATES;
            //case "VI": return WLCountry.UNITED_STATES_VIRGIN_ISLANDS;
            case "UY": return WLCountry.URUGUAY;
            case "UZ": return WLCountry.UZBEKISTAN;
            case "VU": return WLCountry.VANUATU;
            case "VA": return WLCountry.VATICAN_CITY;
            case "VE": return WLCountry.VENEZUELA;
            case "VN": return WLCountry.VIETNAM;
            case "WF": return WLCountry.WALLIS_AND_FUTUNA;
            case "EH": return WLCountry.WESTERN_SAHARA;
            case "YE": return WLCountry.YEMEN;
            case "ZM": return WLCountry.ZAMBIA;
            case "ZW": return WLCountry.ZIMBABWE;
            default: return null;
        }
    }

    public void getCitiesFromTerritory(String territory, CompletionHandler handler) {
        WLCities.INSTANCE.getFromTerritory(this, territory, handler);
    }

    public WLTimeZone[] getTimeZones() {
        return WLTimeZone.getFromCountry(this);
    }
}
