package me.randomhashtags.worldlaws.country;

public enum WLCountryNeighbors { // https://en.wikipedia.org/wiki/List_of_countries_and_territories_by_land_borders
    ;

    private static WLCountry[] collect(WLCountry...countries) {
        return countries;
    }
    public static WLCountry[] getNeighbors(WLCountry country) {
        switch (country) {

            case ABKHAZIA: return collect(WLCountry.RUSSIA, WLCountry.GEORGIA);
            case AFGHANISTAN: return collect(WLCountry.CHINA, WLCountry.IRELAND, WLCountry.PAKISTAN, WLCountry.TAJIKISTAN, WLCountry.TURKMENISTAN, WLCountry.UZBEKISTAN);
            case ALBANIA: return collect(WLCountry.GREECE, WLCountry.KOSOVO, WLCountry.NORTH_MACEDONIA, WLCountry.MONTENEGRO);
            case ALGERIA: return collect(WLCountry.LIBYA, WLCountry.MALI, WLCountry.MAURITANIA, WLCountry.MOROCCO, WLCountry.NIGER, WLCountry.TUNISIA, WLCountry.WESTERN_SAHARA);
            case ANDORRA: return collect(WLCountry.FRANCE, WLCountry.SPAIN);
            case ANGOLA: return collect(WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.NAMIBIA, WLCountry.ZAMBIA);
            case ANTIGUA_AND_BARBUDA: return null;
            case ARGENTINA: return collect(WLCountry.BOLIVIA, WLCountry.BRAZIL, WLCountry.CHILE, WLCountry.PARAGUAY, WLCountry.URUGUAY);
            case ARMENIA: return collect(WLCountry.AZERBAIJAN, WLCountry.GEORGIA, WLCountry.IRAN, WLCountry.TURKEY);
            case AUSTRALIA: return null;
            case AUSTRIA: return collect(WLCountry.CZECH_REPUBLIC, WLCountry.GERMANY, WLCountry.HUNGARY, WLCountry.ITALY, WLCountry.LIECHTENSTEIN, WLCountry.SLOVAKIA, WLCountry.SLOVENIA, WLCountry.SWITZERLAND);
            case AZERBAIJAN: return collect(WLCountry.ARMENIA, WLCountry.GEORGIA, WLCountry.IRAN, WLCountry.RUSSIA, WLCountry.TURKEY);

            case BAHAMAS: return null;
            case BAHRAIN: return null;
            case BANGLADESH: return collect(WLCountry.INDIA, WLCountry.MYANMAR);
            case BARBADOS: return null;
            case BELARUS: return collect(WLCountry.LATVIA, WLCountry.LITHUANIA, WLCountry.POLAND, WLCountry.RUSSIA, WLCountry.UKRAINE);
            case BELGIUM: return collect(WLCountry.FRANCE, WLCountry.GERMANY, WLCountry.LUXEMBOURG, WLCountry.NETHERLANDS);
            case BELIZE: return collect(WLCountry.GUATEMALA, WLCountry.MEXICO);
            case BENIN: return collect(WLCountry.BURKINA_FASO, WLCountry.NIGER, WLCountry.NIGERIA, WLCountry.TOGO);
            case BHUTAN: return collect(WLCountry.CHINA, WLCountry.INDIA);
            case BOLIVIA: return collect(WLCountry.ARGENTINA, WLCountry.BRAZIL, WLCountry.CHILE, WLCountry.PARAGUAY, WLCountry.PERU);
            case BOSNIA_AND_HERZEGOVINA: return collect(WLCountry.CROATIA, WLCountry.MONTENEGRO, WLCountry.SERBIA);
            case BOTSWANA: return collect(WLCountry.NAMIBIA, WLCountry.SOUTH_AFRICA, WLCountry.ZAMBIA, WLCountry.ZIMBABWE);
            case BRAZIL: return collect(WLCountry.ARGENTINA, WLCountry.BOLIVIA, WLCountry.COLOMBIA, WLCountry.GUYANA, WLCountry.PARAGUAY, WLCountry.PERU, WLCountry.SURINAME, WLCountry.URUGUAY, WLCountry.VENEZUELA);
            case BRUNEI: return collect(WLCountry.MALAWI);
            case BULGARIA: return collect(WLCountry.GREECE, WLCountry.NORTH_MACEDONIA, WLCountry.ROMANIA, WLCountry.SERBIA, WLCountry.TURKEY);
            case BURKINA_FASO: return collect(WLCountry.BENIN, WLCountry.IVORY_COAST, WLCountry.GHANA, WLCountry.MALI, WLCountry.NIGER, WLCountry.TOGO);
            case BURUNDI: return collect(WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.RWANDA, WLCountry.TANZANIA);

            case CAMBODIA: return collect(WLCountry.LAOS, WLCountry.THAILAND, WLCountry.VIETNAM);
            case CAMEROON: return collect(WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.CHAD, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.GABON, WLCountry.NIGERIA);
            case CANADA: return collect(WLCountry.UNITED_STATES);
            case CAPE_VERDE: return null;
            case CENTRAL_AFRICAN_REPUBLIC: return collect(WLCountry.CAMEROON, WLCountry.CHAD, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.SOUTH_SUDAN, WLCountry.SUDAN);
            case CHAD: return collect(WLCountry.CAMEROON, WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.LIBYA, WLCountry.NIGER, WLCountry.NIGERIA, WLCountry.SUDAN);
            case CHILE: return collect(WLCountry.ARGENTINA, WLCountry.BOLIVIA, WLCountry.PERU);
            case CHINA: return collect(WLCountry.AFGHANISTAN, WLCountry.BHUTAN, WLCountry.HONG_KONG, WLCountry.INDIA, WLCountry.KAZAKHSTAN, WLCountry.NORTH_KOREA, WLCountry.KYRGYZSTAN, WLCountry.LAOS, WLCountry.MACAU, WLCountry.MONGOLIA, WLCountry.MYANMAR, WLCountry.NEPAL, WLCountry.PAKISTAN, WLCountry.RUSSIA, WLCountry.TAJIKISTAN, WLCountry.VIETNAM);
            case COLOMBIA: return collect(WLCountry.BRAZIL, WLCountry.ECUADOR, WLCountry.PANAMA, WLCountry.PERU, WLCountry.VENEZUELA);
            case COMOROS: return null;
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return collect(WLCountry.ANGOLA, WLCountry.BURUNDI, WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.RWANDA, WLCountry.SOUTH_SUDAN, WLCountry.TANZANIA, WLCountry.UGANDA, WLCountry.ZAMBIA);
            case REPUBLIC_OF_THE_CONGO: return collect(WLCountry.ANGOLA, WLCountry.CAMEROON, WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.GABON);
            case COSTA_RICA: return collect(WLCountry.NICARAGUA, WLCountry.PANAMA);
            case IVORY_COAST: return collect(WLCountry.BURKINA_FASO, WLCountry.GHANA, WLCountry.GUINEA, WLCountry.LIBERIA, WLCountry.MALI);
            case CROATIA: return collect(WLCountry.BOSNIA_AND_HERZEGOVINA, WLCountry.HUNGARY, WLCountry.MONTENEGRO, WLCountry.SERBIA, WLCountry.SLOVENIA);
            case CUBA: return null;
            case CYPRUS: return null;
            case CZECH_REPUBLIC: return collect(WLCountry.AUSTRIA, WLCountry.GERMANY, WLCountry.POLAND, WLCountry.SLOVAKIA);
            case DENMARK: return collect(WLCountry.GERMANY);
            case DJIBOUTI: return collect(WLCountry.ERITREA, WLCountry.ETHIOPIA, WLCountry.SOMALILAND);
            case DOMINICA: return null;
            case DOMINICAN_REPUBLIC: return collect(WLCountry.HAITI);

            case TIMOR_LESTE: return collect(WLCountry.INDONESIA);
            case ECUADOR: return collect(WLCountry.COLOMBIA, WLCountry.PERU);
            case EGYPT: return collect(WLCountry.ISRAEL, WLCountry.LIBYA, WLCountry.SUDAN);
            case EL_SALVADOR: return collect(WLCountry.GUATEMALA, WLCountry.HONDURAS);
            case ERITREA: return collect(WLCountry.DJIBOUTI, WLCountry.ETHIOPIA, WLCountry.SUDAN);
            case ESTONIA: return collect(WLCountry.LATVIA, WLCountry.RUSSIA);
            case ESWATINI: return collect(WLCountry.MOZAMBIQUE, WLCountry.SOUTH_AFRICA);
            case ETHIOPIA: return collect(WLCountry.DJIBOUTI, WLCountry.ERITREA, WLCountry.KENYA, WLCountry.SOMALILAND, WLCountry.SOUTH_SUDAN, WLCountry.SUDAN);

            case FIJI: return null;
            case FINLAND: return collect(WLCountry.NORWAY, WLCountry.SWEDEN, WLCountry.RUSSIA);

            case GABON: return collect(WLCountry.CAMEROON, WLCountry.REPUBLIC_OF_THE_CONGO);
            case GAMBIA: return collect(WLCountry.SENEGAL);
            case GEORGIA: return collect(WLCountry.ARMENIA, WLCountry.AZERBAIJAN, WLCountry.RUSSIA, WLCountry.TURKEY, WLCountry.ABKHAZIA);
            case GERMANY: return collect(WLCountry.AUSTRIA, WLCountry.BELGIUM, WLCountry.CZECH_REPUBLIC, WLCountry.DENMARK, WLCountry.FRANCE, WLCountry.LUXEMBOURG, WLCountry.NETHERLANDS, WLCountry.POLAND, WLCountry.SWITZERLAND);
            case GHANA: return collect(WLCountry.BURKINA_FASO, WLCountry.IVORY_COAST, WLCountry.TOGO);
            case GREECE: return collect(WLCountry.ALBANIA, WLCountry.BULGARIA, WLCountry.TURKEY, WLCountry.NORTH_MACEDONIA);
            case GREENLAND: return collect(WLCountry.CANADA);
            case GRENADA: return null;
            case GUATEMALA: return collect(WLCountry.BELIZE, WLCountry.EL_SALVADOR, WLCountry.HONDURAS, WLCountry.MEXICO);
            case GUINEA: return collect(WLCountry.IVORY_COAST, WLCountry.GUINEA_BISSAU, WLCountry.LIBERIA, WLCountry.MALI, WLCountry.SENEGAL, WLCountry.SIERRA_LEONE);
            case GUINEA_BISSAU: return collect(WLCountry.GUINEA, WLCountry.SENEGAL);
            case GUYANA: return collect(WLCountry.BRAZIL, WLCountry.SURINAME, WLCountry.VENEZUELA);

            case HAITI: return collect(WLCountry.DOMINICAN_REPUBLIC);
            case HONDURAS: return collect(WLCountry.GUATEMALA, WLCountry.EL_SALVADOR, WLCountry.NICARAGUA);
            case HONG_KONG: return collect(WLCountry.CHINA);
            case HUNGARY: return collect(WLCountry.AUSTRIA, WLCountry.CROATIA, WLCountry.ROMANIA, WLCountry.SERBIA, WLCountry.SLOVAKIA, WLCountry.SLOVENIA, WLCountry.UKRAINE);

            case ICELAND: return null;
            case INDIA: return collect(WLCountry.BANGLADESH, WLCountry.BHUTAN, WLCountry.CHINA, WLCountry.MYANMAR, WLCountry.NEPAL, WLCountry.PAKISTAN, WLCountry.SRI_LANKA);
            case INDONESIA: return collect(WLCountry.TIMOR_LESTE, WLCountry.MALAYSIA, WLCountry.PAPUA_NEW_GUINEA);
            case IRAN: return collect(WLCountry.AFGHANISTAN, WLCountry.ARMENIA, WLCountry.AZERBAIJAN, WLCountry.IRAQ, WLCountry.PAKISTAN, WLCountry.TURKEY, WLCountry.TURKMENISTAN);
            case IRAQ: return collect(WLCountry.IRAN, WLCountry.JORDAN, WLCountry.KUWAIT, WLCountry.SAUDI_ARABIA, WLCountry.SYRIA, WLCountry.TURKEY);
            case IRELAND: return collect(WLCountry.UNITED_KINGDOM);
            case ISRAEL: return collect(WLCountry.EGYPT, WLCountry.JORDAN, WLCountry.LEBANON, WLCountry.SYRIA);
            case ITALY: return collect(WLCountry.AUSTRIA, WLCountry.FRANCE, WLCountry.SAN_MARINO, WLCountry.SLOVENIA, WLCountry.SWITZERLAND, WLCountry.VATICAN_CITY);

            case JAMAICA: return null;
            case JAPAN: return null;
            case JORDAN: return collect(WLCountry.IRAQ, WLCountry.ISRAEL, WLCountry.SAUDI_ARABIA, WLCountry.SYRIA);

            case KAZAKHSTAN: return collect(WLCountry.CHINA, WLCountry.KYRGYZSTAN, WLCountry.RUSSIA, WLCountry.TURKMENISTAN, WLCountry.UZBEKISTAN);
            case KENYA: return collect(WLCountry.ETHIOPIA, WLCountry.SOMALIA, WLCountry.SOUTH_SUDAN, WLCountry.TANZANIA, WLCountry.UGANDA);
            case KIRIBATI: return null;
            case KOSOVO: return collect(WLCountry.ALBANIA, WLCountry.MONTENEGRO, WLCountry.NORTH_MACEDONIA, WLCountry.SERBIA);
            case KUWAIT: return collect(WLCountry.IRAQ, WLCountry.SAUDI_ARABIA);
            case KYRGYZSTAN: return collect(WLCountry.CHINA, WLCountry.KAZAKHSTAN, WLCountry.TAJIKISTAN, WLCountry.UZBEKISTAN);

            case LAOS: return collect(WLCountry.CAMBODIA, WLCountry.CHINA, WLCountry.MYANMAR, WLCountry.THAILAND, WLCountry.VIETNAM);
            case LATVIA: return collect(WLCountry.BELARUS, WLCountry.ESTONIA, WLCountry.LITHUANIA, WLCountry.RUSSIA);
            case LEBANON: return collect(WLCountry.ISRAEL, WLCountry.SYRIA);
            case LESOTHO: return collect(WLCountry.SOUTH_AFRICA);
            case LIBERIA: return collect(WLCountry.GUINEA, WLCountry.IVORY_COAST, WLCountry.SIERRA_LEONE);
            case LIBYA: return collect(WLCountry.ALGERIA, WLCountry.CHAD, WLCountry.EGYPT, WLCountry.NIGER, WLCountry.SUDAN, WLCountry.TUNISIA);
            case LIECHTENSTEIN: return collect(WLCountry.AUSTRIA, WLCountry.SWITZERLAND);
            case LITHUANIA: return collect(WLCountry.BELARUS, WLCountry.LATVIA, WLCountry.POLAND, WLCountry.RUSSIA);
            case LUXEMBOURG: return collect(WLCountry.BELGIUM, WLCountry.FRANCE, WLCountry.GERMANY);

            case MACAU: return collect(WLCountry.CHINA);
            case MADAGASCAR: return null;
            case MALAWI: return collect(WLCountry.MOZAMBIQUE, WLCountry.TANZANIA, WLCountry.ZAMBIA);
            case MALAYSIA: return collect(WLCountry.BRUNEI, WLCountry.INDONESIA, WLCountry.THAILAND);
            case MALDIVES: return null;
            case MALI: return collect(WLCountry.ALGERIA, WLCountry.BURKINA_FASO, WLCountry.IVORY_COAST, WLCountry.GUINEA, WLCountry.MAURITANIA, WLCountry.NIGER, WLCountry.SENEGAL);
            case MALTA: return null;
            case MARSHALL_ISLANDS: return null;
            case MAURITANIA: return collect(WLCountry.ALGERIA, WLCountry.MALI, WLCountry.SENEGAL, WLCountry.WESTERN_SAHARA);
            case MAURITIUS: return null;
            case MEXICO: return collect(WLCountry.BELIZE, WLCountry.GUATEMALA, WLCountry.UNITED_STATES);
            case MICRONESIA: return null;
            case MOLDOVA: return collect(WLCountry.ROMANIA, WLCountry.UKRAINE);
            case MONACO: return collect(WLCountry.FRANCE);
            case MONGOLIA: return collect(WLCountry.CHINA, WLCountry.RUSSIA);
            case MONTENEGRO: return collect(WLCountry.ALBANIA, WLCountry.BOSNIA_AND_HERZEGOVINA, WLCountry.CROATIA, WLCountry.KOSOVO, WLCountry.SERBIA);
            case MOROCCO: return collect(WLCountry.ALGERIA, WLCountry.WESTERN_SAHARA, WLCountry.SPAIN);
            case MOZAMBIQUE: return collect(WLCountry.ESWATINI, WLCountry.MALAWI, WLCountry.SOUTH_AFRICA, WLCountry.TANZANIA, WLCountry.ZAMBIA, WLCountry.ZIMBABWE);
            case MYANMAR: return collect(WLCountry.BANGLADESH, WLCountry.CHINA, WLCountry.INDIA, WLCountry.LAOS, WLCountry.THAILAND);

            case NAMIBIA: return collect(WLCountry.ANGOLA, WLCountry.BOTSWANA, WLCountry.SOUTH_AFRICA, WLCountry.ZAMBIA);
            case NAURU: return null;
            case NEPAL: return collect(WLCountry.CHINA, WLCountry.INDIA);
            case NETHERLANDS: return collect(WLCountry.BELGIUM, WLCountry.GERMANY, WLCountry.SAINT_MARTIN);
            case NEW_ZEALAND: return null;
            case NICARAGUA: return collect(WLCountry.COSTA_RICA, WLCountry.HONDURAS);
            case NIGER: return collect(WLCountry.ALGERIA, WLCountry.BENIN, WLCountry.BURKINA_FASO, WLCountry.CHAD, WLCountry.LIBYA, WLCountry.MALI, WLCountry.NIGERIA);
            case NIGERIA: return collect(WLCountry.BENIN, WLCountry.CAMEROON, WLCountry.CHAD, WLCountry.NIGER);
            case NORTH_KOREA: return collect(WLCountry.CHINA, WLCountry.SOUTH_KOREA, WLCountry.RUSSIA);
            case NORTH_MACEDONIA: return collect(WLCountry.ALBANIA, WLCountry.BULGARIA, WLCountry.GREECE, WLCountry.KOSOVO, WLCountry.SERBIA);
            case NORWAY: return collect(WLCountry.FINLAND, WLCountry.SWEDEN, WLCountry.RUSSIA);

            case OMAN: return collect(WLCountry.SAUDI_ARABIA, WLCountry.UNITED_ARAB_EMIRATES, WLCountry.YEMEN);

            case PAKISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.INDIA, WLCountry.IRAN, WLCountry.CHINA);
            case PALAU: return null;
            case PALESTINE: return collect(WLCountry.EGYPT, WLCountry.ISRAEL, WLCountry.JORDAN);
            case PANAMA: return collect(WLCountry.COLOMBIA, WLCountry.COSTA_RICA);
            case PAPUA_NEW_GUINEA: return collect(WLCountry.INDONESIA);
            case PARAGUAY: return collect(WLCountry.ARGENTINA, WLCountry.BOLIVIA, WLCountry.BRAZIL);
            case PERU: return collect(WLCountry.BOLIVIA, WLCountry.BRAZIL, WLCountry.CHILE, WLCountry.COLOMBIA, WLCountry.ECUADOR);
            case PHILIPPINES: return null;
            case POLAND: return collect(WLCountry.BELARUS, WLCountry.CZECH_REPUBLIC, WLCountry.GERMANY, WLCountry.LITHUANIA, WLCountry.RUSSIA, WLCountry.SLOVAKIA, WLCountry.UKRAINE);
            case PORTUGAL: return collect(WLCountry.SPAIN);

            case QATAR: return collect(WLCountry.SAUDI_ARABIA);

            case ROMANIA: return collect(WLCountry.BULGARIA, WLCountry.HUNGARY, WLCountry.MOLDOVA, WLCountry.SERBIA, WLCountry.UKRAINE);
            case RUSSIA: return collect(WLCountry.AZERBAIJAN, WLCountry.BELARUS, WLCountry.CHINA, WLCountry.ESTONIA, WLCountry.FINLAND, WLCountry.GEORGIA, WLCountry.KAZAKHSTAN, WLCountry.NORTH_KOREA, WLCountry.LATVIA, WLCountry.LITHUANIA, WLCountry.MONGOLIA, WLCountry.NORWAY, WLCountry.POLAND, WLCountry.UKRAINE, WLCountry.ABKHAZIA);
            case RWANDA: return collect(WLCountry.BURUNDI, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.TANZANIA, WLCountry.UGANDA);

            case SAINT_KITTS_AND_NEVIS: return null;
            case SAINT_LUCIA: return null;
            case SAINT_VINCENT_AND_THE_GRENADINES: return null;
            case SAMOA: return null;
            case SAN_MARINO: return collect(WLCountry.ITALY);
            case SAO_TOME_AND_PRINCIPE: return null;
            case SAUDI_ARABIA: return collect(WLCountry.IRAQ, WLCountry.JORDAN, WLCountry.KUWAIT, WLCountry.OMAN, WLCountry.QATAR, WLCountry.UNITED_ARAB_EMIRATES, WLCountry.YEMEN);
            case SENEGAL: return collect(WLCountry.GAMBIA, WLCountry.GUINEA, WLCountry.GUINEA_BISSAU, WLCountry.MALI, WLCountry.MAURITANIA);
            case SERBIA: return collect(WLCountry.BOSNIA_AND_HERZEGOVINA, WLCountry.BULGARIA, WLCountry.CROATIA, WLCountry.HUNGARY, WLCountry.KOSOVO, WLCountry.MONTENEGRO, WLCountry.NORTH_MACEDONIA, WLCountry.ROMANIA);
            case SEYCHELLES: return null;
            case SIERRA_LEONE: return collect(WLCountry.GUINEA, WLCountry.LIBERIA);
            case SINGAPORE: return null;
            case SLOVAKIA: return collect(WLCountry.AUSTRIA, WLCountry.CZECH_REPUBLIC, WLCountry.HUNGARY, WLCountry.POLAND, WLCountry.UKRAINE);
            case SLOVENIA: return collect(WLCountry.AUSTRIA, WLCountry.CROATIA, WLCountry.ITALY, WLCountry.HUNGARY);
            case SOLOMON_ISLANDS: return null;
            case SOMALIA: return collect(WLCountry.DJIBOUTI, WLCountry.ETHIOPIA, WLCountry.KENYA);
            case SOUTH_AFRICA: return collect(WLCountry.BOTSWANA, WLCountry.ESWATINI, WLCountry.LESOTHO, WLCountry.MOZAMBIQUE, WLCountry.NAMIBIA, WLCountry.ZIMBABWE);
            case SOUTH_KOREA: return collect(WLCountry.NORTH_KOREA);
            case SOUTH_SUDAN: return collect(WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.ETHIOPIA, WLCountry.KENYA, WLCountry.SUDAN, WLCountry.UGANDA);
            case SPAIN: return collect(WLCountry.FRANCE, WLCountry.GIBRALTAR, WLCountry.PORTUGAL, WLCountry.MOROCCO);
            case SRI_LANKA: return null;
            case SUDAN: return collect(WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.CHAD, WLCountry.EGYPT, WLCountry.ERITREA, WLCountry.ETHIOPIA, WLCountry.LIBYA, WLCountry.SOUTH_SUDAN);
            case SURINAME: return collect(WLCountry.BRAZIL, WLCountry.GUYANA);
            case SWEDEN: return collect(WLCountry.FINLAND, WLCountry.NORWAY);
            case SWITZERLAND: return collect(WLCountry.AUSTRIA, WLCountry.FRANCE, WLCountry.ITALY, WLCountry.LIECHTENSTEIN, WLCountry.GERMANY);
            case SYRIA: return collect(WLCountry.IRAQ, WLCountry.ISRAEL, WLCountry.JORDAN, WLCountry.LEBANON, WLCountry.TURKEY);
            case TAIWAN: return null;
            case TAJIKISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.CHINA, WLCountry.KYRGYZSTAN, WLCountry.UZBEKISTAN);
            case TANZANIA: return collect(WLCountry.BURUNDI, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.KENYA, WLCountry.MALAWI, WLCountry.MOZAMBIQUE, WLCountry.RWANDA, WLCountry.UGANDA, WLCountry.ZAMBIA);
            case THAILAND: return collect(WLCountry.CAMBODIA, WLCountry.LAOS, WLCountry.MALAYSIA, WLCountry.MYANMAR);
            case TOGO: return collect(WLCountry.BENIN, WLCountry.BURKINA_FASO, WLCountry.GHANA);
            case TONGA: return null;
            case TRINIDAD_AND_TOBAGO: return null;
            case TUNISIA: return collect(WLCountry.ALGERIA, WLCountry.LIBYA);
            case TURKEY: return collect(WLCountry.ARMENIA, WLCountry.AZERBAIJAN, WLCountry.BULGARIA, WLCountry.GEORGIA, WLCountry.GREECE, WLCountry.IRAN, WLCountry.IRAQ, WLCountry.SYRIA);
            case TURKMENISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.IRAN, WLCountry.KAZAKHSTAN, WLCountry.UZBEKISTAN);
            case TUVALU: return null;

            case UGANDA: return collect(WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.KENYA, WLCountry.RWANDA, WLCountry.SOUTH_SUDAN, WLCountry.TANZANIA);
            case UKRAINE: return collect(WLCountry.BELARUS, WLCountry.HUNGARY, WLCountry.MOLDOVA, WLCountry.POLAND, WLCountry.ROMANIA, WLCountry.RUSSIA, WLCountry.SLOVAKIA);
            case UNITED_ARAB_EMIRATES: return collect(WLCountry.OMAN, WLCountry.SAUDI_ARABIA);
            case UNITED_KINGDOM: return collect(WLCountry.CYPRUS, WLCountry.IRELAND, WLCountry.SPAIN);
            case UNITED_STATES: return collect(WLCountry.CANADA, WLCountry.MEXICO);
            case URUGUAY: return collect(WLCountry.ARGENTINA, WLCountry.BRAZIL);
            case UZBEKISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.KAZAKHSTAN, WLCountry.KYRGYZSTAN, WLCountry.TAJIKISTAN, WLCountry.TURKMENISTAN);

            case VANUATU: return null;
            case VATICAN_CITY: return collect(WLCountry.ITALY);
            case VENEZUELA: return collect(WLCountry.BRAZIL, WLCountry.COLOMBIA, WLCountry.GUYANA);
            case VIETNAM: return collect(WLCountry.CAMBODIA, WLCountry.CHINA, WLCountry.LAOS);

            case WESTERN_SAHARA: return collect(WLCountry.ALGERIA, WLCountry.MAURITANIA, WLCountry.MOROCCO);

            case YEMEN: return collect(WLCountry.OMAN, WLCountry.SAUDI_ARABIA);

            case ZAMBIA: return collect(WLCountry.ANGOLA, WLCountry.BOTSWANA, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.MALAWI, WLCountry.MOZAMBIQUE, WLCountry.NAMIBIA, WLCountry.TANZANIA, WLCountry.ZIMBABWE);
            case ZIMBABWE: return collect(WLCountry.BOTSWANA, WLCountry.MOZAMBIQUE, WLCountry.SOUTH_AFRICA, WLCountry.ZAMBIA);
            default: return null;
        }
    }
}
