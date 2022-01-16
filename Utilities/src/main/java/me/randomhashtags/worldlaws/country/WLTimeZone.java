package me.randomhashtags.worldlaws.country;

public enum WLTimeZone { // https://www.timeanddate.com/time/map/ | https://www.timeanddate.com/worldclock/tonga | https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

    ABBREVIATION_COORDINATED_UNIVERSAL_TIME("UTC"),

    ACRE_TIME("America/Rio_Branco"),
    AFGHANISTAN_TIME("Asia/Kabul"),
    AMAZON_TIME("America/Manaus"),

    AMERICA_ALASKA_STANDARD_TIME("America/Anchorage"),
    AMERICA_CENTRAL_STANDARD_TIME("America/Chicago"),
    AMERICA_EASTERN_STANDARD_TIME("America/New_York"),
    AMERICA_HAWAII_STANDARD_TIME("Pacific/Honolulu"),
    AMERICA_MOUNTAIN_STANDARD_TIME("America/Denver"),
    AMERICA_PACIFIC_STANDARD_TIME("America/Los_Angeles"),

    ARABIA_TIME("Asia/Riyadh"),
    ARGENTINA_TIME("America/Argentina/Buenos_Aires"),

    AUSTRALIA_CENTRAL_STANDARD_TIME("Australia/Adelaide"),
    AUSTRALIA_CENTRAL_WESTERN_STANDARD_TIME("Australia/Eucla"),
    AUSTRALIA_EASTERN_STANDARD_TIME("Australia/Melbourne"),
    AUSTRALIA_WESTERN_STANDARD_TIME("Australia/Perth"),

    BRITISH_STANDARD_TIME("Europe/London"),
    BRASILIA_TIME("America/Sao_Paulo"),

    ATLANTIC_STANDARD_TIME("America/Halifax"),
    CANADA_NEWFOUNDLAND_STANDARD_TIME("America/St_Johns"),

    CAPE_VERDE_TIME("Atlantic/Cape_Verde"),

    CHINA_TIME("Asia/Shanghai"),
    COLOMBIA_TIME("America/Bogota"),
    ECUADOR_TIME("America/Santo_Domingo"),
    GALAPAGOS_TIME("Pacific/Galapagos"),

    EUROPE_CENTRAL_TIME("Africa/Algiers"),
    EUROPE_CENTRAL_STANDARD_TIME("Europe/Stockholm"),
    EUROPE_EASTERN_STANDARD_TIME("Europe/Helsinki"),
    EUROPE_WESTERN_STANDARD_TIME("Europe/Lisbon"),
    AZORES_STANDARD_TIME("Atlantic/Azores"),

    FIJI_TIME("Pacific/Fiji"),

    GREENLAND_EASTERN_STANDARD_TIME("America/Scoresbysund"),
    GREENLAND_WESTERN_STANDARD_TIME("America/Nuuk"),

    INDIA_TIME("Asia/Kolkata"),
    IRAN_STANDARD_TIME("Asia/Tehran"),
    IRISH_STANDARD_TIME("Europe/Dublin"),

    JAPAN_TIME("Asia/Tokyo"),

    ATLANTIC_TIME("America/Blanc-Sablon"),
    CENTRAL_STANDARD_TIME("America/Regina"),
    MOUNTAIN_STANDARD_TIME("America/Hermosillo"),
    EASTERN_TIME("America/Cancun"),
    EASTERN_EUROPEAN_STANDARD_TIME("Europe/Kaliningrad"),

    NEW_ZEALAND_STANDARD_TIME("Pacific/Auckland"),
    NEW_ZEALAND_CHATHAM_ISLAND_STANDARD_TIME("Pacific/Chatham"),

