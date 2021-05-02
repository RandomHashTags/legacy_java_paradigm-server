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
    LATVIA,
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
    MALDIVES,
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
    SCOTLAND,
    SENEGAL,
    SERBIA,
    SEYCHELLES,
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

    public static WLCountry valueOfBackendID(String backendID) {
        for(WLCountry country : values()) {
            if(country.getBackendID().equals(backendID)) {
                return country;
            }
        }
        return null;
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
    public String[] getAliases() {
        return aliases;
    }

    public String getGovernmentWebsite() {
        switch (this) {
            case AFGHANISTAN: return "https://president.gov.af/en/";
            case ARGENTINA: return "https://www.argentina.gob.ar";
            case ARMENIA: return "https://www.gov.am/en";
            case AUSTRALIA: return "https://www.australia.gov.au";
            case AZERBAIJAN: return "https://en.president.az";
            case BAHAMAS: return "https://www.bahamas.gov.bs";
            case BANGLADESH: return "https://bangladesh.gov.bd/index.php";
            case BARBADOS: return "https://www.gov.bb";
            case BELARUS: return "https://www.belarus.by/en/";
            case BELGIUM: return "https://www.belgium.be/en";
            case BELIZE: return "https://belize.gov.bz";
            case BENIN: return "https://www.gouv.bj";
            case BERMUDA: return "https://www.gov.bm";
            case BHUTAN: return "https://www.gov.bt";
            case BOTSWANA: return "https://www.gov.bw";
            case BRAZIL: return "http://www.brazil.gov.br";
            case BULGARIA: return "https://www.gov.bg/en";
            case CAMBODIA: return "https://www.mfaic.gov.kh";
            case CAMEROON: return "https://www.prc.cm/en/";
            case CANADA: return "https://www.canada.ca/en.html";
            case CHILE: return "https://www.gob.cl/en/";
            case CHINA: return "https://www.gov.cn";
            case COLOMBIA: return "http://wp.presidencia.gov.co/sitios/en/Paginas/Presidency-Republic-Colombia.aspx";
            case COOK_ISLANDS: return "http://www.ck/govt.htm";
            case COSTA_RICA: return "https://presidencia.go.cr";
            case DENMARK: return "https://denmark.dk";
            case DOMINICA: return "https://dominica.gov.dm";
            case EAST_TIMOR: return "http://timor-leste.gov.tl/?lang=en";
            case EGYPT: return "http://egypt.gov.eg/";
            case ESTONIA: return "https://www.valitsus.ee/en";
            case ETHIOPIA: return "https://www.pmo.gov.et";
            case FALKLAND_ISLANDS: return "https://www.falklands.gov.fk";
            case FIJI: return "https://www.fiji.gov.fj/Home";
            case FINLAND: return "https://valtioneuvosto.fi/etusivu";
            case FRANCE: return "https://www.gouvernement.fr";
            case GABON: return "http://www.gouvernement.ga";
            case GAMBIA: return "https://statehouse.gm";
            case GERMANY: return "https://www.bundesregierung.de/breg-de";
            case GIBRALTAR: return "https://www.gibraltar.gov.gi";
            case GREECE: return "https://www.gov.gr";
            case GREENLAND: return "https://naalakkersuisut.gl/en/Naalakkersuisut";
            case ICELAND: return "https://www.government.is";
            case INDIA: return "https://www.india.gov.in";
            case INDONESIA: return "https://indonesia.nl/en/";
            case IRAN: return "https://irangov.ir/en";
            case IRAQ: return "https://gds.gov.iq";
            case IRELAND: return "https://www.gov.ie";
            case ISRAEL: return "https://www.gov.il/en";
            case ITALY: return "http://www.italia.it/";
            case JAPAN: return "https://www.japan.go.jp";
            case JAMAICA: return "https://www.gov.jm";
            case JORDAN: return "https://portal.jordan.gov.jo/wps/portal/%5c?lang=en#/";
            case KAZAKHSTAN: return "https://www.akorda.kz/en";
            case KENYA: return "https://www.president.go.ke";
            case KUWAIT: return "https://www.e.gov.kw/sites/kgoEnglish/Pages/HomePage.aspx";
            case LAOS: return "http://www.na.gov.la/index.php?lang=en#";
            case LATVIA: return "https://www.mk.gov.lv/en";
            case LEBANON: return "http://portal.gov.lb/index.html";
            case LIBERIA: return "https://eliberia.gov.lr";
            case LIECHTENSTEIN: return "https://www.liechtenstein.li/en/";
            case LITHUANIA: return "https://lrv.lt/en";
            case LUXEMBOURG: return "https://gouvernement.lu";
            case MALAYSIA: return "https://www.malaysia.gov.my/portal/";
            case MEXICO: return "https://www.gob.mx";
            case MICRONESIA: return "https://fsmgov.org";
            case MOROCCO: return "https://www.maroc.ma/en";
            case MOZAMBIQUE: return "https://www.portaldogoverno.gov.mz";
            case NAMIBIA: return "https://www.gov.na";
            case NEPAL: return "https://www.nepal.gov.np";
            case NEW_ZEALAND: return "https://www.govt.nz";
            case NETHERLANDS: return "https://www.government.nl";
            case NIGER: return "https://www.presidence.ne";
            case NIGERIA: return "https://nigeria.gov.ng";
            case NORTH_MACEDONIA: return "https://vlada.mk/?ln=en-gb";
            case NORWAY: return "https://www.regjeringen.no";
            case OMAN: return "https://www.oman.om";
            case PAKISTAN: return "https://www.pakistan.gov.pk";
            case PALAU: return "https://www.palaugov.pw";
            case POLAND: return "https://www.gov.pl";
            case QATAR: return "https://www.gco.gov.qa/en/";
            case ROMANIA: return "https://gov.ro/en";
            case RUSSIA: return "http://government.ru";
            case RWANDA: return "https://www.gov.rw";
            case SCOTLAND: return "https://www.gov.scot";
            case SENEGAL: return "https://www.presidence.sn/en/";
            case SERBIA: return "https://www.srbija.gov.rs";
            case SINGAPORE: return "https://www.gov.sg";
            case SLOVAKIA: return "https://www.vlada.gov.sk";
            case SOMALIA: return "https://www.somalia.gov.so";
            case SWEDEN: return "https://www.government.se";
            case SWITZERLAND: return "https://www.admin.ch";
            case TAIWAN: return "https://taiwan.gov.tw";
            case TAJIKISTAN: return "https://mfa.tj/en";
            case THAILAND: return "https://www.thaigov.go.th/main/contents";
            case TURKEY: return "https://www.turkiye.gov.tr";
            case UNITED_ARAB_EMIRATES: return "https://u.ae/en";
            case UNITED_KINGDOM: return "https://www.gov.uk";
            case UNITED_STATES: return "https://www.usa.gov";
            case ZAMBIA: return "https://zambia.co.zm";
            case ZIMBABWE: return "http://www.zim.gov.zw/index.php/en/";
            default: return null;
        }
    }
    public boolean hasTerritories() {
        return hasTerritories;
    }
}
