package me.randomhashtags.worldlaws.location;

public enum WLCountry {
    ABKHAZIA,
    AFGHANISTAN(true),
    ALBANIA(true),
    ALGERIA(true),
    ANDORRA(true),
    ANGOLA(true),
    ANGUILLA,
    ANTIGUA_AND_BARBUDA,
    ARGENTINA(true),
    ARMENIA(true),
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
    CENTRAL_AFRICAN_REPUBLIC,
    CHAD,
    CHILE,
    CHINA(true),
    COLOMBIA,
    COMOROS,
    COOK_ISLANDS,
    COSTA_RICA,
    IVORY_COAST(false, "Cote d'Ivoire"),
    CROATIA,
    CUBA,
    CYPRUS,
    CZECH_REPUBLIC,

    DEMOCRATIC_REPUBLIC_OF_THE_CONGO,
    DENMARK,
    DJIBOUTI,
    DOMINICA,
    DOMINICAN_REPUBLIC,

    EAST_TIMOR,
    ECUADOR,
    EGYPT,
    EL_SALVADOR,
    ERITREA,
    ESTONIA,
    ETHIOPIA,

    FALKLAND_ISLANDS,
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
    GUATEMALA,
    GUERNSEY,
    GUINEA,
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
    LEBANON,
    LIBERIA,
    LIBYA,
    LIECHTENSTEIN,
    LITHUANIA,
    LUXEMBOURG,

    MACAU,
    MADAGASCAR,
    MALAWI,
    MALAYSIA,
    MALI,
    MALTA,
    MARSHALL_ISLANDS,
    MAURITIUS,
    MEXICO(true),
    MICRONESIA,
    MOLDOVA,
    MONGOLIA,
    MONTENEGRO,
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
    NORTH_KOREA,
    NORTH_MACEDONIA,
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

    SAMOA,
    SAN_MARINO,
    SAO_TOME_AND_PRINCIPE(false, "São Tomé and Príncipe"),
    SAUDI_ARABIA,
    SERBIA,
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
    TOGO(true),
    TONGA(true),
    TRANSNISTRIA,
    TRINIDAD_AND_TOBAGO(true),
    TUNISIA(true),
    TURKEY(true),
    TURKMENISTAN(true),
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

    YEMEN(true),

    ZAMBIA(true),
    ZIMBABWE(true),


    PUERTO_RICO,
    ;

    private final String[] aliases;
    private final boolean hasTerritories;

    WLCountry() {
        this(false);
    }
    WLCountry(boolean hasTerritories, String...aliases) {
        this.hasTerritories = hasTerritories;
        this.aliases = aliases;
    }

    public String getBackendID() {
        return name().toLowerCase().replace("_", "").replace("'", "");
    }
    public String[] getAliases() {
        return aliases;
    }

    public String getGovernmentWebsite() {
        switch (this) {
            case ARGENTINA: return "https://www.argentina.gob.ar";
            case AUSTRALIA: return "https://www.australia.gov.au";
            case BAHAMAS: return "https://www.bahamas.gov.bs";
            case BRAZIL: return "http://www.brazil.gov.br";
            case CANADA: return "https://www.canada.ca/en.html";
            case CHINA: return "https://www.gov.cn";
            case DENMARK: return "https://denmark.dk";
            case EGYPT: return "http://egypt.gov.eg/";
            case FIJI: return "https://www.fiji.gov.fj/Home";
            case FINLAND: return "https://valtioneuvosto.fi/etusivu";
            case FRANCE: return "https://www.gouvernement.fr";
            case GERMANY: return "https://www.bundesregierung.de/breg-de";
            case GREECE: return "https://www.gov.gr";
            case ICELAND: return "https://www.government.is";
            case INDIA: return "https://www.india.gov.in";
            case IRELAND: return "https://www.gov.ie";
            case ISRAEL: return "https://www.gov.il/en";
            case ITALY: return "http://www.italia.it/";
            case JAPAN: return "https://www.japan.go.jp";
            case JAMAICA: return "https://www.gov.jm";
            case LUXEMBOURG: return "https://gouvernement.lu";
            case MEXICO: return "https://www.gob.mx";
            case NEW_ZEALAND: return "https://www.govt.nz";
            case NETHERLANDS: return "https://www.government.nl";
            case NORWAY: return "https://www.regjeringen.no";
            case RUSSIA: return "http://government.ru";
            case SINGAPORE: return "https://www.gov.sg";
            case SWEDEN: return "https://www.government.se";
            case SWITZERLAND: return "https://www.admin.ch";
            case UNITED_KINGDOM: return "https://www.gov.uk";
            case UNITED_STATES: return "https://www.usa.gov";
            case ZAMBIA: return "https://zambia.co.zm";
            default: return null;
        }
    }
    public boolean hasTerritories() {
        return hasTerritories;
    }
}
