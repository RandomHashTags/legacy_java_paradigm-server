package me.randomhashtags.worldlaws.country;

public enum WLCountryDemonyms {
    ;

    private static String[] collect(String...strings) {
        return strings;
    }

    public static String[] getSingular(WLCountry country) {
        final String countryName = country.getShortName();
        final String suffix;
        switch (country) {
            case ABKHAZIA:
                return collect("Abkhazian", "Abkhaz");
            case ARTSAKH:
            case AZERBAIJAN:
            case BELARUS:
            case BRAZIL:
            case CAMEROON:
            case CHAD:
            case ECUADOR:
            case EGYPT:
            case GHANA:
            case GIBRALTAR:
            case IRAN:
            case JORDAN:
                return collect(countryName + "ian");

            case GUAM:
            case PANAMA:
                suffix = (countryName.endsWith("a") ? "" : "a") + "nian";
                return collect(countryName + suffix);

            case ARGENTINA: return collect("Argentine", "Argentinian", "Argentinean");

            case BAHAMAS: return collect("Bahamian");
            case BARBADOS: return collect("Barbadian", "Bajan");
            case BELGIUM: return collect("Belgian");
            case BENIN: return collect("Beninois(e)", "Beninese");
            case BERMUDA: return collect("Bermudian");
            case BOTSWANA: return collect("Motswana", "Botswanan");
            case BURKINA_FASO: return collect("Burkinese", "Burkinabé");

            case CANADA: return collect("Canadian");
            case CAYMAN_ISLANDS: return collect("Caymanian");
            case CHINA: return collect("Chinese");
            case COMOROS: return collect("Comorian");
            case COOK_ISLANDS: return collect("Cook Islander");
            case CROATIA: return collect("Croatian", "Croat");
            case CYPRUS: return collect("Cypriot");

            case DENMARK: return collect("Danish", "Dane");
            case DOMINICAN_REPUBLIC: return collect("Dominican");

            case EL_SALVADOR: return collect("Salvadoran", "Salvadoreño");
            case ESWATINI: return collect("Swazi", "Swatis");

            case FAROE_ISLANDS: return collect("Faroese");
            case FIJI: return collect("Fijian", "Fijindian");
            case FINLAND: return collect("Finnish", "Finn", "Finnic");

            case GABON: return collect("Gabonese", "Gabonai", "Gabanais(e)");
            case GERMANY: return collect("German");
            case GREECE: return collect("Greek");
            case GREENLAND: return collect("Greenlander", "Greenlandic");
            case GRENADA: return collect("Grenadian");
            case GUERNSEY: return collect("Sarnian", "Sarnia");
            case GUINEA_BISSAU: return collect("Bissau-Guinean");
            case GUYANA: return collect("Guyanese");

            case HONDURAS: return collect("Honduran");
            case HUNGARY: return collect("Hungarian");

            case ICELAND: return collect("Icelander", "Icelandic");
            case IRELAND: return collect("Irish", "Irishmen", "Irishwomen");
            case ISRAEL: return collect("Israelite", "Israeli");
            case IVORY_COAST: return collect("Ivorian");
            case ITALY: return collect("Italian");

            case JAPAN: return collect("Japanese", "Nipponese");

            case KIRIBATI: return collect("Kiribatian", "I-Kiribati");
            case KOSOVO: return collect("Kosovan", "Kosovar");

            case LAOS: return collect("Laotian");
            case LEBANON: return collect("Lebanese");
            case LIECHTENSTEIN: return collect("Liechtensteiner");
            case LUXEMBOURG: return collect("Luxembourgish", "Luxembourger");

            case MACAU: return collect("Macanese");
            case MALDIVES: return collect("Maldivian");
            case MALTA: return collect("Maltese");
            case MARSHALL_ISLANDS: return collect("Marshallese");
            case MAURITIUS: return collect("Mauritian");
            case MEXICO: return collect("Mexican");
            case MONGOLIA: return collect("Mongolian", "Mongol");
            case MONACO: return collect("Monacan", "Monégasque");
            case MONTENEGRO: return collect("Montenegrin");
            case MOROCCO: return collect("Moroccan");
            case MOZAMBIQUE: return collect("Mozambican");
            case MYANMAR: return collect("Myanman", "Burmese");

            case NETHERLANDS: return collect("Netherlander", "Dutchman", "Hollander");
            case NEW_ZEALAND: return collect("New Zealander");
            case NIGER: return collect("Nigerien", "Nigerish");
            case NORTH_MACEDONIA: return collect("Macedonian");
            case NORWAY: return collect("Norwegian");

            case PALESTINE: return collect("Palestinian");
            case PARAGUAY: return collect("Paraguayan");
            case PERU: return collect("Peruvian");
            case PHILIPPINES: return collect("Philippine", "Filipino", "Filipina");
            case POLAND: return collect("Polish", "Pole");
            case PORTUGAL: return collect("Portuguese");
            case PUERTO_RICO: return collect("Puerto Rican");

            case RWANDA: return collect("Rwandan", "Rwandese");

            case SAINT_KITTS_AND_NEVIS: return collect("Kittitian", "Nevisian");
            case SAINT_VINCENT_AND_THE_GRENADINES: return collect("Vincentian");
            case SAN_MARINO: return collect("Sammarinese");
            case SAUDI_ARABIA: return collect("Saudi Arabian", "Saudi");
            case SCOTLAND: return collect("Scottish", "Scot", "Scotsmen", "Scotswomen");
            case SENEGAL: return collect("Senegalese");
            case SERBIA: return collect("Serbian", "Serb");
            case SEYCHELLES: return collect("Seychellois(e)");
            case SLOVAKIA: return collect("Slovakian", "Slovak");
            case SLOVENIA: return collect("Slovenian", "Slovene");
            case SOLOMON_ISLANDS: return collect("Solomon Islander");
            case SOMALIA: return collect("Somali");
            case SOUTH_SUDAN: return collect("South Sudanese");
            case SPAIN: return collect("Spaniard", "Spanish");
            case SURINAME: return collect("Surinamese");
            case SWEDEN: return collect("Swedish", "Swede");

            case TAIWAN: return collect("Taiwanese");
            case TAJIKISTAN: return collect("Tajikistani", "Tajik");
            case THAILAND: return collect("Thai");
            case TIMOR_LESTE: return collect("East Timorese");
            case TOGO: return collect("Togolese");
            case TRINIDAD_AND_TOBAGO: return collect("Trinidadian", "Tobagonian", "Trinbagonian", "Trini");
            case TURKEY: return collect("Turkish", "Turk");
            case TURKMENISTAN: return collect("Turkmenistani", "Turkmen");

            case UKRAINE: return collect("Ukrainian");
            case UNITED_ARAB_EMIRATES: return collect("Emirati");
            case UNITED_KINGDOM: return collect("British", "Briton");
            case UNITED_STATES: return collect("American");
            case URUGUAY: return collect("Uruguayan", "Orientale");
            case UZBEKISTAN: return collect("Uzbekistani", "Uzbek");

            case VANUATU: return collect("Vanuatuan", "Ni-Vanuatu");
            case VIETNAM: return collect("Vietnamese");

            default:
                suffix =
                        countryName.endsWith("in") || countryName.endsWith("an") ? "ese"
                        : countryName.endsWith("n") || countryName.endsWith("h") || countryName.endsWith("l") || countryName.endsWith("q") || countryName.endsWith("r") || countryName.endsWith("t") ? "i"
                        : countryName.endsWith("a") ? "n"
                        : countryName.endsWith("i") || countryName.endsWith("e") || countryName.endsWith("u") ? "an"
                        : "";
                final String demonym = countryName + suffix;
                return collect(demonym);
        }
    }
}
