package me.randomhashtags.worldlaws.country;

public enum WLCountryCurrencies { // https://en.wikipedia.org/wiki/ISO_4217 | https://en.wikipedia.org/wiki/List_of_circulating_currencies
    ;

    private static WLCurrency[] collect(WLCurrency...currencies) {
        return currencies;
    }
    public static WLCurrency[] get(WLCountry country) {
        switch (country) {
            case ABKHAZIA: return collect(WLCurrency.RUB);
            case AFGHANISTAN: return collect(WLCurrency.AFN);
            case ALBANIA: return collect(WLCurrency.ALL);
            case ALGERIA: return collect(WLCurrency.DZD);
            case AMERICAN_SAMOA: return collect(WLCurrency.USD);
            case ANDORRA: return collect(WLCurrency.EUR);
            case ANGOLA: return collect(WLCurrency.AOA);
            case ANGUILLA: return collect(WLCurrency.XCD);
            case ANTIGUA_AND_BARBUDA: return collect(WLCurrency.XCD);
            case ARGENTINA: return collect(WLCurrency.ARS);
            case ARMENIA: return collect(WLCurrency.AMD);
            case ARTSAKH: return collect(WLCurrency.AMD);
            case ARUBA: return collect(WLCurrency.AWG);
            case AUSTRALIA: return collect(WLCurrency.AUD);
            case AUSTRIA: return collect(WLCurrency.EUR);
            case AZERBAIJAN: return collect(WLCurrency.AZN);

            case BAHAMAS: return collect(WLCurrency.BSD);
            case BAHRAIN: return collect(WLCurrency.BHD);
            case BANGLADESH: return collect(WLCurrency.BDT);
            case BARBADOS: return collect(WLCurrency.BBD);
            case BELARUS: return collect(WLCurrency.BYN);
            case BELGIUM: return collect(WLCurrency.EUR);
            case BELIZE: return collect(WLCurrency.BZD);
            case BENIN: return collect(WLCurrency.XOF);
            case BERMUDA: return collect(WLCurrency.BMD);
            case BHUTAN: return collect(WLCurrency.BTN, WLCurrency.INR);
            case BOLIVIA: return collect(WLCurrency.BOB, WLCurrency.BOV);
            case BOSNIA_AND_HERZEGOVINA: return collect(WLCurrency.BAM);
            case BOTSWANA: return collect(WLCurrency.BWP);
            case BRAZIL: return collect(WLCurrency.BRL);
            case BRITISH_VIRGIN_ISLANDS: return collect(WLCurrency.USD);
            case BRUNEI: return collect(WLCurrency.BND, WLCurrency.SGD);
            case BULGARIA: return collect(WLCurrency.BGN);
            case BURKINA_FASO: return collect(WLCurrency.XOF);
            case BURUNDI: return collect(WLCurrency.BIF);

            case CAPE_VERDE: return collect(WLCurrency.CVE);
            case CAMBODIA: return collect(WLCurrency.KHR);
            case CAMEROON: return collect(WLCurrency.XAF);
            case CANADA: return collect(WLCurrency.CAD);
            case CAYMAN_ISLANDS: return collect(WLCurrency.KYD);
            case CENTRAL_AFRICAN_REPUBLIC: return collect(WLCurrency.XAF);
            case CHAD: return collect(WLCurrency.XAF);
            case CHILE: return collect(WLCurrency.CLF, WLCurrency.CLP);
            case CHINA: return collect(WLCurrency.CNY);
            case COLOMBIA: return collect(WLCurrency.COP, WLCurrency.COU);
            case COMOROS: return collect(WLCurrency.KMF);
            case COOK_ISLANDS: return collect(WLCurrency.CKD, WLCurrency.NZD);
            case COSTA_RICA: return collect(WLCurrency.CRC);
            case CROATIA: return collect(WLCurrency.HRK);
            case CUBA: return collect(WLCurrency.CUC, WLCurrency.CUP);
            case CYPRUS: return collect(WLCurrency.EUR);
            case CZECH_REPUBLIC: return collect(WLCurrency.CZK);

            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return collect(WLCurrency.CDF);
            case DENMARK: return collect(WLCurrency.DKK);
            case DJIBOUTI: return collect(WLCurrency.DJF);
            case DOMINICA: return collect(WLCurrency.XCD);
            case DOMINICAN_REPUBLIC: return collect(WLCurrency.DOP);

            case ECUADOR: return collect(WLCurrency.USD);
            case EGYPT: return collect(WLCurrency.EGP);
            case EL_SALVADOR: return collect(WLCurrency.SVC, WLCurrency.USD);
            case ERITREA: return collect(WLCurrency.ERN);
            case ESTONIA: return collect(WLCurrency.EUR);
            case ESWATINI: return collect(WLCurrency.SZL, WLCurrency.ZAR);
            case ETHIOPIA: return collect(WLCurrency.ETB);

            case FALKLAND_ISLANDS: return collect(WLCurrency.FKP);
            case FAROE_ISLANDS: return collect(WLCurrency.DKK, WLCurrency.FOK);
            case FIJI: return collect(WLCurrency.FJD);
            case FINLAND: return collect(WLCurrency.EUR);
            case FRANCE: return collect(WLCurrency.EUR);

            case GABON: return collect(WLCurrency.XAF);
            case GAMBIA: return collect(WLCurrency.GMD);
            case GEORGIA: return collect(WLCurrency.GEL);
            case GERMANY: return collect(WLCurrency.EUR);
            case GHANA: return collect(WLCurrency.GHS);
            case GIBRALTAR: return collect(WLCurrency.GIP);
            case GREECE: return collect(WLCurrency.EUR);
            case GREENLAND: return collect(WLCurrency.DKK);
            case GRENADA: return collect(WLCurrency.XCD);
            case GUADELOUPE: return collect(WLCurrency.EUR);
            case GUAM: return collect(WLCurrency.USD);
            case GUATEMALA: return collect(WLCurrency.GTQ);
            case GUERNSEY: return collect(WLCurrency.GGP, WLCurrency.GBP);
            case GUINEA: return collect(WLCurrency.GNF);
            case GUINEA_BISSAU: return collect(WLCurrency.XOF);
            case GUYANA: return collect(WLCurrency.GYD);

            case HAITI: return collect(WLCurrency.HTG);
            case HONDURAS: return collect(WLCurrency.HNL);
            case HONG_KONG: return collect(WLCurrency.HKD);
            case HUNGARY: return collect(WLCurrency.HUF);

            case ICELAND: return collect(WLCurrency.ISK);
            case INDIA: return collect(WLCurrency.INR);
            case INDONESIA: return collect(WLCurrency.IDR);
            case IRAN: return collect(WLCurrency.IRR);
            case IRAQ: return collect(WLCurrency.IQD);
            case IRELAND: return collect(WLCurrency.EUR);
            case ISRAEL: return collect(WLCurrency.ILS);
            case ITALY: return collect(WLCurrency.EUR);
            case IVORY_COAST: return collect(WLCurrency.XOF);

            case JAMAICA: return collect(WLCurrency.JMD);
            case JAPAN: return collect(WLCurrency.JPY);
            case JERSEY: return collect(WLCurrency.JEP, WLCurrency.GBP);
            case JORDAN: return collect(WLCurrency.JOD);

            case KAZAKHSTAN: return collect(WLCurrency.KZT);
            case KENYA: return collect(WLCurrency.KES);
            case KIRIBATI: return collect(WLCurrency.KID, WLCurrency.AUD);
            case KUWAIT: return collect(WLCurrency.KWD);
            case KYRGYZSTAN: return collect(WLCurrency.KGS);

            case LAOS: return collect(WLCurrency.LAK);
            case LATVIA: return collect(WLCurrency.EUR);
            case LEBANON: return collect(WLCurrency.LSL);
            case LESOTHO: return collect(WLCurrency.ZAR, WLCurrency.LSL);
            case LIBERIA: return collect(WLCurrency.LRD);
            case LIBYA: return collect(WLCurrency.LYD);
            case LIECHTENSTEIN: return collect(WLCurrency.CHW);
            case LITHUANIA: return collect(WLCurrency.EUR);
            case LUXEMBOURG: return collect(WLCurrency.EUR);

            case MACAU: return collect(WLCurrency.MOP, WLCurrency.HKD);
            case MADAGASCAR: return collect(WLCurrency.MGA);
            case MALAWI: return collect(WLCurrency.MWK);
            case MALAYSIA: return collect(WLCurrency.MYR);
            case MALDIVES: return collect(WLCurrency.MVR);
            case MALI: return collect(WLCurrency.XOF);
            case MALTA: return collect(WLCurrency.EUR);
            case MARSHALL_ISLANDS: return collect(WLCurrency.USD);
            case MAURITANIA: return collect(WLCurrency.MRU);
            case MAURITIUS: return collect(WLCurrency.MUR);
            case MEXICO: return collect(WLCurrency.MXN, WLCurrency.MXV);
            case MICRONESIA: return collect(WLCurrency.USD);
            case MOLDOVA: return collect(WLCurrency.MDL);
            case MONACO: return collect(WLCurrency.EUR);
            case MONGOLIA: return collect(WLCurrency.MNT);
            case MONTENEGRO: return collect(WLCurrency.EUR);
            case MONTSERRAT: return collect(WLCurrency.XCD);
            case MOROCCO: return collect(WLCurrency.MAD);
            case MOZAMBIQUE: return collect(WLCurrency.MZN);
            case MYANMAR: return collect(WLCurrency.MMK);

            case NAURU: return collect(WLCurrency.AUD);
            case NAMIBIA: return collect(WLCurrency.ZAR, WLCurrency.NAD);
            case NEPAL: return collect(WLCurrency.NPR);
            case NETHERLANDS: return collect(WLCurrency.EUR);
            case NEW_ZEALAND: return collect(WLCurrency.NZD);
            case NICARAGUA: return collect(WLCurrency.NIO);
            case NIGER: return collect(WLCurrency.XOF);
            case NIGERIA: return collect(WLCurrency.NGN);
            case NIUE: return collect(WLCurrency.NZD);
            case NORFOLK_ISLAND: return collect(WLCurrency.AUD);
            case NORTH_KOREA: return collect(WLCurrency.KPW);
            case NORTH_MACEDONIA: return collect(WLCurrency.MKD);
            case NORTHERN_MARIANA_ISLANDS: return collect(WLCurrency.USD);
            case NORWAY: return collect(WLCurrency.NOK);

            case OMAN: return collect(WLCurrency.OMR);

            case PAKISTAN: return collect(WLCurrency.PKR);
            case PALAU: return collect(WLCurrency.USD);
            case PALESTINE: return collect(WLCurrency.ILS, WLCurrency.JOD);
            case PANAMA: return collect(WLCurrency.PAB, WLCurrency.USD);
            case PAPUA_NEW_GUINEA: return collect(WLCurrency.PGK);
            case PARAGUAY: return collect(WLCurrency.PYG);
            case PERU: return collect(WLCurrency.PEN);
            case PHILIPPINES: return collect(WLCurrency.PHP);
            case POLAND: return collect(WLCurrency.PLN);
            case PORTUGAL: return collect(WLCurrency.EUR);
            case PUERTO_RICO: return collect(WLCurrency.USD);

            case QATAR: return collect(WLCurrency.QAR);

            case REPUBLIC_OF_THE_CONGO: return collect(WLCurrency.XAF);
            case ROMANIA: return collect(WLCurrency.RON);
            case RUSSIA: return collect(WLCurrency.RUB);
            case RWANDA: return collect(WLCurrency.RWF);

            case SAINT_BARTHELEMY: return collect(WLCurrency.EUR);
            case SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA: return collect(WLCurrency.SHP);
            case SAINT_KITTS_AND_NEVIS: return collect(WLCurrency.XCD);
            case SAINT_LUCIA: return collect(WLCurrency.XCD);
            case SAINT_MARTIN: return collect(WLCurrency.EUR);
            case SAINT_PIERRE_AND_MIQUELON: return collect(WLCurrency.EUR);
            case SAINT_VINCENT_AND_THE_GRENADINES: return collect(WLCurrency.XCD);
            case SAMOA: return collect(WLCurrency.WST);
            case SAN_MARINO: return collect(WLCurrency.EUR);
            case SAO_TOME_AND_PRINCIPE: return collect(WLCurrency.STN);
            case SAUDI_ARABIA: return collect(WLCurrency.SAR);
            case SENEGAL: return collect(WLCurrency.XOF);
            case SERBIA: return collect(WLCurrency.RSD);
            case SEYCHELLES: return collect(WLCurrency.SCR);
            case SIERRA_LEONE: return collect(WLCurrency.SLL);
            case SINGAPORE: return collect(WLCurrency.SGD, WLCurrency.BND);
            case SLOVAKIA: return collect(WLCurrency.EUR);
            case SLOVENIA: return collect(WLCurrency.EUR);
            case SOLOMON_ISLANDS: return collect(WLCurrency.SBD);
            case SOMALIA: return collect(WLCurrency.SOS);
            case SOUTH_AFRICA: return collect(WLCurrency.ZAR);
            case SOUTH_KOREA: return collect(WLCurrency.KRW);
            case SOUTH_SUDAN: return collect(WLCurrency.SSP);
            case SPAIN: return collect(WLCurrency.EUR);
            case SRI_LANKA: return collect(WLCurrency.LKR);
            case SUDAN: return collect(WLCurrency.SDG);
            case SURINAME: return collect(WLCurrency.SRD);
            case SWEDEN: return collect(WLCurrency.SEK);
            case SWITZERLAND: return collect(WLCurrency.CHE, WLCurrency.CHW, WLCurrency.CHF);
            case SYRIA: return collect(WLCurrency.SYP);

            case TAIWAN: return collect(WLCurrency.TWD);
            case TAJIKISTAN: return collect(WLCurrency.TJS);
            case TANZANIA: return collect(WLCurrency.TZS);
            case THAILAND: return collect(WLCurrency.THB);
            case TIMOR_LESTE: return collect(WLCurrency.USD);
            case TOGO: return collect(WLCurrency.XOF);
            case TOKELAU: return collect(WLCurrency.NZD);
            case TONGA: return collect(WLCurrency.TOP);
            case TRANSNISTRIA: return collect(WLCurrency.PRB);
            case TRINIDAD_AND_TOBAGO: return collect(WLCurrency.TTD);
            case TUNISIA: return collect(WLCurrency.TND);
            case TURKEY: return collect(WLCurrency.TRY);
            case TURKMENISTAN: return collect(WLCurrency.TMT);
            case TURKS_AND_CAICOS_ISLANDS: return collect(WLCurrency.USD);
            case TUVALU: return collect(WLCurrency.AUD, WLCurrency.TVD);

            case UGANDA: return collect(WLCurrency.UGX);
            case UKRAINE: return collect(WLCurrency.UAH);
            case UNITED_ARAB_EMIRATES: return collect(WLCurrency.AED);
            case UNITED_KINGDOM: return collect(WLCurrency.GBP);
            case UNITED_STATES: return collect(WLCurrency.USN, WLCurrency.USD);
            case URUGUAY: return collect(WLCurrency.UYI, WLCurrency.UYU, WLCurrency.UYW);
            case UZBEKISTAN: return collect(WLCurrency.UZS);
            case VANUATU: return collect(WLCurrency.VUV);
            case VENEZUELA: return collect(WLCurrency.VED, WLCurrency.VES);
            case VIETNAM: return collect(WLCurrency.VND);
            case YEMEN: return collect(WLCurrency.YER);
            case ZAMBIA: return collect(WLCurrency.ZMW);
            case ZIMBABWE: return collect(WLCurrency.ZWL);


            case VATICAN_CITY: return collect(WLCurrency.EUR);

            case WALLIS_AND_FUTUNA: return collect(WLCurrency.XPF);
            case WESTERN_SAHARA: return collect(WLCurrency.MAD);
            default: return null;
        }
    }
}
