package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.Arrays;
import java.util.HashSet;

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

    BAHAMAS(
            "The Bahamas"
    ),
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
    CAPE_VERDE(
            "Cabo Verde"
    ),
    CAYMAN_ISLANDS,
    CENTRAL_AFRICAN_REPUBLIC,
    CHAD,
    CHILE,
    CHINA("People's Republic of China"),
    COLOMBIA,
    COMOROS,
    COOK_ISLANDS,
    COSTA_RICA,
    IVORY_COAST("Côte d'Ivoire", "Cote d'Ivoire"),
    CROATIA,
    CUBA,
    CYPRUS,
    CZECH_REPUBLIC("Czechia"),

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
    GAMBIA(
            "The Gambia"
    ),
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
    PHILIPPINES("Philipines"),
    POLAND,
    PORTUGAL,

    QATAR,

    REPUBLIC_OF_THE_CONGO,
    ROMANIA,
    RUSSIA("Russian Federation"),
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
    SOLOMON_ISLANDS(
            "Soloman Island"
    ),
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
            "Timor-Leste",
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
    VATICAN_CITY(
            "Holy See"
    ),
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
        this.aliases =  aliases != null && aliases.length > 0 ? new HashSet<>(Arrays.asList(aliases)) : null;
    }

    public static WLCountry valueOfString(String string) {
        for(WLCountry country : values()) {
            if(string.equalsIgnoreCase(country.getBackendID()) || string.equalsIgnoreCase(country.getISOAlpha2Official()) || string.equalsIgnoreCase(country.getISOAlpha3())) {
                return country;
            }
            final HashSet<String> aliases = country.getAliases();
            if(aliases != null) {
                for(String alias : country.getAliases()) {
                    if(string.equalsIgnoreCase(alias.replace(" ", ""))) {
                        return country;
                    }
                }
            }
        }
        return null;
    }

    public String getBackendID() {
        return name().toLowerCase().replace("_", "");
    }
    public String getShortName() {
        return LocalServer.toCorrectCapitalization(name(), "and", "the", "da", "of");
    }
    public String getShortNamePrefix() {
        switch (this) {
            case BAHAMAS:
            case BRITISH_VIRGIN_ISLANDS:

            case CAYMAN_ISLANDS:
            case CENTRAL_AFRICAN_REPUBLIC:
            case COMOROS:
            case COOK_ISLANDS:
            case CZECH_REPUBLIC:

            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO:

            case GAMBIA:

            case MARSHALL_ISLANDS:

            case NETHERLANDS:
            case NORTHERN_MARIANA_ISLANDS:

            case PHILIPPINES:

            case REPUBLIC_OF_THE_CONGO:

            case SOLOMON_ISLANDS:

            case UNITED_ARAB_EMIRATES:
            case UNITED_KINGDOM:
            case UNITED_STATES:
                return "the ";
            case MICRONESIA:
                return "the Federated States of ";
            default:
                return "";
        }
    }
    public HashSet<String> getAliases() {
        return aliases;
    }

    public String getGovernmentWebsite() {
        return WLGovernmentWebsite.get(this);
    }

    public String[] getDemonyms(boolean singular) {
        return WLCountryDemonyms.getSingular(this);
    }
    public String getISOAlpha2Official() {
        return WLCountryISOAlpha2.getOfficial(this);
    }
    public String getISOAlpha2Alias() {
        return WLCountryISOAlpha2.getAlias(this);
    }
    public String getISOAlpha2ParentGroup() {
        return WLCountryISOAlpha2.getParentGroup(this);
    }
    public String getISOAlpha3() {
        return WLCountryISOAlpha3.get(this);
    }

    public String getFlagEmoji() {
        return WLFlagEmoji.INSTANCE.get(this);
    }
    public SovereignStateSubdivision[] getSubdivisions() {
        return WLSubdivisions.get(this);
    }
    public SovereignStateSubdivision valueOfSovereignStateSubdivision(String name) {
        if(name != null) {
            final SovereignStateSubdivision[] subdivisions = getSubdivisions();
            if(subdivisions != null) {
                for(SovereignStateSubdivision subdivision : subdivisions) {
                    if(name.equalsIgnoreCase(subdivision.name())
                            || name.equalsIgnoreCase(subdivision.getBackendID())
                            || name.equalsIgnoreCase(subdivision.getName())
                            || name.equalsIgnoreCase(subdivision.getShortName())
                            || name.equalsIgnoreCase(subdivision.getRealName())
                            || name.equalsIgnoreCase(subdivision.getConditionalName())
                            || name.equalsIgnoreCase(subdivision.getISOAlpha2())
                    ) {
                        return subdivision;
                    }
                }
            }
        }
        return null;
    }

    public WLTimeZone[] getTimeZones() {
        return WLTimeZone.get(this);
    }

    public WLCountry[] getNeighbors() {
        return WLCountryNeighbors.get(this);
    }
    public WLCurrency[] getCurrencies() {
        return WLCountryCurrencies.get(this);
    }

    public boolean isUNMemberState() {
        return UNMemberStates.isUNMember(this);
    }
    public boolean isUNObserverState() {
        return UNMemberStates.isUNObserverState(this);
    }

    public String getWikipediaURL() {
        final String prefix = "https://en.wikipedia.org/wiki/";
        final String name;
        switch (this) {
            case GEORGIA:
                name = "Georgia_(country)";
                break;
            case MICRONESIA:
                name = "Federated_States_of_Micronesia";
                break;
            case SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA:
                name = "Saint_Helena%2C_Ascension_and_Tristan_da_Cunha";
                break;
            case SAINT_MARTIN:
                name = "Saint_Martin_(island)";
                break;
            default:
                name = getShortName().replace(" ", "_");
                break;
        }
        return prefix + name;
    }
    public String getUNStatus() {
        switch (this) {
            case ABKHAZIA:
            case ARTSAKH:
            case NORTHERN_CYPRUS:
            case SOMALILAND:
            case TRANSNISTRIA:
                return "NO MEMBERSHIP";
            case COOK_ISLANDS:
            case KOSOVO:
            case NIUE:
                return "UN SPECIAL AGENCY MEMBER";
            case PALESTINE:
            case VATICAN_CITY:
                return "UN OBSERVER STATE";
            case TAIWAN:
                return "FORMER UN MEMBER STATE";
            default:
                return null;
        }
    }
    public String getSovereigntyDispute() {
        switch (this) {
            case ABKHAZIA: return "Claimed by Georgia";
            case ARMENIA: return "Not recognised by Pakistan";
            case ARTSAKH: return "Claimed by Azerbaijan";
            case CHINA: return "Partially unrecognised. Claimed by the Republic of China";
            case CYPRUS: return "Not recognised by Turkey";
            case ISRAEL: return "Unrecognised as a state by 28 UN members";
            case KOSOVO: return "Claimed by Serbia";
            case NORTH_KOREA: return "Claimed by South Korea; unrecognised by 3 UN members (France, Japan, and South Korea)";
            case NORTHERN_CYPRUS: return "Claimed by Cyprus; recognised only by Turkey";
            case PALESTINE: return "Unrecognised as a state by Israel; recognised by 138 UN members";
            case SOMALILAND: return "Claimed by Somalia";
            case SOUTH_KOREA: return "Claimed by North Korea";
            case TAIWAN: return "Claimed by China";
            case TRANSNISTRIA: return "Claimed by Moldova; recognised only by Abkhazia, Artsakh, and South Ossetia";
            default: return null;
        }
    }

    public String[] getOfficialNames() {
        return WLCountryOfficialName.get(this);
    }
}
