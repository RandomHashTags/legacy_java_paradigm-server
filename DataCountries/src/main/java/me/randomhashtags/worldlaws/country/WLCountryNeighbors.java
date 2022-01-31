package me.randomhashtags.worldlaws.country;

public enum WLCountryNeighbors { // https://en.wikipedia.org/wiki/List_of_countries_and_territories_by_land_and_maritime_borders
    ;

    private static WLCountry[] collect(WLCountry...countries) {
        return countries;
    }
    public static WLCountry[] get(WLCountry country) {
        switch (country) {
            case ABKHAZIA: return collect(WLCountry.RUSSIA, WLCountry.GEORGIA, WLCountry.TURKEY);
            case AFGHANISTAN: return collect(WLCountry.CHINA, WLCountry.IRAN, WLCountry.PAKISTAN, WLCountry.TAJIKISTAN, WLCountry.TURKMENISTAN, WLCountry.UZBEKISTAN);
            case ALBANIA: return collect(WLCountry.GREECE, WLCountry.ITALY, WLCountry.KOSOVO, WLCountry.MONTENEGRO, WLCountry.NORTH_MACEDONIA);
            case ALGERIA: return collect(WLCountry.FRANCE, WLCountry.ITALY, WLCountry.LIBYA, WLCountry.MALI, WLCountry.MAURITANIA, WLCountry.MOROCCO, WLCountry.NIGER, WLCountry.SPAIN, WLCountry.TUNISIA, WLCountry.WESTERN_SAHARA);
            case AMERICAN_SAMOA: return collect(WLCountry.COOK_ISLANDS, WLCountry.NIUE, WLCountry.TOKELAU, WLCountry.TONGA);
            case ANDORRA: return collect(WLCountry.FRANCE, WLCountry.SPAIN);
            case ANGOLA: return collect(WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.NAMIBIA, WLCountry.ZAMBIA);
            case ANGUILLA: return collect(WLCountry.ANTIGUA_AND_BARBUDA, WLCountry.BRITISH_VIRGIN_ISLANDS, WLCountry.NETHERLANDS, WLCountry.SAINT_BARTHELEMY, WLCountry.SAINT_MARTIN);

            case ANTIGUA_AND_BARBUDA: return collect(WLCountry.ANGUILLA, WLCountry.FRANCE, WLCountry.MONTSERRAT, WLCountry.SAINT_BARTHELEMY, WLCountry.SAINT_KITTS_AND_NEVIS);
            case ARGENTINA: return collect(WLCountry.BOLIVIA, WLCountry.BRAZIL, WLCountry.CHILE, WLCountry.FALKLAND_ISLANDS, WLCountry.PARAGUAY, WLCountry.URUGUAY);
            case ARTSAKH: return collect(WLCountry.ARMENIA, WLCountry.AZERBAIJAN);
            case ARMENIA: return collect(WLCountry.AZERBAIJAN, WLCountry.GEORGIA, WLCountry.IRAN, WLCountry.TURKEY);
            case ARUBA: return collect(WLCountry.DOMINICAN_REPUBLIC, WLCountry.VENEZUELA);
            case AUSTRALIA: return collect(WLCountry.INDONESIA, WLCountry.NEW_ZEALAND, WLCountry.PAPUA_NEW_GUINEA, WLCountry.SOLOMON_ISLANDS, WLCountry.TIMOR_LESTE);
            case AUSTRIA: return collect(WLCountry.CZECH_REPUBLIC, WLCountry.GERMANY, WLCountry.HUNGARY, WLCountry.ITALY, WLCountry.LIECHTENSTEIN, WLCountry.SLOVAKIA, WLCountry.SLOVENIA, WLCountry.SWITZERLAND);
            case AZERBAIJAN: return collect(WLCountry.ARMENIA, WLCountry.GEORGIA, WLCountry.IRAN, WLCountry.KAZAKHSTAN, WLCountry.RUSSIA, WLCountry.TURKEY, WLCountry.TURKMENISTAN);

            case BAHAMAS: return collect(WLCountry.CUBA, WLCountry.HAITI, WLCountry.TURKS_AND_CAICOS_ISLANDS, WLCountry.UNITED_STATES);
            case BAHRAIN: return collect(WLCountry.IRAN, WLCountry.QATAR, WLCountry.SAUDI_ARABIA);
            case BANGLADESH: return collect(WLCountry.INDIA, WLCountry.MYANMAR);
            case BARBADOS: return collect(WLCountry.FRANCE, WLCountry.GUYANA, WLCountry.SAINT_LUCIA, WLCountry.SAINT_VINCENT_AND_THE_GRENADINES, WLCountry.TRINIDAD_AND_TOBAGO, WLCountry.VENEZUELA);
            case BELARUS: return collect(WLCountry.LATVIA, WLCountry.LITHUANIA, WLCountry.POLAND, WLCountry.RUSSIA, WLCountry.UKRAINE);
            case BELGIUM: return collect(WLCountry.FRANCE, WLCountry.GERMANY, WLCountry.LUXEMBOURG, WLCountry.NETHERLANDS, WLCountry.UNITED_KINGDOM);
            case BELIZE: return collect(WLCountry.GUATEMALA, WLCountry.HONDURAS, WLCountry.MEXICO);
            case BENIN: return collect(WLCountry.BURKINA_FASO, WLCountry.NIGER, WLCountry.NIGERIA, WLCountry.TOGO);
            case BERMUDA: return null;
            case BHUTAN: return collect(WLCountry.CHINA, WLCountry.INDIA);
            case BOLIVIA: return collect(WLCountry.ARGENTINA, WLCountry.BRAZIL, WLCountry.CHILE, WLCountry.PARAGUAY, WLCountry.PERU);
            case BOSNIA_AND_HERZEGOVINA: return collect(WLCountry.CROATIA, WLCountry.MONTENEGRO, WLCountry.SERBIA);
            case BOTSWANA: return collect(WLCountry.NAMIBIA, WLCountry.SOUTH_AFRICA, WLCountry.ZAMBIA, WLCountry.ZIMBABWE);
            case BRAZIL: return collect(WLCountry.ARGENTINA, WLCountry.BOLIVIA, WLCountry.COLOMBIA, WLCountry.FRANCE, WLCountry.GUYANA, WLCountry.PARAGUAY, WLCountry.PERU, WLCountry.SURINAME, WLCountry.URUGUAY, WLCountry.VENEZUELA);
            case BRITISH_VIRGIN_ISLANDS: return collect(WLCountry.ANGUILLA, WLCountry.PUERTO_RICO);
            case BRUNEI: return collect(WLCountry.CHINA, WLCountry.MALAYSIA, WLCountry.PHILIPPINES, WLCountry.TAIWAN, WLCountry.VIETNAM);
            case BULGARIA: return collect(WLCountry.GEORGIA, WLCountry.GREECE, WLCountry.NORTH_MACEDONIA, WLCountry.ROMANIA, WLCountry.SERBIA, WLCountry.TURKEY);
            case BURKINA_FASO: return collect(WLCountry.BENIN, WLCountry.IVORY_COAST, WLCountry.GHANA, WLCountry.MALI, WLCountry.NIGER, WLCountry.TOGO);
            case BURUNDI: return collect(WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.RWANDA, WLCountry.TANZANIA);

            case CAMBODIA: return collect(WLCountry.LAOS, WLCountry.THAILAND, WLCountry.VIETNAM);
            case CAMEROON: return collect(WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.CHAD, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.GABON, WLCountry.NIGERIA);
            case CANADA: return collect(WLCountry.GREENLAND, WLCountry.SAINT_PIERRE_AND_MIQUELON, WLCountry.UNITED_STATES);
            case CAPE_VERDE: return collect(WLCountry.GAMBIA, WLCountry.MAURITANIA, WLCountry.SENEGAL);
            case CAYMAN_ISLANDS: return collect(WLCountry.CUBA, WLCountry.HONDURAS, WLCountry.JAMAICA);
            case CENTRAL_AFRICAN_REPUBLIC: return collect(WLCountry.CAMEROON, WLCountry.CHAD, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.SOUTH_SUDAN, WLCountry.SUDAN);
            case CHAD: return collect(WLCountry.CAMEROON, WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.LIBYA, WLCountry.NIGER, WLCountry.NIGERIA, WLCountry.SUDAN);
            case CHILE: return collect(WLCountry.ARGENTINA, WLCountry.BOLIVIA, WLCountry.PERU);
            case CHINA: return collect(WLCountry.AFGHANISTAN, WLCountry.BHUTAN, WLCountry.BRUNEI, WLCountry.HONG_KONG, WLCountry.INDIA, WLCountry.INDONESIA, WLCountry.JAPAN, WLCountry.KAZAKHSTAN, WLCountry.NORTH_KOREA, WLCountry.SOUTH_KOREA, WLCountry.KYRGYZSTAN, WLCountry.LAOS, WLCountry.MACAU, WLCountry.MALAYSIA, WLCountry.MONGOLIA, WLCountry.MYANMAR, WLCountry.NEPAL, WLCountry.PAKISTAN, WLCountry.PHILIPPINES, WLCountry.RUSSIA, WLCountry.TAIWAN, WLCountry.TAJIKISTAN, WLCountry.VIETNAM);
            case COLOMBIA: return collect(WLCountry.BRAZIL, WLCountry.COSTA_RICA, WLCountry.DOMINICAN_REPUBLIC, WLCountry.ECUADOR, WLCountry.HAITI, WLCountry.JAMAICA, WLCountry.NICARAGUA, WLCountry.PANAMA, WLCountry.PERU, WLCountry.VENEZUELA);
            case COMOROS: return collect(WLCountry.FRANCE, WLCountry.MADAGASCAR, WLCountry.MOZAMBIQUE, WLCountry.SEYCHELLES, WLCountry.TANZANIA);
            case COOK_ISLANDS: return collect(WLCountry.AMERICAN_SAMOA, WLCountry.KIRIBATI, WLCountry.NIUE, WLCountry.TOKELAU);
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return collect(WLCountry.ANGOLA, WLCountry.BURUNDI, WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.RWANDA, WLCountry.SOUTH_SUDAN, WLCountry.TANZANIA, WLCountry.UGANDA, WLCountry.ZAMBIA);
            case REPUBLIC_OF_THE_CONGO: return collect(WLCountry.ANGOLA, WLCountry.CAMEROON, WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.GABON);
            case COSTA_RICA: return collect(WLCountry.COLOMBIA, WLCountry.ECUADOR, WLCountry.NICARAGUA, WLCountry.PANAMA);
            case IVORY_COAST: return collect(WLCountry.BURKINA_FASO, WLCountry.GHANA, WLCountry.GUINEA, WLCountry.LIBERIA, WLCountry.MALI);
            case CROATIA: return collect(WLCountry.BOSNIA_AND_HERZEGOVINA, WLCountry.HUNGARY, WLCountry.ITALY, WLCountry.MONTENEGRO, WLCountry.SERBIA, WLCountry.SLOVENIA);
            case CUBA: return collect(WLCountry.BAHAMAS, WLCountry.CAYMAN_ISLANDS, WLCountry.HAITI, WLCountry.HONDURAS, WLCountry.JAMAICA, WLCountry.MEXICO, WLCountry.UNITED_STATES);
            case CYPRUS: return collect(WLCountry.EGYPT, WLCountry.GREECE, WLCountry.ISRAEL, WLCountry.LEBANON, WLCountry.NORTHERN_CYPRUS, WLCountry.SYRIA, WLCountry.TURKEY);
            case CZECH_REPUBLIC: return collect(WLCountry.AUSTRIA, WLCountry.GERMANY, WLCountry.POLAND, WLCountry.SLOVAKIA);

            case DENMARK: return collect(WLCountry.GERMANY, WLCountry.NORWAY, WLCountry.POLAND, WLCountry.SWEDEN, WLCountry.UNITED_KINGDOM);
            case DJIBOUTI: return collect(WLCountry.ERITREA, WLCountry.ETHIOPIA, WLCountry.SOMALILAND, WLCountry.YEMEN);
            case DOMINICA: return collect(WLCountry.FRANCE, WLCountry.VENEZUELA);
            case DOMINICAN_REPUBLIC: return collect(WLCountry.ARUBA, WLCountry.COLOMBIA, WLCountry.HAITI, WLCountry.PUERTO_RICO, WLCountry.TURKS_AND_CAICOS_ISLANDS, WLCountry.VENEZUELA);

            case TIMOR_LESTE: return collect(WLCountry.AUSTRALIA, WLCountry.INDONESIA);
            case ECUADOR: return collect(WLCountry.COLOMBIA, WLCountry.COSTA_RICA, WLCountry.PERU);
            case EGYPT: return collect(WLCountry.CYPRUS, WLCountry.GREECE, WLCountry.ISRAEL, WLCountry.JORDAN, WLCountry.LIBYA, WLCountry.SAUDI_ARABIA, WLCountry.SUDAN, WLCountry.TURKEY, WLCountry.PALESTINE);
            case EL_SALVADOR: return collect(WLCountry.GUATEMALA, WLCountry.HONDURAS, WLCountry.NICARAGUA);
            case ERITREA: return collect(WLCountry.DJIBOUTI, WLCountry.ETHIOPIA, WLCountry.SAUDI_ARABIA, WLCountry.SUDAN, WLCountry.YEMEN);
            case ESTONIA: return collect(WLCountry.FINLAND, WLCountry.LATVIA, WLCountry.RUSSIA, WLCountry.SWEDEN);
            case ESWATINI: return collect(WLCountry.MOZAMBIQUE, WLCountry.SOUTH_AFRICA);
            case ETHIOPIA: return collect(WLCountry.DJIBOUTI, WLCountry.ERITREA, WLCountry.KENYA, WLCountry.SOMALILAND, WLCountry.SOUTH_SUDAN, WLCountry.SUDAN);

            case FALKLAND_ISLANDS: return collect(WLCountry.ARGENTINA);
            case FIJI: return collect(WLCountry.NEW_ZEALAND, WLCountry.SOLOMON_ISLANDS, WLCountry.TONGA, WLCountry.TUVALU, WLCountry.VANUATU, WLCountry.WALLIS_AND_FUTUNA);
            case FINLAND: return collect(WLCountry.ESTONIA, WLCountry.NORWAY, WLCountry.RUSSIA, WLCountry.SWEDEN);
            case FRANCE: return collect(WLCountry.ALGERIA, WLCountry.ANDORRA, WLCountry.ANTIGUA_AND_BARBUDA, WLCountry.BARBADOS, WLCountry.BELGIUM, WLCountry.BRAZIL, WLCountry.COMOROS, WLCountry.DOMINICA, WLCountry.GERMANY, WLCountry.GUERNSEY, WLCountry.ITALY, WLCountry.JERSEY, WLCountry.LUXEMBOURG, WLCountry.MADAGASCAR, WLCountry.MAURITIUS, WLCountry.MONACO, WLCountry.MONTSERRAT, WLCountry.SAINT_LUCIA, WLCountry.SPAIN, WLCountry.SWITZERLAND, WLCountry.SURINAME, WLCountry.UNITED_KINGDOM, WLCountry.VENEZUELA);

            case GABON: return collect(WLCountry.CAMEROON, WLCountry.REPUBLIC_OF_THE_CONGO, WLCountry.SAO_TOME_AND_PRINCIPE);
            case GAMBIA: return collect(WLCountry.SENEGAL);
            case GEORGIA: return collect(WLCountry.ARMENIA, WLCountry.AZERBAIJAN, WLCountry.BULGARIA, WLCountry.RUSSIA, WLCountry.TURKEY);
            case GERMANY: return collect(WLCountry.AUSTRIA, WLCountry.BELGIUM, WLCountry.CZECH_REPUBLIC, WLCountry.DENMARK, WLCountry.FRANCE, WLCountry.LUXEMBOURG, WLCountry.NETHERLANDS, WLCountry.POLAND, WLCountry.SWEDEN, WLCountry.SWITZERLAND, WLCountry.UNITED_KINGDOM);
            case GHANA: return collect(WLCountry.BURKINA_FASO, WLCountry.IVORY_COAST, WLCountry.TOGO);
            case GIBRALTAR: return collect(WLCountry.SPAIN);
            case GREECE: return collect(WLCountry.ALBANIA, WLCountry.BULGARIA, WLCountry.CYPRUS, WLCountry.EGYPT, WLCountry.ITALY, WLCountry.LIBYA, WLCountry.NORTH_MACEDONIA, WLCountry.TURKEY);
            case GREENLAND: return collect(WLCountry.CANADA, WLCountry.ICELAND);
            case GRENADA: return collect(WLCountry.SAINT_VINCENT_AND_THE_GRENADINES, WLCountry.TRINIDAD_AND_TOBAGO, WLCountry.VENEZUELA);
            case GUAM: return collect(WLCountry.MICRONESIA, WLCountry.NORTHERN_MARIANA_ISLANDS);
            case GUATEMALA: return collect(WLCountry.BELIZE, WLCountry.EL_SALVADOR, WLCountry.HONDURAS, WLCountry.MEXICO);
            case GUERNSEY: return collect(WLCountry.FRANCE, WLCountry.JERSEY, WLCountry.UNITED_KINGDOM);
            case GUINEA: return collect(WLCountry.IVORY_COAST, WLCountry.GUINEA_BISSAU, WLCountry.LIBERIA, WLCountry.MALI, WLCountry.SENEGAL, WLCountry.SIERRA_LEONE);
            case GUINEA_BISSAU: return collect(WLCountry.GUINEA, WLCountry.SENEGAL);
            case GUYANA: return collect(WLCountry.BARBADOS, WLCountry.BRAZIL, WLCountry.SURINAME, WLCountry.TRINIDAD_AND_TOBAGO, WLCountry.VENEZUELA);

            case HAITI: return collect(WLCountry.COLOMBIA, WLCountry.CUBA, WLCountry.DOMINICAN_REPUBLIC, WLCountry.JAMAICA, WLCountry.TURKS_AND_CAICOS_ISLANDS);
            case HONDURAS: return collect(WLCountry.BELIZE, WLCountry.CAYMAN_ISLANDS, WLCountry.CUBA, WLCountry.EL_SALVADOR, WLCountry.GUATEMALA, WLCountry.JAMAICA, WLCountry.MEXICO, WLCountry.NICARAGUA);
            case HONG_KONG: return collect(WLCountry.CHINA, WLCountry.MACAU);
            case HUNGARY: return collect(WLCountry.AUSTRIA, WLCountry.CROATIA, WLCountry.ROMANIA, WLCountry.SERBIA, WLCountry.SLOVAKIA, WLCountry.SLOVENIA, WLCountry.UKRAINE);

            case ICELAND: return collect(WLCountry.FAROE_ISLANDS, WLCountry.GREENLAND);
            case INDIA: return collect(WLCountry.BANGLADESH, WLCountry.BHUTAN, WLCountry.CHINA, WLCountry.INDONESIA, WLCountry.MALDIVES, WLCountry.MYANMAR, WLCountry.NEPAL, WLCountry.PAKISTAN, WLCountry.SRI_LANKA, WLCountry.THAILAND);
            case INDONESIA: return collect(WLCountry.AUSTRALIA, WLCountry.CHINA, WLCountry.TIMOR_LESTE, WLCountry.INDIA, WLCountry.MALAYSIA, WLCountry.PALAU, WLCountry.PAPUA_NEW_GUINEA, WLCountry.PHILIPPINES, WLCountry.SINGAPORE, WLCountry.TAIWAN, WLCountry.THAILAND, WLCountry.VIETNAM);
            case IRAN: return collect(WLCountry.AFGHANISTAN, WLCountry.ARMENIA, WLCountry.AZERBAIJAN, WLCountry.BAHRAIN, WLCountry.IRAQ, WLCountry.KAZAKHSTAN, WLCountry.KUWAIT, WLCountry.OMAN, WLCountry.PAKISTAN, WLCountry.QATAR, WLCountry.RUSSIA, WLCountry.SAUDI_ARABIA, WLCountry.TURKEY, WLCountry.TURKMENISTAN, WLCountry.UNITED_ARAB_EMIRATES);
            case IRAQ: return collect(WLCountry.IRAN, WLCountry.JORDAN, WLCountry.KUWAIT, WLCountry.SAUDI_ARABIA, WLCountry.SYRIA, WLCountry.TURKEY);
            case IRELAND: return collect(WLCountry.UNITED_KINGDOM);
            case ISRAEL: return collect(WLCountry.CYPRUS, WLCountry.EGYPT, WLCountry.JORDAN, WLCountry.LEBANON, WLCountry.PALESTINE, WLCountry.SYRIA);
            case ITALY: return collect(WLCountry.ALBANIA, WLCountry.ALGERIA, WLCountry.AUSTRIA, WLCountry.CROATIA, WLCountry.FRANCE, WLCountry.GREECE, WLCountry.LIBYA, WLCountry.MALTA, WLCountry.MONTENEGRO, WLCountry.SAN_MARINO, WLCountry.SLOVENIA, WLCountry.SPAIN, WLCountry.SWITZERLAND, WLCountry.TUNISIA, WLCountry.VATICAN_CITY);

            case JAMAICA: return collect(WLCountry.CAYMAN_ISLANDS, WLCountry.COLOMBIA, WLCountry.CUBA, WLCountry.HAITI, WLCountry.HONDURAS, WLCountry.NICARAGUA);
            case JAPAN: return collect(WLCountry.CHINA, WLCountry.PALAU, WLCountry.PHILIPPINES, WLCountry.RUSSIA, WLCountry.SOUTH_KOREA, WLCountry.NORTHERN_MARIANA_ISLANDS, WLCountry.TAIWAN);
            case JERSEY: return collect(WLCountry.FRANCE, WLCountry.GUERNSEY);
            case JORDAN: return collect(WLCountry.EGYPT, WLCountry.IRAQ, WLCountry.ISRAEL, WLCountry.PALESTINE, WLCountry.SAUDI_ARABIA, WLCountry.SYRIA);

            case KAZAKHSTAN: return collect(WLCountry.AZERBAIJAN, WLCountry.CHINA, WLCountry.KYRGYZSTAN, WLCountry.RUSSIA, WLCountry.TURKMENISTAN, WLCountry.UZBEKISTAN);
            case KENYA: return collect(WLCountry.ETHIOPIA, WLCountry.SOMALIA, WLCountry.SOUTH_SUDAN, WLCountry.TANZANIA, WLCountry.UGANDA);
            case KIRIBATI: return collect(WLCountry.COOK_ISLANDS, WLCountry.MARSHALL_ISLANDS, WLCountry.NAURU, WLCountry.TOKELAU, WLCountry.TUVALU);
            case KOSOVO: return collect(WLCountry.ALBANIA, WLCountry.MONTENEGRO, WLCountry.NORTH_MACEDONIA, WLCountry.SERBIA);
            case KUWAIT: return collect(WLCountry.IRAN, WLCountry.IRAQ, WLCountry.SAUDI_ARABIA);
            case KYRGYZSTAN: return collect(WLCountry.CHINA, WLCountry.KAZAKHSTAN, WLCountry.TAJIKISTAN, WLCountry.UZBEKISTAN);

            case LAOS: return collect(WLCountry.CAMBODIA, WLCountry.CHINA, WLCountry.MYANMAR, WLCountry.THAILAND, WLCountry.VIETNAM);
            case LATVIA: return collect(WLCountry.BELARUS, WLCountry.ESTONIA, WLCountry.LITHUANIA, WLCountry.RUSSIA, WLCountry.SWEDEN);
            case LEBANON: return collect(WLCountry.CYPRUS, WLCountry.ISRAEL, WLCountry.SYRIA);
            case LESOTHO: return collect(WLCountry.SOUTH_AFRICA);
            case LIBERIA: return collect(WLCountry.GUINEA, WLCountry.IVORY_COAST, WLCountry.SIERRA_LEONE);
            case LIBYA: return collect(WLCountry.ALGERIA, WLCountry.CHAD, WLCountry.EGYPT, WLCountry.GREECE, WLCountry.ITALY, WLCountry.MALTA, WLCountry.NIGER, WLCountry.SUDAN, WLCountry.TUNISIA, WLCountry.TURKEY);
            case LIECHTENSTEIN: return collect(WLCountry.AUSTRIA, WLCountry.SWITZERLAND);
            case LITHUANIA: return collect(WLCountry.BELARUS, WLCountry.LATVIA, WLCountry.POLAND, WLCountry.RUSSIA, WLCountry.SWEDEN);
            case LUXEMBOURG: return collect(WLCountry.BELGIUM, WLCountry.FRANCE, WLCountry.GERMANY);

            case MACAU: return collect(WLCountry.CHINA, WLCountry.HONG_KONG);
            case MADAGASCAR: return collect(WLCountry.COMOROS, WLCountry.FRANCE, WLCountry.MAURITIUS, WLCountry.MOZAMBIQUE, WLCountry.SEYCHELLES);
            case MALAWI: return collect(WLCountry.MOZAMBIQUE, WLCountry.TANZANIA, WLCountry.ZAMBIA);
            case MALAYSIA: return collect(WLCountry.BRUNEI, WLCountry.CHINA, WLCountry.INDONESIA, WLCountry.PHILIPPINES, WLCountry.SINGAPORE, WLCountry.TAIWAN, WLCountry.THAILAND);
            case MALDIVES: return collect(WLCountry.INDIA, WLCountry.SRI_LANKA);
            case MALI: return collect(WLCountry.ALGERIA, WLCountry.BURKINA_FASO, WLCountry.IVORY_COAST, WLCountry.GUINEA, WLCountry.MAURITANIA, WLCountry.NIGER, WLCountry.SENEGAL);
            case MALTA: return collect(WLCountry.ITALY, WLCountry.LIBYA, WLCountry.TUNISIA);
            case MARSHALL_ISLANDS: return collect(WLCountry.KIRIBATI, WLCountry.MICRONESIA, WLCountry.NAURU);
            case MAURITANIA: return collect(WLCountry.ALGERIA, WLCountry.CAPE_VERDE, WLCountry.MALI, WLCountry.SENEGAL, WLCountry.WESTERN_SAHARA);
            case MAURITIUS: return collect(WLCountry.FRANCE, WLCountry.MADAGASCAR, WLCountry.SEYCHELLES);
            case MEXICO: return collect(WLCountry.BELIZE, WLCountry.CUBA, WLCountry.GUATEMALA, WLCountry.HONDURAS, WLCountry.UNITED_STATES);
            case MICRONESIA: return collect(WLCountry.GUAM, WLCountry.MARSHALL_ISLANDS, WLCountry.PALAU, WLCountry.PAPUA_NEW_GUINEA);
            case MOLDOVA: return collect(WLCountry.ROMANIA, WLCountry.UKRAINE);
            case MONACO: return collect(WLCountry.FRANCE);
            case MONGOLIA: return collect(WLCountry.CHINA, WLCountry.RUSSIA);
            case MONTENEGRO: return collect(WLCountry.ALBANIA, WLCountry.BOSNIA_AND_HERZEGOVINA, WLCountry.CROATIA, WLCountry.ITALY, WLCountry.KOSOVO, WLCountry.SERBIA);
            case MONTSERRAT: return collect(WLCountry.ANTIGUA_AND_BARBUDA, WLCountry.FRANCE, WLCountry.SAINT_KITTS_AND_NEVIS, WLCountry.VENEZUELA);
            case MOROCCO: return collect(WLCountry.ALGERIA, WLCountry.PORTUGAL, WLCountry.SPAIN, WLCountry.WESTERN_SAHARA);
            case MOZAMBIQUE: return collect(WLCountry.COMOROS, WLCountry.ESWATINI, WLCountry.MADAGASCAR, WLCountry.MALAWI, WLCountry.SOUTH_AFRICA, WLCountry.TANZANIA, WLCountry.ZAMBIA, WLCountry.ZIMBABWE);
            case MYANMAR: return collect(WLCountry.BANGLADESH, WLCountry.CHINA, WLCountry.INDIA, WLCountry.LAOS, WLCountry.THAILAND);

            case NAMIBIA: return collect(WLCountry.ANGOLA, WLCountry.BOTSWANA, WLCountry.SOUTH_AFRICA, WLCountry.ZAMBIA);
            case NAURU: return collect(WLCountry.KIRIBATI, WLCountry.MARSHALL_ISLANDS);
            case NEPAL: return collect(WLCountry.CHINA, WLCountry.INDIA);

            case NETHERLANDS: return collect(WLCountry.ANGUILLA, WLCountry.BELGIUM, WLCountry.GERMANY, WLCountry.SAINT_BARTHELEMY, WLCountry.SAINT_MARTIN, WLCountry.UNITED_KINGDOM, WLCountry.VENEZUELA);
            case NEW_ZEALAND: return collect(WLCountry.AUSTRALIA, WLCountry.FIJI, WLCountry.NORFOLK_ISLAND);
            case NICARAGUA: return collect(WLCountry.COLOMBIA, WLCountry.COSTA_RICA, WLCountry.EL_SALVADOR, WLCountry.HONDURAS, WLCountry.JAMAICA, WLCountry.PANAMA);
            case NIGER: return collect(WLCountry.ALGERIA, WLCountry.BENIN, WLCountry.BURKINA_FASO, WLCountry.CHAD, WLCountry.LIBYA, WLCountry.MALI, WLCountry.NIGERIA);
            case NIGERIA: return collect(WLCountry.BENIN, WLCountry.CAMEROON, WLCountry.CHAD, WLCountry.NIGER, WLCountry.SAO_TOME_AND_PRINCIPE);
            case NIUE: return collect(WLCountry.AMERICAN_SAMOA, WLCountry.COOK_ISLANDS, WLCountry.TONGA);
            case NORFOLK_ISLAND: return collect(WLCountry.NEW_ZEALAND);
            case NORTH_KOREA: return collect(WLCountry.CHINA, WLCountry.SOUTH_KOREA, WLCountry.RUSSIA);
            case NORTH_MACEDONIA: return collect(WLCountry.ALBANIA, WLCountry.BULGARIA, WLCountry.GREECE, WLCountry.KOSOVO, WLCountry.SERBIA);
            case NORTHERN_CYPRUS: return collect(WLCountry.CYPRUS, WLCountry.SYRIA, WLCountry.TURKEY);
            case NORTHERN_MARIANA_ISLANDS: return collect(WLCountry.GUAM, WLCountry.JAPAN);
            case NORWAY: return collect(WLCountry.DENMARK, WLCountry.FAROE_ISLANDS, WLCountry.FINLAND, WLCountry.RUSSIA, WLCountry.SWEDEN, WLCountry.UNITED_KINGDOM);

            case OMAN: return collect(WLCountry.IRAN, WLCountry.PAKISTAN, WLCountry.SAUDI_ARABIA, WLCountry.UNITED_ARAB_EMIRATES, WLCountry.YEMEN);

            case PAKISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.CHINA, WLCountry.INDIA, WLCountry.IRAN, WLCountry.OMAN);
            case PALAU: return collect(WLCountry.INDONESIA, WLCountry.JAPAN, WLCountry.MICRONESIA, WLCountry.PHILIPPINES);
            case PALESTINE: return collect(WLCountry.EGYPT, WLCountry.ISRAEL, WLCountry.JORDAN);
            case PANAMA: return collect(WLCountry.COLOMBIA, WLCountry.COSTA_RICA, WLCountry.NICARAGUA);
            case PAPUA_NEW_GUINEA: return collect(WLCountry.AUSTRALIA, WLCountry.INDONESIA, WLCountry.MICRONESIA, WLCountry.SOLOMON_ISLANDS);
            case PARAGUAY: return collect(WLCountry.ARGENTINA, WLCountry.BOLIVIA, WLCountry.BRAZIL);
            case PERU: return collect(WLCountry.BOLIVIA, WLCountry.BRAZIL, WLCountry.CHILE, WLCountry.COLOMBIA, WLCountry.ECUADOR);
            case PHILIPPINES: return collect(WLCountry.BRUNEI, WLCountry.CHINA, WLCountry.INDONESIA, WLCountry.JAPAN, WLCountry.MALAYSIA, WLCountry.PALAU, WLCountry.TAIWAN, WLCountry.VIETNAM);
            case POLAND: return collect(WLCountry.BELARUS, WLCountry.CZECH_REPUBLIC, WLCountry.DENMARK, WLCountry.GERMANY, WLCountry.LITHUANIA, WLCountry.RUSSIA, WLCountry.SLOVAKIA, WLCountry.SWEDEN, WLCountry.UKRAINE);
            case PORTUGAL: return collect(WLCountry.MOROCCO, WLCountry.SPAIN);
            case PUERTO_RICO: return collect(WLCountry.BRITISH_VIRGIN_ISLANDS, WLCountry.DOMINICAN_REPUBLIC, WLCountry.VENEZUELA);

            case QATAR: return collect(WLCountry.BAHRAIN, WLCountry.IRAN, WLCountry.SAUDI_ARABIA, WLCountry.UNITED_ARAB_EMIRATES);

            case ROMANIA: return collect(WLCountry.BULGARIA, WLCountry.HUNGARY, WLCountry.MOLDOVA, WLCountry.RUSSIA, WLCountry.SERBIA, WLCountry.UKRAINE);
            case RUSSIA: return collect(WLCountry.AZERBAIJAN, WLCountry.BELARUS, WLCountry.CHINA, WLCountry.ESTONIA, WLCountry.FINLAND, WLCountry.GEORGIA, WLCountry.IRAN, WLCountry.JAPAN, WLCountry.KAZAKHSTAN, WLCountry.NORTH_KOREA, WLCountry.LATVIA, WLCountry.LITHUANIA, WLCountry.MONGOLIA, WLCountry.NORWAY, WLCountry.POLAND, WLCountry.ROMANIA, WLCountry.SWEDEN, WLCountry.TURKEY, WLCountry.UKRAINE, WLCountry.UNITED_STATES);
            case RWANDA: return collect(WLCountry.BURUNDI, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.TANZANIA, WLCountry.UGANDA);

            case SAINT_BARTHELEMY: return collect(WLCountry.ANGUILLA, WLCountry.ANTIGUA_AND_BARBUDA, WLCountry.NETHERLANDS, WLCountry.SAINT_KITTS_AND_NEVIS, WLCountry.SAINT_MARTIN);
            case SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA: return null;
            case SAINT_KITTS_AND_NEVIS: return collect(WLCountry.ANTIGUA_AND_BARBUDA, WLCountry.MONTSERRAT, WLCountry.NETHERLANDS, WLCountry.SAINT_BARTHELEMY, WLCountry.VENEZUELA);
            case SAINT_LUCIA: return collect(WLCountry.BARBADOS, WLCountry.FRANCE, WLCountry.SAINT_VINCENT_AND_THE_GRENADINES, WLCountry.VENEZUELA);
            case SAINT_PIERRE_AND_MIQUELON: return collect(WLCountry.CANADA);
            case SAINT_MARTIN: return collect(WLCountry.ANGUILLA, WLCountry.NETHERLANDS, WLCountry.SAINT_BARTHELEMY);
            case SAINT_VINCENT_AND_THE_GRENADINES: return collect(WLCountry.BARBADOS, WLCountry.GRENADA, WLCountry.SAINT_LUCIA, WLCountry.TRINIDAD_AND_TOBAGO, WLCountry.VENEZUELA);
            case SAMOA: return collect(WLCountry.AMERICAN_SAMOA, WLCountry.TOKELAU, WLCountry.TONGA, WLCountry.WALLIS_AND_FUTUNA);
            case SAN_MARINO: return collect(WLCountry.ITALY);
            case SAO_TOME_AND_PRINCIPE: return collect(WLCountry.GABON, WLCountry.NIGERIA);
            case SAUDI_ARABIA: return collect(WLCountry.BAHRAIN, WLCountry.EGYPT, WLCountry.ERITREA, WLCountry.IRAN, WLCountry.IRAQ, WLCountry.JORDAN, WLCountry.KUWAIT, WLCountry.OMAN, WLCountry.QATAR, WLCountry.SUDAN, WLCountry.UNITED_ARAB_EMIRATES, WLCountry.YEMEN);
            case SENEGAL: return collect(WLCountry.CAPE_VERDE, WLCountry.GAMBIA, WLCountry.GUINEA, WLCountry.GUINEA_BISSAU, WLCountry.MALI, WLCountry.MAURITANIA);
            case SERBIA: return collect(WLCountry.BOSNIA_AND_HERZEGOVINA, WLCountry.BULGARIA, WLCountry.CROATIA, WLCountry.HUNGARY, WLCountry.KOSOVO, WLCountry.MONTENEGRO, WLCountry.NORTH_MACEDONIA, WLCountry.ROMANIA);
            case SEYCHELLES: return collect(WLCountry.COMOROS, WLCountry.MADAGASCAR, WLCountry.MAURITIUS, WLCountry.TANZANIA);
            case SIERRA_LEONE: return collect(WLCountry.GUINEA, WLCountry.LIBERIA);
            case SINGAPORE: return collect(WLCountry.INDONESIA, WLCountry.MALAYSIA);
            case SLOVAKIA: return collect(WLCountry.AUSTRIA, WLCountry.CZECH_REPUBLIC, WLCountry.HUNGARY, WLCountry.POLAND, WLCountry.UKRAINE);
            case SLOVENIA: return collect(WLCountry.AUSTRIA, WLCountry.CROATIA, WLCountry.ITALY, WLCountry.HUNGARY);
            case SOLOMON_ISLANDS: return collect(WLCountry.FIJI, WLCountry.PAPUA_NEW_GUINEA, WLCountry.VANUATU);
            case SOMALIA: return collect(WLCountry.DJIBOUTI, WLCountry.ETHIOPIA, WLCountry.KENYA, WLCountry.YEMEN);
            case SOMALILAND: return collect(WLCountry.DJIBOUTI, WLCountry.ETHIOPIA, WLCountry.SOMALIA, WLCountry.YEMEN);
            case SOUTH_AFRICA: return collect(WLCountry.BOTSWANA, WLCountry.ESWATINI, WLCountry.LESOTHO, WLCountry.MOZAMBIQUE, WLCountry.NAMIBIA, WLCountry.ZIMBABWE);
            case SOUTH_KOREA: return collect(WLCountry.CHINA, WLCountry.JAPAN, WLCountry.NORTH_KOREA);
            case SOUTH_SUDAN: return collect(WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.ETHIOPIA, WLCountry.KENYA, WLCountry.SUDAN, WLCountry.UGANDA);
            case SPAIN: return collect(WLCountry.ALGERIA, WLCountry.ANDORRA, WLCountry.FRANCE, WLCountry.ITALY, WLCountry.GIBRALTAR, WLCountry.MOROCCO, WLCountry.PORTUGAL, WLCountry.WESTERN_SAHARA);
            case SRI_LANKA: return collect(WLCountry.INDIA, WLCountry.MALDIVES);
            case SUDAN: return collect(WLCountry.CENTRAL_AFRICAN_REPUBLIC, WLCountry.CHAD, WLCountry.EGYPT, WLCountry.ERITREA, WLCountry.ETHIOPIA, WLCountry.LIBYA, WLCountry.SAUDI_ARABIA, WLCountry.SOUTH_SUDAN);
            case SURINAME: return collect(WLCountry.BRAZIL, WLCountry.FRANCE, WLCountry.GUYANA);
            case SWEDEN: return collect(WLCountry.DENMARK, WLCountry.ESTONIA, WLCountry.FINLAND, WLCountry.GERMANY, WLCountry.LATVIA, WLCountry.LITHUANIA, WLCountry.NORWAY, WLCountry.POLAND, WLCountry.RUSSIA);
            case SWITZERLAND: return collect(WLCountry.AUSTRIA, WLCountry.FRANCE, WLCountry.ITALY, WLCountry.LIECHTENSTEIN, WLCountry.GERMANY);
            case SYRIA: return collect(WLCountry.CYPRUS, WLCountry.IRAQ, WLCountry.ISRAEL, WLCountry.JORDAN, WLCountry.LEBANON, WLCountry.NORTHERN_CYPRUS, WLCountry.TURKEY);
            case TAIWAN: return collect(WLCountry.BRUNEI, WLCountry.CHINA, WLCountry.INDONESIA, WLCountry.JAPAN, WLCountry.MALAYSIA, WLCountry.PHILIPPINES, WLCountry.VIETNAM);
            case TAJIKISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.CHINA, WLCountry.KYRGYZSTAN, WLCountry.UZBEKISTAN);
            case TANZANIA: return collect(WLCountry.BURUNDI, WLCountry.COMOROS, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.KENYA, WLCountry.MALAWI, WLCountry.MOZAMBIQUE, WLCountry.RWANDA, WLCountry.SEYCHELLES, WLCountry.UGANDA, WLCountry.ZAMBIA);
            case THAILAND: return collect(WLCountry.CAMBODIA, WLCountry.INDIA, WLCountry.INDONESIA, WLCountry.LAOS, WLCountry.MALAYSIA, WLCountry.MYANMAR, WLCountry.VIETNAM);
            case TOGO: return collect(WLCountry.BENIN, WLCountry.BURKINA_FASO, WLCountry.GHANA);
            case TOKELAU: return collect(WLCountry.AMERICAN_SAMOA, WLCountry.COOK_ISLANDS, WLCountry.KIRIBATI, WLCountry.SAMOA, WLCountry.WALLIS_AND_FUTUNA);
            case TONGA: return collect(WLCountry.AMERICAN_SAMOA, WLCountry.FIJI, WLCountry.NIUE, WLCountry.SAMOA, WLCountry.WALLIS_AND_FUTUNA);
            case TRANSNISTRIA: return collect(WLCountry.MOLDOVA, WLCountry.UKRAINE);
            case TRINIDAD_AND_TOBAGO: return collect(WLCountry.BARBADOS, WLCountry.GRENADA, WLCountry.GUYANA, WLCountry.SAINT_VINCENT_AND_THE_GRENADINES, WLCountry.VENEZUELA);
            case TUNISIA: return collect(WLCountry.ALGERIA, WLCountry.ITALY, WLCountry.LIBYA, WLCountry.MALTA);
            case TURKEY: return collect(WLCountry.ARMENIA, WLCountry.AZERBAIJAN, WLCountry.BULGARIA, WLCountry.CYPRUS, WLCountry.EGYPT, WLCountry.GEORGIA, WLCountry.GREECE, WLCountry.IRAN, WLCountry.IRAQ, WLCountry.NORTHERN_CYPRUS, WLCountry.RUSSIA, WLCountry.SYRIA, WLCountry.UKRAINE, WLCountry.LIBYA);
            case TURKMENISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.AZERBAIJAN, WLCountry.IRAN, WLCountry.KAZAKHSTAN, WLCountry.UZBEKISTAN);
            case TURKS_AND_CAICOS_ISLANDS: return collect(WLCountry.BAHAMAS, WLCountry.DOMINICAN_REPUBLIC, WLCountry.HAITI);
            case TUVALU: return collect(WLCountry.FIJI, WLCountry.KIRIBATI, WLCountry.WALLIS_AND_FUTUNA);

            case UGANDA: return collect(WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.KENYA, WLCountry.RWANDA, WLCountry.SOUTH_SUDAN, WLCountry.TANZANIA);
            case UKRAINE: return collect(WLCountry.BELARUS, WLCountry.HUNGARY, WLCountry.MOLDOVA, WLCountry.POLAND, WLCountry.ROMANIA, WLCountry.RUSSIA, WLCountry.SLOVAKIA, WLCountry.TURKEY);
            case UNITED_ARAB_EMIRATES: return collect(WLCountry.IRAN, WLCountry.OMAN, WLCountry.QATAR, WLCountry.SAUDI_ARABIA);
            case UNITED_KINGDOM: return collect(WLCountry.BELGIUM, WLCountry.DENMARK, WLCountry.FAROE_ISLANDS, WLCountry.FRANCE, WLCountry.GERMANY, WLCountry.GUERNSEY, WLCountry.IRELAND, WLCountry.NETHERLANDS, WLCountry.NORWAY, WLCountry.SPAIN);
            case UNITED_STATES: return collect(WLCountry.BAHAMAS, WLCountry.CANADA, WLCountry.CUBA, WLCountry.MEXICO, WLCountry.RUSSIA);
            case URUGUAY: return collect(WLCountry.ARGENTINA, WLCountry.BRAZIL);
            case UZBEKISTAN: return collect(WLCountry.AFGHANISTAN, WLCountry.KAZAKHSTAN, WLCountry.KYRGYZSTAN, WLCountry.TAJIKISTAN, WLCountry.TURKMENISTAN);

            case VANUATU: return collect(WLCountry.FIJI, WLCountry.SOLOMON_ISLANDS);
            case VATICAN_CITY: return collect(WLCountry.ITALY);
            case VENEZUELA: return collect(WLCountry.ARUBA, WLCountry.BARBADOS, WLCountry.BRAZIL, WLCountry.COLOMBIA, WLCountry.DOMINICA, WLCountry.DOMINICAN_REPUBLIC, WLCountry.FRANCE, WLCountry.GRENADA, WLCountry.GUYANA, WLCountry.MONTSERRAT, WLCountry.NETHERLANDS, WLCountry.PUERTO_RICO, WLCountry.SAINT_KITTS_AND_NEVIS, WLCountry.SAINT_LUCIA, WLCountry.SAINT_VINCENT_AND_THE_GRENADINES, WLCountry.TRINIDAD_AND_TOBAGO);
            case VIETNAM: return collect(WLCountry.BRUNEI, WLCountry.CAMBODIA, WLCountry.CHINA, WLCountry.INDONESIA, WLCountry.LAOS, WLCountry.MALAYSIA, WLCountry.PHILIPPINES, WLCountry.TAIWAN, WLCountry.THAILAND);

            case WALLIS_AND_FUTUNA: return collect(WLCountry.FIJI, WLCountry.SAMOA, WLCountry.TONGA, WLCountry.TUVALU, WLCountry.TOKELAU);
            case WESTERN_SAHARA: return collect(WLCountry.ALGERIA, WLCountry.MAURITANIA, WLCountry.MOROCCO, WLCountry.SPAIN);

            case YEMEN: return collect(WLCountry.DJIBOUTI, WLCountry.ERITREA, WLCountry.OMAN, WLCountry.SAUDI_ARABIA, WLCountry.SOMALIA);

            case ZAMBIA: return collect(WLCountry.ANGOLA, WLCountry.BOTSWANA, WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO, WLCountry.MALAWI, WLCountry.MOZAMBIQUE, WLCountry.NAMIBIA, WLCountry.TANZANIA, WLCountry.ZIMBABWE);
            case ZIMBABWE: return collect(WLCountry.BOTSWANA, WLCountry.MOZAMBIQUE, WLCountry.SOUTH_AFRICA, WLCountry.ZAMBIA);
            default: return null;
        }
    }
}
