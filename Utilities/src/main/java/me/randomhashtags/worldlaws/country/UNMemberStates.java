package me.randomhashtags.worldlaws.country;

public enum UNMemberStates { // https://en.wikipedia.org/wiki/Member_states_of_the_United_Nations
    ;

    public static boolean isUNMember(WLCountry country) {
        switch (country) {
            case AFGHANISTAN:
            case ALBANIA:
            case ALGERIA:
            case ANDORRA:
            case ANGOLA:
            case ANTIGUA_AND_BARBUDA:
            case ARGENTINA:
            case ARMENIA:
            case AUSTRALIA:
            case AUSTRIA:
            case AZERBAIJAN:

            case BAHAMAS:
            case BAHRAIN:
            case BANGLADESH:
            case BARBADOS:
            case BELARUS:
            case BELGIUM:
            case BELIZE:
            case BENIN:
            case BHUTAN:
            case BOLIVIA:
            case BOSNIA_AND_HERZEGOVINA:
            case BOTSWANA:
            case BRAZIL:
            case BRUNEI:
            case BULGARIA:
            case BURKINA_FASO:
            case BURUNDI:

            case CAMBODIA:
            case CAMEROON:
            case CANADA:
            case CAPE_VERDE:
            case CENTRAL_AFRICAN_REPUBLIC:
            case CHAD:
            case CHILE:
            case CHINA:
            case COLOMBIA:
            case COMOROS:
            case COSTA_RICA:
            case CROATIA:
            case CUBA:
            case CYPRUS:
            case CZECH_REPUBLIC:

            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO:
            case DENMARK:
            case DJIBOUTI:
            case DOMINICA:
            case DOMINICAN_REPUBLIC:

            case ECUADOR:
            case EGYPT:
            case EL_SALVADOR:
            case ERITREA:
            case ESTONIA:
            case ESWATINI:
            case ETHIOPIA:

            case FIJI:
            case FINLAND:
            case FRANCE:

            case GABON:
            case GAMBIA:
            case GEORGIA:
            case GERMANY:
            case GHANA:
            case GREECE:
            case GRENADA:
            case GUATEMALA:
            case GUINEA:
            case GUINEA_BISSAU:
            case GUYANA:

            case HAITI:
            case HONDURAS:
            case HUNGARY:

            case ICELAND:
            case INDIA:
            case INDONESIA:
            case IRAN:
            case IRAQ:
            case IRELAND:
            case ISRAEL:
            case ITALY:
            case IVORY_COAST:

            case JAMAICA:
            case JAPAN:
            case JORDAN:

            case KAZAKHSTAN:
            case KENYA:
            case KIRIBATI:
            case KUWAIT:
            case KYRGYZSTAN:

            case LAOS:
            case LATVIA:
            case LEBANON:
            case LESOTHO:
            case LIBERIA:
            case LIBYA:
            case LIECHTENSTEIN:
            case LITHUANIA:
            case LUXEMBOURG:

            case MADAGASCAR:
            case MALAWI:
            case MALAYSIA:
            case MALDIVES:
            case MALI:
            case MALTA:
            case MARSHALL_ISLANDS:
            case MAURITANIA:
            case MAURITIUS:
            case MEXICO:
            case MICRONESIA:
            case MOLDOVA:
            case MONACO:
            case MONGOLIA:
            case MONTENEGRO:
            case MOROCCO:
            case MOZAMBIQUE:
            case MYANMAR:

            case NAMIBIA:
            case NAURU:
            case NEPAL:
            case NETHERLANDS:
            case NEW_ZEALAND:
            case NICARAGUA:
            case NIGER:
            case NIGERIA:
            case NORTH_KOREA:
            case NORTH_MACEDONIA:
            case NORWAY:

            case OMAN:

            case PAKISTAN:
            case PALAU:
            case PANAMA:
            case PAPUA_NEW_GUINEA:
            case PARAGUAY:
            case PERU:
            case PHILIPPINES:
            case POLAND:
            case PORTUGAL:

            case QATAR:

            case REPUBLIC_OF_THE_CONGO:
            case ROMANIA:
            case RUSSIA:
            case RWANDA:

            case SAINT_KITTS_AND_NEVIS:
            case SAINT_LUCIA:
            case SAINT_VINCENT_AND_THE_GRENADINES:
            case SAMOA:
            case SAN_MARINO:
            case SAO_TOME_AND_PRINCIPE:
            case SAUDI_ARABIA:
            case SENEGAL:
            case SERBIA:
            case SEYCHELLES:
            case SIERRA_LEONE:
            case SINGAPORE:
            case SLOVAKIA:
            case SLOVENIA:
            case SOLOMON_ISLANDS:
            case SOMALIA:
            case SOUTH_AFRICA:
            case SOUTH_KOREA:
            case SOUTH_SUDAN:
            case SPAIN:
            case SRI_LANKA:
            case SUDAN:
            case SURINAME:
            case SWEDEN:
            case SWITZERLAND:
            case SYRIA:

            case TAJIKISTAN:
            case TANZANIA:
            case THAILAND:
            case TIMOR_LESTE:
            case TOGO:
            case TONGA:
            case TRINIDAD_AND_TOBAGO:
            case TUNISIA:
            case TURKEY:
            case TURKMENISTAN:
            case TUVALU:

            case UGANDA:
            case UKRAINE:
            case UNITED_ARAB_EMIRATES:
            case UNITED_KINGDOM:
            case UNITED_STATES:
            case URUGUAY:
            case UZBEKISTAN:

            case VANUATU:
            case VENEZUELA:
            case VIETNAM:

            case YEMEN:

            case ZAMBIA:
            case ZIMBABWE:
                return true;
            default:
                return false;
        }
    }
    public static boolean isUNObserverState(WLCountry country) {
        switch (country) {
            case PALESTINE:
            case VATICAN_CITY:
                return true;
            default:
                return false;
        }
    }
}