    ALMATY_TIME("Asia/Almaty"),
    AQTOBE_TIME("Asia/Aqtobe"),
    ARMENIA_TIME("Asia/Yerevan"),
    AZERBAIJAN_TIME("Asia/Baku"),
    BANGLADESH_TIME("Asia/Dhaka"),
    BOLIVIA_TIME("America/La_Paz"),
    BOUGAINVILLE_TIME("Pacific/Bougainville"),
    BHUTAN_TIME("Asia/Thimphu"),
    BRUNEI_TIME("Asia/Brunei"),
    CHILE_TIME("America/Punta_Arenas"),
    CHILE_STANDARD_TIME("America/Santiago"),
    CHOIBALSAN_TIME("Asia/Choibalsan"),
    CHUUK_TIME("Pacific/Chuuk"),
    COOK_ISLAND_TIME("Pacific/Rarotonga"),
    FALKLAND_ISLANDS_TIME("Atlantic/Stanley"),
    GEORGIA_TIME("Asia/Tbilisi"),
    GILBERT_TIME("Pacific/Tarawa"),
    GUAM_TIME("Pacific/Guam"),
    GULF_TIME("Asia/Dubai"),
    GUYANA_TIME("America/Guyana"),
    HOVD_TIME("Asia/Hovd"),
    INDOCHINA_TIME("Asia/Bangkok"),
    INDONESIA_CENTRAL_TIME("Asia/Makassar"),
    INDONESIA_EAST_TIME("Asia/Jayapura"),
    INDONESIA_WEST_TIME("Asia/Jakarta"),
    ISRAEL_STANDARD_TIME("Asia/Jerusalem"),
    KOREA_TIME("Asia/Seoul"),
    KOSRAE_TIME("Pacific/Kosrae"),
    KYRGYZSTAN_TIME("Asia/Bishkek"),
    LINE_TIME("Pacific/Kiritimati"),
    MALAYSIA_TIME("Asia/Kuala_Lumpur"),
    MALDIVES_TIMES("Indian/Maldives"),
    MARSHALL_ISLANDS("Pacific/Majuro"),
    MAURITIUS_TIME("Indian/Mauritius"),
    MYANMAR_TIME("Asia/Yangon"),
    NAURU_TIME("Pacific/Nauru"),
    NEPAL_TIME("Asia/Kathmandu"),
    NIUE_TIME("Pacific/Niue"),
    NORFOLK_STANDARD_TIME("Pacific/Norfolk"),
    ORAL_TIME("Asia/Oral"),
    PAKISTAN_TIME("Asia/Karachi"),
    PALAU_TIME("Pacific/Palau"),
    PAPUA_NEW_GUINEA_TIME("Pacific/Port_Moresby"),
    PARAGUAY_STANDARD_TIME("America/Asuncion"),
    PERU_TIME("America/Lima"),
    PHILIPPINES_TIME("Asia/Manila"),
    PHOENIX_TIME("Pacific/Enderbury"),
    POHNPEI_TIME("Pacific/Pohnpei"),
    SAMOA_WEST_STANDARD_TIME("Pacific/Apia"),
    SEYCHELLES_TIME("Indian/Mahe"),
    SINGAPORE_TIME("Asia/Singapore"),
    SOLOMAN_ISLAND_TIME("Pacific/Guadalcanal"),
    SURINAME_TIME("America/Paramaribo"),
    TAJIKISTAN_TIME("Asia/Dushanbe"),
    TIMOR_TIME("Asia/Dili"),
    TONGA_TIME("Pacific/Tongatapu"),
    TURKEY_TIME("Europe/Istanbul"),
    TUVALU_TIME("Pacific/Funafuti"),
    ULAANBAATAR_TIME("Asia/Ulaanbaatar"),
    URUGUAY_TIME("America/Montevideo"),
    UZBEKISTAN_TIME("Asia/Samarkand"),
    VANUATU_TIME("Pacific/Efate"),
    VENEZUELA_TIME("America/Caracas"),

    MOSCOW_TIME("Europe/Moscow"),
    RUSSIA_SAMARA_STANDARD_TIME("Europe/Samara"),
    RUSSIA_YEKATERINBURG_STANDARD_TIME("Asia/Yekaterinburg"),
    RUSSIA_OMSK_STANDARD_TIME("Asia/Omsk"),
    RUSSIA_KRASNOYARSK_STANDARD_TIME("Asia/Krasnoyarsk"),
    RUSSIA_IRKUTSK_STANDARD_TIME("Asia/Irkutsk"),
    RUSSIA_YAKUTSK_STANDARD_TIME("Asia/Yakutsk"),
    RUSSIA_VLADIVOSTOK_STANDARD_TIME("Asia/Vladivostok"),
    RUSSIA_MAGADAN_STANDARD_TIME("Asia/Magadan"),
    RUSSIA_ANADYR_STANDARD_TIME("Asia/Anadyr"),

