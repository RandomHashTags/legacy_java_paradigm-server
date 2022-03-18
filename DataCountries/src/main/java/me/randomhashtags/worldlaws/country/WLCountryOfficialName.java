package me.randomhashtags.worldlaws.country;

public enum WLCountryOfficialName {
    ;

    private static String[] collect(String...names) {
        return names;
    }
    public static String[] get(WLCountry country) {
        switch (country) {
            case ABKHAZIA: return collect("Republic of Abkhazia");
            case ARTSAKH: return collect("Republic of Artsakh");
            case COOK_ISLANDS: return null;
            case KOSOVO: return collect("Republic of Kosovo");
            case NIUE: return null;
            case NORTHERN_CYPRUS: return collect("Turkish Republic of Northern Cyprus");
            case SOMALILAND: return collect("Republic of Somaliland");
            case TAIWAN: return collect("Republic of China");
            case TRANSNISTRIA: return collect("Pridnestrovian Moldavian Republic");

            case AFGHANISTAN: return collect("Islamic Emirate of Afghanistan");
            case ALBANIA: return collect("Republic of Albania");
            case ALGERIA: return collect("People's Democratic Republic of Algeria");
            case AMERICAN_SAMOA: return null;
            case ANDORRA: return collect("Principality of Andorra");
            case ANGOLA: return collect("Republic of Angola");
            case ANGUILLA: return null;
            case ANTIGUA_AND_BARBUDA: return null;
            case ARGENTINA: return collect("Argentine Republic");
            case ARMENIA: return collect("Republic of Armenia");
            case ARUBA: return null;
            case AUSTRALIA: return collect("Commonwealth of Australia");
            case AUSTRIA: return collect("Republic of Austria");
            case AZERBAIJAN: return collect("Azerbaijan Republic", "Republic of Azerbaijan");

            case BAHAMAS: return collect("Commonwealth of The Bahamas");
            case BAHRAIN: return collect("Kingdom of Bahrain");
            case BANGLADESH: return collect("People's Republic of Bangladesh");
            case BARBADOS: return null;
            case BELARUS: return collect("Republic of Belarus");
            case BELGIUM: return collect("Kingdom of Belgium");
            case BELIZE: return null;
            case BENIN: return collect("Republic of Benin");
            case BERMUDA: return null;
            case BHUTAN: return collect("Kingdom of Bhutan");
            case BOLIVIA: return collect("Plurinational State of Bolivia");
            case BOSNIA_AND_HERZEGOVINA: return null;
            case BOTSWANA: return collect("Republic of Botswana");
            case BRAZIL: return collect("Federative Republic of Brazil");
            case BRITISH_VIRGIN_ISLANDS: return collect("Virgin Islands");
            case BRUNEI: return collect("Brunei Darussalam");
            case BULGARIA: return collect("Republic of Bulgaria");
            case BURKINA_FASO: return null;
            case BURUNDI: return collect("Republic of Burundi");

            case CAMBODIA: return collect("Kingdom of Cambodia");
            case CAMEROON: return collect("Republic of Cameroon");
            case CANADA: return null;
            case CAPE_VERDE: return collect("Republic of Cabo Verde");
            case CAYMAN_ISLANDS: return null;
            case CENTRAL_AFRICAN_REPUBLIC: return null;
            case CHAD: return collect("Republic of Chad");
            case CHILE: return collect("Republic of Chile");
            case CHINA: return collect("People's Republic of China");
            case COLOMBIA: return collect("Republic of Colombia");
            case COMOROS: return collect("Union of the Comoros");
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return null;
            case REPUBLIC_OF_THE_CONGO: return null;
            case COSTA_RICA: return collect("Republic of Costa Rica");
            case CROATIA: return collect("Republic of Croatia");
            case CUBA: return collect("Republic of Cuba");
            case CYPRUS: return collect("Republic of Cyprus");
            case CZECH_REPUBLIC: return null;

            case DENMARK: return collect("Kingdom of Denmark");
            case DJIBOUTI: return collect("Republic of Djibouti");
            case DOMINICA: return collect("Commonwealth of Dominica");
            case DOMINICAN_REPUBLIC: return null;

            case TIMOR_LESTE: return collect("Democratic Republic of Timor-Leste");
            case ECUADOR: return collect("Republic of Ecuador");
            case EGYPT: return collect("Arab Republic of Egypt");
            case EL_SALVADOR: return collect("Republic of El Salvador");
            case ERITREA: return collect("State of Eritrea");
            case ESTONIA: return collect("Republic of Estonia");
            case ESWATINI: return collect("Kingdom of Eswatini");
            case ETHIOPIA: return collect("Federal Democratic Republic of Ethiopia");

            case FALKLAND_ISLANDS: return null;
            case FAROE_ISLANDS: return null;

            case FIJI: return collect("Republic of Fiji");
            case FINLAND: return collect("Republic of Finland");
            case FRANCE: return collect("French Republic");

            case GABON: return collect("Gabonese Republic");
            case GAMBIA: return collect("Republic of The Gambia");
            case GEORGIA: return collect("Republic of Georgia");
            case GERMANY: return collect("Federal Republic of Germany");
            case GHANA: return collect("Republic of Ghana");
            case GIBRALTAR: return null;
            case GREECE: return collect("Hellenic Republic");
            case GREENLAND: return null;
            case GRENADA: return null;
            case GUAM: return null;
            case GUADELOUPE: return null;

            case GUATEMALA: return collect("Republic of Guatemala");
            case GUERNSEY: return collect("Bailiwick of Guernsey");
            case GUINEA: return collect("Republic of Guinea");
            case GUINEA_BISSAU: return collect("Republic of Guinea-Bissau");
            case GUYANA: return collect("Co-operative Republic of Guyana");

            case HAITI: return collect("Republic of Haiti");
            case HONDURAS: return collect("Republic of Honduras");
            case HONG_KONG: return collect("Hong Kong Special Administrative Region of the People's Republic of China");
            case HUNGARY: return null;

            case ICELAND: return null;
            case INDIA: return collect("Republic of India");
            case INDONESIA: return collect("Republic of Indonesia");
            case IRAN: return collect("Islamic Republic of Iran");
            case IRAQ: return collect("Republic of Iraq");
            case IRELAND: return null;
            case ISRAEL: return collect("State of Israel");
            case ITALY: return collect("Italian Republic");
            case IVORY_COAST: return collect("Republic of Côte d'Ivoire");

            case JAMAICA: return null;
            case JAPAN: return null;
            case JERSEY: return collect("Bailiwick of Jersey");
            case JORDAN: return  collect("Hashemite Kingdom of Jordan");

            case KAZAKHSTAN: return collect("Republic of Kazakhstan");
            case KENYA: return collect("Republic of Kenya");
            case KIRIBATI: return collect("Republic of Kiribati");
            case NORTH_KOREA: return collect("Democratic People's Republic of Korea");
            case SOUTH_KOREA: return collect("Republic of Korea");
            case KUWAIT: return collect("State of Kuwait");
            case KYRGYZSTAN: return collect("Kyrgyz Republic");

            case LAOS: return collect("Lao People's Democratic Republic");
            case LATVIA: return collect("Republic of Latvia");
            case LEBANON: return collect("Republic of Lebanon");
            case LESOTHO: return collect("Kingdom of Lesotho");
            case LIBERIA: return collect("Republic of Liberia");
            case LIBYA: return collect("State of Libya");
            case LIECHTENSTEIN: return collect("Principality of Liechtenstein");
            case LITHUANIA: return collect("Republic of Lithuania");
            case LUXEMBOURG: return collect("Grand Duchy of Luxembourg");

            case MACAU: return collect("Macao Special Administrative Region of the People's Republic of China");
            case MADAGASCAR: return collect("Republic of Madagascar");
            case MALAWI: return collect("Republic of Malawi");
            case MALAYSIA: return null;
            case MALDIVES: return collect("Republic of Maldives");
            case MALI: return collect("Republic of Mali");
            case MALTA: return collect("Republic of Malta");
            case MARSHALL_ISLANDS: return collect("Republic of the Marshall Islands");
            case MAURITANIA: return collect("Islamic Republic of Mauritania");
            case MAURITIUS: return collect("Republic of Mauritius");
            case MEXICO: return collect("United Mexican States");
            case MICRONESIA: return collect("Federated States of Micronesia");
            case MOLDOVA: return collect("Republic of Moldova");
            case MONACO: return collect("Principality of Monaco");
            case MONGOLIA: return null;
            case MONTENEGRO: return null;
            case MONTSERRAT: return null;
            case MOROCCO: return collect("Kingdom of Morocco");
            case MOZAMBIQUE: return collect("Republic of Mozambique");
            case MYANMAR: return collect("Republic of the Union of Myanmar");

            case NAMIBIA: return collect("Republic of Namibia");
            case NAURU: return collect("Republic of Nauru");
            case NEPAL: return collect("Federal Democratic Republic of Nepal");
            case NETHERLANDS: return null;
            case NEW_ZEALAND: return null;
            case NICARAGUA: return collect("Republic of Nicaragua");
            case NIGER: return collect("Republic of the Niger");
            case NIGERIA: return collect("Federal Republic of Nigeria");
            case NORFOLK_ISLAND: return null;
            case NORTH_MACEDONIA: return collect("Republic of North Macedonia");
            case NORTHERN_MARIANA_ISLANDS: return null;
            case NORWAY: return collect("Kingdom of Norway");

            case OMAN: return collect("Sultanate of Oman");

            case PAKISTAN: return collect("Islamic Republic of Pakistan");
            case PALAU: return collect("Republic of Palau");
            case PALESTINE: return collect("State of Palestine");
            case PANAMA: return collect("Republic of Panama");
            case PAPUA_NEW_GUINEA: return collect("Independent State of Papua New Guinea");
            case PARAGUAY: return collect("Republic of Paraguay");
            case PERU: return collect("Republic of Peru");
            case PHILIPPINES: return collect("Republic of the Philippines");
            case POLAND: return collect("Republic of Poland");
            case PORTUGAL: return collect("Portuguese Republic");
            case PUERTO_RICO: return null;

            case QATAR: return collect("State of Qatar");

            case ROMANIA: return null;
            case RUSSIA: return collect("Russian Federation");
            case RWANDA: return collect("Republic of Rwanda");

            case SAINT_BARTHELEMY: return collect("Territorial Collectivity of Saint Barthélemy");
            case SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA: return null;
            case SAINT_KITTS_AND_NEVIS: return collect("Federation of Saint Christopher and Nevis");
            case SAINT_LUCIA: return null;
            case SAINT_MARTIN: return collect("Collectivity of Saint Martin");
            case SAINT_PIERRE_AND_MIQUELON: return collect("Territorial Collectivity of Saint-Pierre and Miquelon");
            case SAINT_VINCENT_AND_THE_GRENADINES: return null;
            case SAMOA: return collect("Independent State of Samoa");
            case SAN_MARINO: return collect("Republic of San Marino");
            case SAO_TOME_AND_PRINCIPE: return collect("Democratic Republic of São Tomé and Príncipe");
            case SAUDI_ARABIA: return collect("Kingdom of Saudi Arabia");
            case SCOTLAND: return null;
            case SENEGAL: return collect("Republic of Senegal");
            case SERBIA: return collect("Republic of Serbia");
            case SEYCHELLES: return collect("Republic of Seychelles");
            case SIERRA_LEONE: return collect("Republic of Sierra Leone");
            case SINGAPORE: return collect("Republic of Singapore");
            case SLOVAKIA: return collect("Slovak Republic");
            case SLOVENIA: return collect("Republic of Slovenia");
            case SOLOMON_ISLANDS: return null;
            case SOMALIA: return collect("Federal Republic of Somalia");
            case SOUTH_AFRICA: return collect("Republic of South Africa");
            case SOUTH_SUDAN: return collect("Republic of South Sudan");
            case SPAIN: return collect("Kingdom of Spain");
            case SRI_LANKA: return collect("Sri Lanka");
            case SUDAN: return collect("Republic of the Sudan");
            case SURINAME: return collect("Republic of Suriname");
            case SWEDEN: return collect("Kingdom of Sweden");
            case SWITZERLAND: return collect("Swiss Confederation");
            case SYRIA: return collect("Syrian Arab Republic");

            case TAJIKISTAN: return collect("Republic of Tajikistan");
            case TANZANIA: return collect("United Republic of Tanzania");
            case THAILAND: return collect("Kingdom of Thailand");
            case TOGO: return collect("Togolese Republic");
            case TOKELAU: return null;
            case TONGA: return collect("Kingdom of Tonga");
            case TRINIDAD_AND_TOBAGO: return collect("Republic of Trinidad and Tobago");
            case TUNISIA: return collect("Republic of Tunisia");
            case TURKEY: return collect("Republic of Turkey");
            case TURKMENISTAN: return null;
            case TURKS_AND_CAICOS_ISLANDS: return null;
            case TUVALU: return null;

            case UGANDA: return collect("Republic of Uganda");
            case UKRAINE: return null;
            case UNITED_ARAB_EMIRATES: return null;
            case UNITED_KINGDOM: return collect("United Kingdom of Great Britian and Northern Ireland");
            case UNITED_STATES: return collect("United States of America");
            case URUGUAY: return collect("Oriental Republic of Uruguay");
            case UZBEKISTAN: return collect("Uzbekistan");

            case VANUATU: return collect("Republic of Vanuatu");
            case VATICAN_CITY: return collect("Vatican City State");
            case VENEZUELA: return collect("Bolivarian Republic of Venezuela");

            case VIETNAM: return collect("Socialist Republic of Vietnam");

            case WALLIS_AND_FUTUNA: return collect("Territory of the Wallis and Futuna Islands");
            case WESTERN_SAHARA: return null;

            case YEMEN: return collect("Republic of Yemen");

            case ZAMBIA: return collect("Republic of Zambia");
            case ZIMBABWE: return collect("Republic of Zimbabwe");
            default: return null;
        }
    }
}