    CENTRAL_AFRICA_TIME("Africa/Maputo"),
    EAST_AFRICA_TIME("Africa/Nairobi"),
    WEST_AFRICA_TIME("Africa/Lagos"),

    SOUTH_AFRICA_STANDARD_TIME("Africa/Johannesburg"),

    TURKMENISTAN_TIME("Asia/Ashgabat"),
    ;

    private final String identifier;

    WLTimeZone(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static WLTimeZone[] get(WLCountry country) {
        switch (country) {
            case AFGHANISTAN: return getAfghanistan();
            case ALBANIA:
            case ANDORRA:
            case AUSTRIA:
            case BELGIUM:
            case BOSNIA_AND_HERZEGOVINA:
            case CROATIA:
            case CZECH_REPUBLIC:
            case DENMARK:
            case FRANCE:
            case GERMANY:
            case GIBRALTAR:
            case HUNGARY:
            case ITALY:
            case KOSOVO:
            case LIECHTENSTEIN:
            case LUXEMBOURG:
            case MALTA:
            case MONACO:
            case MONTENEGRO:
            case NORTH_MACEDONIA:
            case NORWAY:
            case NETHERLANDS:
            case POLAND:
            case SAN_MARINO:
            case SERBIA:
            case SLOVAKIA:
            case SLOVENIA:
            case SWEDEN:
            case SWITZERLAND:
            case VATICAN_CITY:
                return getAlbania();
            case ALGERIA:
            case TUNISIA:
                return getAlgeria();
            case ANGOLA:
            case BENIN:
            case CAMEROON:
            case CENTRAL_AFRICAN_REPUBLIC:
            case CHAD:
            case GABON:
            case NIGER:
            case NIGERIA:
            case REPUBLIC_OF_THE_CONGO:
                return getAngola();
            case ANGUILLA:
            case ANTIGUA_AND_BARBUDA:
            case ARUBA:
            case BARBADOS:
            case DOMINICA:
            case DOMINICAN_REPUBLIC:
            case GUADELOUPE:
            case GRENADA:
            case PUERTO_RICO:
            case TRINIDAD_AND_TOBAGO:
                return getAnguilla();
            case ARGENTINA: return getArgentina();
            case ARMENIA: return getArmenia();
            case AUSTRALIA: return getAustralia();
            case AZERBAIJAN: return getAzerbaijan();
            case BAHAMAS:
            case HAITI:
                return getBahamas();
            case BAHRAIN:
            case IRAQ:
            case KUWAIT:
            case QATAR:
            case SAUDI_ARABIA:
            case YEMEN:
                return getBahrain();
            case BANGLADESH: return getBangladesh();
            case BERMUDA: return getBermuda();
            case BELARUS: return getBelarus();
            case BULGARIA:
            case CYPRUS:
            case ESTONIA:
            case FINLAND:
            case GREECE:
            case JORDAN:
            case LATVIA:
            case LEBANON:
            case LITHUANIA:
            case MOLDOVA:
            case NORTHERN_CYPRUS:
            case ROMANIA:
            case SYRIA:
            case TRANSNISTRIA:
                return getBulgaria();
            case BELIZE:
            case COSTA_RICA:
            case EL_SALVADOR:
            case GUATEMALA:
            case HONDURAS:
            case NICARAGUA:
                return getBelize();
            case BHUTAN: return getBhutan();
            case BOLIVIA: return getBolivia();
            case BOTSWANA:
            case BURUNDI:
            case MALAWI:
            case MOZAMBIQUE:
            case NAMIBIA:
            case RWANDA:
            case SOUTH_SUDAN:
            case SUDAN:
            case ZAMBIA:
            case ZIMBABWE:
                return getBotswana();
            case BRAZIL: return getBrazil();
            case BRUNEI: return getBrunei();
            case BURKINA_FASO:
            case GAMBIA:
            case GHANA:
            case GUINEA:
            case GUINEA_BISSAU:
            case ICELAND:
            case IVORY_COAST:
            case LIBERIA:
            case MALI:
            case MAURITANIA:
            case SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA:
            case SAO_TOME_AND_PRINCIPE:
            case SENEGAL:
            case SIERRA_LEONE:
            case TOGO:
                return getUTC();
            case CAMBODIA:
            case LAOS:
            case THAILAND:
            case VIETNAM:
                return getCambodia();
            case CANADA: return getCanada();
            case CAPE_VERDE: return getCapeVerde();
            case CHILE: return getChile();
            case CHINA:
            case MACAU:
            case TAIWAN:
                return getChina();
            case COLOMBIA: return getColombia();
            case COMOROS:
            case DJIBOUTI:
            case ERITREA:
            case ETHIOPIA:
            case KENYA:
            case MADAGASCAR:
            case SOMALIA:
            case SOMALILAND:
            case TANZANIA:
            case UGANDA:
                return getComoros();
            case COOK_ISLANDS: return getCookIslands();
            case CUBA: return getCuba();
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return getCongoDemocratic();
            case ECUADOR: return getEcuador();
            case EGYPT:
            case LIBYA:
                return getEgypt();
            case ESWATINI:
            case LESOTHO:
            case SOUTH_AFRICA:
                return getEswatini();
            case FALKLAND_ISLANDS: return getFalklandIslands();
            case FAROE_ISLANDS:
            case MOROCCO:
            case WESTERN_SAHARA:
                return getFaroeIslands();
            case FIJI: return getFiji();
            case GEORGIA: return getGeorgia();
            case GREENLAND: return getGreenland();
            case GUAM:
            case NORTHERN_MARIANA_ISLANDS:
                return getGuam();
            case GUYANA: return getGuyana();
            case INDIA:
            case SRI_LANKA:
                return getIndia();
            case INDONESIA: return getIndonesia();
            case IRAN: return getIran();
            case IRELAND: return getIreland();
            case ISRAEL: return getIsrael();
            case CAYMAN_ISLANDS:
            case JAMAICA:
            case PANAMA:
                return getCaymanIslands();
            case JAPAN: return getJapan();
            case KAZAKHSTAN: return getKazakhstan();
            case KIRIBATI: return getKiribati();
            case KYRGYZSTAN: return getKyrgyzstan();
            case MALAYSIA: return getMalaysia();
            case MALDIVES: return getMaldives();
            case MARSHALL_ISLANDS: return getMarshallIslands();
            case MAURITIUS: return getMauritius();
            case MEXICO: return getMexico();
            case MICRONESIA: return getMicronesia();
            case MONGOLIA: return getMongolia();
            case MYANMAR: return getMyanmar();
            case NAURU: return getNauru();
            case NEPAL: return getNepal();
            case NEW_ZEALAND: return getNewZealand();
            case NIUE: return getNiue();
            case NORFOLK_ISLAND: return getNorfolkIsland();
            case NORTH_KOREA:
            case SOUTH_KOREA:
                return getNorthKorea();
            case OMAN:
            case UNITED_ARAB_EMIRATES:
                return getOman();
            case PALAU: return getPalau();
            case PAPUA_NEW_GUINEA: return getPapuaNewGuinea();
            case PARAGUAY: return getParaguay();
            case PAKISTAN: return getPakistan();
            case PERU: return getPeru();
            case PHILIPPINES: return getPhilippines();
            case PORTUGAL: return getPortugal();
            case RUSSIA: return getRussia();
            case SAMOA: return getSamoa();
            case SCOTLAND:
            case UNITED_KINGDOM:
                return getScotland();
            case SEYCHELLES: return getSeychelles();
            case SINGAPORE: return getSingapore();
            case SOLOMON_ISLANDS: return getSolomonIslands();
            case SPAIN: return getSpain();
            case SURINAME: return getSuriname();
            case TAJIKISTAN: return getTajikistan();
            case TIMOR_LESTE: return getTimor();
            case TONGA: return getTonga();
            case TURKEY: return getTurkey();
            case TURKMENISTAN: return getTurkmenistan();
            case TUVALU: return getTuvalu();
            case UKRAINE: return getUkraine();
            case UNITED_STATES: return getUnitedStates();
            case URUGUAY: return getUruguay();
            case UZBEKISTAN: return getUzbekistan();
            case VANUATU: return getVanuatu();
            case VENEZUELA: return getVenezuela();
            default: return null;
        }
    }
    private static WLTimeZone[] get(WLTimeZone...timezones) {
        return timezones;
    }
    private static WLTimeZone[] getUTC() {
        return get(
                ABBREVIATION_COORDINATED_UNIVERSAL_TIME
        );
    }

    private static WLTimeZone[] getAfghanistan() {
        return get(
                AFGHANISTAN_TIME
        );
    }
    private static WLTimeZone[] getAlgeria() {
        return get(
                EUROPE_CENTRAL_TIME
        );
    }
    private static WLTimeZone[] getAngola() {
        return get(
                WEST_AFRICA_TIME
        );
    }
    private static WLTimeZone[] getArgentina() {
        return get(
                ARGENTINA_TIME
        );
    }
    private static WLTimeZone[] getArmenia() {
        return get(
                ARMENIA_TIME
        );
    }
    private static WLTimeZone[] getAustralia() {
        return get(
                AUSTRALIA_CENTRAL_STANDARD_TIME,
                AUSTRALIA_CENTRAL_WESTERN_STANDARD_TIME,
                AUSTRALIA_EASTERN_STANDARD_TIME,
                AUSTRALIA_WESTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getAzerbaijan() {
        return get(
                AZERBAIJAN_TIME
        );
    }
    private static WLTimeZone[] getAlbania() {
        return get(
                EUROPE_CENTRAL_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getBahamas() {
        return get(
                AMERICA_EASTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getBahrain() {
        return get(
                ARABIA_TIME
        );
    }
    private static WLTimeZone[] getAnguilla() {
        return get(
                ATLANTIC_TIME
        );
    }
    private static WLTimeZone[] getBelarus() {
        return get(
                MOSCOW_TIME
        );
    }
    private static WLTimeZone[] getBangladesh() {
        return get(
                BANGLADESH_TIME
        );
    }
    private static WLTimeZone[] getBermuda() {
        return get(
                ATLANTIC_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getBulgaria() {
        return get(
                EUROPE_EASTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getBelize() {
        return get(
                CENTRAL_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getBhutan() {
        return get(
                BHUTAN_TIME
        );
    }
    private static WLTimeZone[] getBolivia() {
        return get(
                BOLIVIA_TIME
        );
    }
    private static WLTimeZone[] getBotswana() {
        return get(
                CENTRAL_AFRICA_TIME
        );
    }
    private static WLTimeZone[] getBrazil() {
        return get(
                ACRE_TIME,
                AMAZON_TIME,
                BRASILIA_TIME
        );
    }
    private static WLTimeZone[] getBrunei() {
        return get(
                BRUNEI_TIME
        );
    }
    private static WLTimeZone[] getCapeVerde() {
        return get(
                CAPE_VERDE_TIME
        );
    }
    private static WLTimeZone[] getCambodia() {
        return get(
                INDOCHINA_TIME
        );
    }
    private static WLTimeZone[] getCanada() {
        return get(
                ATLANTIC_TIME,
                CENTRAL_STANDARD_TIME,
                MOUNTAIN_STANDARD_TIME,
                AMERICA_CENTRAL_STANDARD_TIME,
                AMERICA_MOUNTAIN_STANDARD_TIME,
                AMERICA_PACIFIC_STANDARD_TIME,
                AMERICA_EASTERN_STANDARD_TIME,
                ATLANTIC_STANDARD_TIME,
                CANADA_NEWFOUNDLAND_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getChile() {
        return get(
                CHILE_TIME,
                CHILE_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getChina() {
        return get(
                CHINA_TIME
        );
    }
    private static WLTimeZone[] getCongoDemocratic() {
        return get(
                WEST_AFRICA_TIME,
                CENTRAL_AFRICA_TIME
        );
    }
    private static WLTimeZone[] getColombia() {
        return get(
                COLOMBIA_TIME
        );
    }
    private static WLTimeZone[] getCookIslands() {
        return get(
                COOK_ISLAND_TIME
        );
    }
    private static WLTimeZone[] getCuba() {
        return get(
                AMERICA_CENTRAL_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getComoros() {
        return get(
                EAST_AFRICA_TIME
        );
    }
    private static WLTimeZone[] getEcuador() {
        return get(
                ECUADOR_TIME,
                GALAPAGOS_TIME
        );
    }
    private static WLTimeZone[] getEgypt() {
        return get(
                EASTERN_EUROPEAN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getEswatini() {
        return get(
                SOUTH_AFRICA_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getFalklandIslands() {
        return get(
                FALKLAND_ISLANDS_TIME
        );
    }
    private static WLTimeZone[] getFaroeIslands() {
        return get(
                EUROPE_WESTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getFiji() {
        return get(
                FIJI_TIME
        );
    }
    private static WLTimeZone[] getGeorgia() {
        return get(
                MOSCOW_TIME,
                GEORGIA_TIME
        );
    }
    private static WLTimeZone[] getGreenland() {
        return get(
                ATLANTIC_STANDARD_TIME,
                GREENLAND_EASTERN_STANDARD_TIME,
                GREENLAND_WESTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getGuam() {
        return get(
                GUAM_TIME
        );
    }
    private static WLTimeZone[] getGuyana() {
        return get(
                GUYANA_TIME
        );
    }
    private static WLTimeZone[] getIndia() {
        return get(
                INDIA_TIME
        );
    }
    private static WLTimeZone[] getIndonesia() {
        return get(
                INDONESIA_WEST_TIME,
                INDONESIA_CENTRAL_TIME,
                INDONESIA_EAST_TIME
        );
    }
    private static WLTimeZone[] getIran() {
        return get(
                IRAN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getIreland() {
        return get(
                IRISH_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getIsrael() {
        return get(
                ISRAEL_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getCaymanIslands() {
        return get(
                EASTERN_TIME
        );
    }
    private static WLTimeZone[] getJapan() {
        return get(
                JAPAN_TIME
        );
    }
    private static WLTimeZone[] getKazakhstan() {
        return get(
                ALMATY_TIME,
                AQTOBE_TIME,
                ORAL_TIME
        );
    }
    private static WLTimeZone[] getKiribati() {
        return get(
                GILBERT_TIME,
                PHOENIX_TIME,
                LINE_TIME
        );
    }
    private static WLTimeZone[] getKyrgyzstan() {
        return get(
                KYRGYZSTAN_TIME
        );
    }
    private static WLTimeZone[] getMalaysia() {
        return get(
                MALAYSIA_TIME
        );
    }
    private static WLTimeZone[] getMaldives() {
        return get(
                MALDIVES_TIMES
        );
    }
    private static WLTimeZone[] getMarshallIslands() {
        return get(
                MARSHALL_ISLANDS
        );
    }
    private static WLTimeZone[] getMauritius() {
        return get(
                MAURITIUS_TIME
        );
    }
    private static WLTimeZone[] getMexico() {
        return get(
                MOUNTAIN_STANDARD_TIME,
                EASTERN_TIME,
                AMERICA_CENTRAL_STANDARD_TIME,
                AMERICA_MOUNTAIN_STANDARD_TIME,
                AMERICA_PACIFIC_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getMicronesia() {
        return get(
                CHUUK_TIME,
                POHNPEI_TIME,
                KOSRAE_TIME
        );
    }
    private static WLTimeZone[] getMongolia() {
        return get(
                CHOIBALSAN_TIME,
                HOVD_TIME,
                ULAANBAATAR_TIME
        );
    }
    private static WLTimeZone[] getMyanmar() {
        return get(
                MYANMAR_TIME
        );
    }
    private static WLTimeZone[] getNauru() {
        return get(
                NAURU_TIME
        );
    }
    private static WLTimeZone[] getNepal() {
        return get(
                NEPAL_TIME
        );
    }
    private static WLTimeZone[] getNewZealand() {
        return get(
                NEW_ZEALAND_CHATHAM_ISLAND_STANDARD_TIME,
                NEW_ZEALAND_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getNiue() {
        return get(
                NIUE_TIME
        );
    }
    private static WLTimeZone[] getNorfolkIsland() {
        return get(
                NORFOLK_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getNorthKorea() {
        return get(
                KOREA_TIME
        );
    }
    private static WLTimeZone[] getOman() {
        return get(
                GULF_TIME
        );
    }
    private static WLTimeZone[] getPalau() {
        return get(
                PALAU_TIME
        );
    }
    private static WLTimeZone[] getPapuaNewGuinea() {
        return get(
                PAPUA_NEW_GUINEA_TIME,
                BOUGAINVILLE_TIME
        );
    }
    private static WLTimeZone[] getParaguay() {
        return get(
                PARAGUAY_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getPakistan() {
        return get(
                PAKISTAN_TIME
        );
    }
    private static WLTimeZone[] getPeru() {
        return get(
                PERU_TIME
        );
    }
    private static WLTimeZone[] getPhilippines() {
        return get(
                PHILIPPINES_TIME
        );
    }
    private static WLTimeZone[] getPortugal() {
        return get(
                AZORES_STANDARD_TIME,
                EUROPE_WESTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getRussia() {
        return get(
                EASTERN_EUROPEAN_STANDARD_TIME,
                MOSCOW_TIME,
                RUSSIA_SAMARA_STANDARD_TIME,
                RUSSIA_YEKATERINBURG_STANDARD_TIME,
                RUSSIA_OMSK_STANDARD_TIME,
                RUSSIA_KRASNOYARSK_STANDARD_TIME,
                RUSSIA_IRKUTSK_STANDARD_TIME,
                RUSSIA_YAKUTSK_STANDARD_TIME,
                RUSSIA_VLADIVOSTOK_STANDARD_TIME,
                RUSSIA_ANADYR_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getSamoa() {
        return get(
                SAMOA_WEST_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getSeychelles() {
        return get(
                SEYCHELLES_TIME
        );
    }
    private static WLTimeZone[] getSingapore() {
        return get(
                SINGAPORE_TIME
        );
    }
    private static WLTimeZone[] getSolomonIslands() {
        return get(
                SOLOMAN_ISLAND_TIME
        );
    }
    private static WLTimeZone[] getSpain() {
        return get(
                EUROPE_CENTRAL_STANDARD_TIME,
                EUROPE_WESTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getSuriname() {
        return get(
                SURINAME_TIME
        );
    }
    private static WLTimeZone[] getTajikistan() {
        return get(
                TAJIKISTAN_TIME
        );
    }
    private static WLTimeZone[] getTimor() {
        return get(
                TIMOR_TIME
        );
    }
    private static WLTimeZone[] getTonga() {
        return get(
                TONGA_TIME
        );
    }
    private static WLTimeZone[] getTurkey() {
        return get(
                TURKEY_TIME
        );
    }
    private static WLTimeZone[] getTurkmenistan() {
        return get(
                TURKMENISTAN_TIME
        );
    }
    private static WLTimeZone[] getTuvalu() {
        return get(
                TUVALU_TIME
        );
    }
    private static WLTimeZone[] getUkraine() {
        return get(
                EUROPE_EASTERN_STANDARD_TIME,
                MOSCOW_TIME
        );
    }
    private static WLTimeZone[] getScotland() {
        return get(
                BRITISH_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getUnitedStates() {
        return get(
                MOUNTAIN_STANDARD_TIME,
                AMERICA_ALASKA_STANDARD_TIME,
                AMERICA_CENTRAL_STANDARD_TIME,
                AMERICA_MOUNTAIN_STANDARD_TIME,
                AMERICA_HAWAII_STANDARD_TIME,
                AMERICA_PACIFIC_STANDARD_TIME,
                AMERICA_EASTERN_STANDARD_TIME
        );
    }
    private static WLTimeZone[] getUruguay() {
        return get(
                URUGUAY_TIME
        );
    }
    private static WLTimeZone[] getUzbekistan() {
        return get(
                UZBEKISTAN_TIME
        );
    }
    private static WLTimeZone[] getVanuatu() {
        return get(
                VANUATU_TIME
        );
    }
    private static WLTimeZone[] getVenezuela() {
        return get(
                VENEZUELA_TIME
        );
    }
}
