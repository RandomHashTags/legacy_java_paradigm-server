package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum IndependenceDay implements IHoliday { // https://en.wikipedia.org/wiki/Category:Independence_days | https://en.wikipedia.org/wiki/List_of_national_independence_days
    INSTANCE;

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    public String getWikipediaName() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ABKHAZIA:
            case BOTSWANA:
                return new EventDate(Month.SEPTEMBER, 30, year);
            case AFGHANISTAN: return new EventDate(Month.AUGUST, 19, year);
            case ALBANIA: return new EventDate(Month.NOVEMBER, 28, year);
            case ALGERIA:
            case VENEZUELA:
                return new EventDate(Month.JULY, 5, year);
            case ANGOLA: return new EventDate(Month.NOVEMBER, 11, year);
            case ANTIGUA_AND_BARBUDA: return new EventDate(Month.NOVEMBER, 1, year);
            case ARGENTINA:
            case SOUTH_SUDAN:
                return new EventDate(Month.JULY, 9, year);
            case ARMENIA:
            case BELIZE:
                return new EventDate(Month.SEPTEMBER, 21, year);
            case AUSTRIA: return new EventDate(Month.OCTOBER, 26, year);
            case AZERBAIJAN: return new EventDate(Month.OCTOBER, 18, year);
            case BAHAMAS: return new EventDate(Month.JULY, 10, year);
            case BAHRAIN: return new EventDate(Month.DECEMBER, 16, year);
            case BANGLADESH: return new EventDate(Month.MARCH, 26, year);
            case BARBADOS: return new EventDate(Month.NOVEMBER, 30, year);
            case BELARUS: return new EventDate(Month.JULY, 3, year);
            case BELGIUM: return new EventDate(Month.JULY, 21, year);
            case BENIN: return new EventDate(Month.AUGUST, 1, year);
            case BOLIVIA: return new EventDate(Month.AUGUST, 6, year);
            case BOSNIA_AND_HERZEGOVINA: return new EventDate(Month.MARCH, 1, year);
            case BRAZIL: return new EventDate(Month.SEPTEMBER, 7, year);
            case BRUNEI: return new EventDate(Month.FEBRUARY, 23, year);
            case BULGARIA: return new EventDate(Month.OCTOBER, 5, year);
            case BURKINA_FASO: return new EventDate(Month.AUGUST, 5, year);
            case BURUNDI: return new EventDate(Month.JULY, 1, year);
            case CAMBODIA: return new EventDate(Month.NOVEMBER, 9, year);
            case CAPE_VERDE: return new EventDate(Month.JULY, 5, year);
            case CENTRAL_AFRICAN_REPUBLIC: return new EventDate(Month.AUGUST, 13, year);
            case CHAD: return new EventDate(Month.AUGUST, 11, year);
            case CHILE: return new EventDate(Month.SEPTEMBER, 18, year);
            case COLOMBIA: return new EventDate(Month.JULY, 20, year);
            case COMOROS: return new EventDate(Month.JULY, 6, year);
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return new EventDate(Month.JUNE, 30, year);
            case REPUBLIC_OF_THE_CONGO: return new EventDate(Month.AUGUST, 15, year);
            case COSTA_RICA: return new EventDate(Month.SEPTEMBER, 15, year);
            case CROATIA: return new EventDate(Month.MAY, 30, year);
            case CUBA: return new EventDate(Month.OCTOBER, 10, year);
            case CYPRUS: return new EventDate(Month.OCTOBER, 1, year);
            case CZECH_REPUBLIC: return new EventDate(Month.OCTOBER, 28, year);
            case DJIBOUTI: return new EventDate(Month.JUNE, 27, year);
            case DOMINICA: return new EventDate(Month.NOVEMBER, 3, year);
            case DOMINICAN_REPUBLIC: return new EventDate(Month.FEBRUARY, 27, year);
            case TIMOR_LESTE: return new EventDate(Month.MAY, 20, year);
            case ECUADOR: return new EventDate(Month.AUGUST, 10, year);
            case EL_SALVADOR: return new EventDate(Month.SEPTEMBER, 15, year);
            case ERITREA: return new EventDate(Month.MAY, 24, year);
            case ESTONIA: return new EventDate(Month.FEBRUARY, 24, year);
            case FIJI: return new EventDate(Month.OCTOBER, 10, year);
            case FINLAND: return new EventDate(Month.DECEMBER, 6, year);
            case GABON: return new EventDate(Month.AUGUST, 17, year);
            case GAMBIA: return new EventDate(Month.FEBRUARY, 18, year);
            case GEORGIA: return new EventDate(Month.MAY, 26, year);
            case GHANA: return new EventDate(Month.MARCH, 6, year);
            case GREECE: return new EventDate(Month.MARCH, 25, year);
            case GUATEMALA: return new EventDate(Month.SEPTEMBER, 15, year);
            case GUINEA: return new EventDate(Month.OCTOBER, 2, year);
            case GUYANA: return new EventDate(Month.MAY, 26, year);
            case HAITI: return new EventDate(Month.JANUARY, 1, year);
            case HONDURAS: return new EventDate(Month.SEPTEMBER, 15, year);
            case HUNGARY: return getLast(DayOfWeek.SATURDAY, Month.JUNE, year);
            case ICELAND: return new EventDate(Month.JUNE, 17, year);
            case INDIA: return new EventDate(Month.AUGUST, 15, year);
            case INDONESIA: return new EventDate(Month.AUGUST, 17, year);
            case IRAN: return new EventDate(Month.APRIL, 1, year);
            case IRAQ: return new EventDate(Month.OCTOBER, 3, year);
            case IRELAND: return new EventDate(Month.APRIL, 24, year);
            case IVORY_COAST: return new EventDate(Month.AUGUST, 7, year);
            case JAMAICA: return new EventDate(Month.AUGUST, 6, year);
            case JORDAN: return new EventDate(Month.MAY, 25, year);
            case KAZAKHSTAN: return new EventDate(Month.DECEMBER, 16, year);
            case KENYA: return new EventDate(Month.DECEMBER, 12, year);
            case KIRIBATI: return new EventDate(Month.JULY, 12, year);
            case KUWAIT: return new EventDate(Month.FEBRUARY, 25, year);
            case KYRGYZSTAN: return new EventDate(Month.AUGUST, 31, year);
            case LATVIA: return new EventDate(Month.MAY, 4, year);
            case LEBANON: return new EventDate(Month.NOVEMBER, 22, year);
            case LIBERIA: return new EventDate(Month.JULY, 26, year);
            case LIBYA: return new EventDate(Month.DECEMBER, 24, year);
            case LITHUANIA: return new EventDate(Month.MARCH, 11, year);
            case MADAGASCAR: return new EventDate(Month.JUNE, 26, year);
            case MALAWI: return new EventDate(Month.JULY, 6, year);
            case MALAYSIA: return new EventDate(Month.AUGUST, 31, year);
            case MALDIVES: return new EventDate(Month.JULY, 26, year);
            case MALI: return new EventDate(Month.SEPTEMBER, 22, year);
            case MALTA: return new EventDate(Month.SEPTEMBER, 21, year);
            case MAURITIUS: return new EventDate(Month.NOVEMBER, 28, year);
            case MEXICO: return new EventDate(Month.SEPTEMBER, 16, year);
            case MICRONESIA: return new EventDate(Month.NOVEMBER, 3, year);
            case MOLDOVA: return new EventDate(Month.AUGUST, 27, year);
            case MONGOLIA: return new EventDate(Month.DECEMBER, 29, year);
            case MONTENEGRO: return new EventDate(Month.MAY, 21, year);
            case MOROCCO: return new EventDate(Month.NOVEMBER, 18, year);
            case MOZAMBIQUE: return new EventDate(Month.JUNE, 25, year);
            case MYANMAR: return new EventDate(Month.JANUARY, 4, year);
            case NAMIBIA: return new EventDate(Month.MARCH, 21, year);
            case NAURU: return new EventDate(Month.JANUARY, 31, year);
            case NETHERLANDS: return new EventDate(Month.JUNE, 23, year);
            case NICARAGUA: return new EventDate(Month.SEPTEMBER, 15, year);
            case NIGER: return new EventDate(Month.AUGUST, 3, year);
            case NIGERIA: return new EventDate(Month.OCTOBER, 1, year);
            case NORTH_KOREA: return new EventDate(Month.AUGUST, 15, year);
            case NORTH_MACEDONIA: return new EventDate(Month.SEPTEMBER, 8, year);
            case NORWAY: return new EventDate(Month.JUNE, 7, year);
            case OMAN: return new EventDate(Month.NOVEMBER, 18, year);
            case PAKISTAN: return new EventDate(Month.AUGUST, 14, year);
            case PALESTINE: return new EventDate(Month.NOVEMBER, 15, year);
            case PANAMA: return new EventDate(Month.NOVEMBER, 3, year);
            case PAPUA_NEW_GUINEA: return new EventDate(Month.SEPTEMBER, 16, year);
            case PARAGUAY: return new EventDate(Month.MAY, 15, year);
            case PERU: return new EventDate(Month.JULY, 28, year);
            case PHILIPPINES: return new EventDate(Month.JUNE, 12, year);
            case POLAND: return new EventDate(Month.NOVEMBER, 11, year);
            case PORTUGAL: return new EventDate(Month.DECEMBER, 1, year);
            case QATAR: return new EventDate(Month.DECEMBER, 18, year);
            case ROMANIA: return new EventDate(Month.MAY, 9, year);
            case RWANDA:
            case SOMALIA:
                return new EventDate(Month.JULY, 1, year);
            case SAMOA: return new EventDate(Month.JUNE, 1, year);
            case SAO_TOME_AND_PRINCIPE: return new EventDate(Month.JULY, 12, year);
            case SAUDI_ARABIA: return new EventDate(Month.SEPTEMBER, 23, year);
            case SENEGAL: return new EventDate(Month.APRIL, 4, year);
            case SERBIA: return new EventDate(Month.FEBRUARY, 15, year);
            case SEYCHELLES: return new EventDate(Month.JUNE, 29, year);
            case SINGAPORE: return new EventDate(Month.AUGUST, 9, year);
            case SLOVAKIA: return new EventDate(Month.JULY, 17, year);
            case SLOVENIA: return new EventDate(Month.DECEMBER, 26, year);
            case SOLOMON_ISLANDS: return new EventDate(Month.JULY, 7, year);
            case SOUTH_AFRICA: return new EventDate(Month.DECEMBER, 11, year);
            case SOUTH_KOREA: return new EventDate(Month.MARCH, 1, year);
            case SRI_LANKA: return new EventDate(Month.FEBRUARY, 4, year);
            case SUDAN: return new EventDate(Month.JANUARY, 1, year);
            case SURINAME: return new EventDate(Month.NOVEMBER, 25, year);
            case SWEDEN: return new EventDate(Month.JUNE, 6, year);
            case SWITZERLAND: return new EventDate(Month.AUGUST, 1, year);
            case SYRIA: return new EventDate(Month.APRIL, 17, year);
            case TAJIKISTAN: return new EventDate(Month.SEPTEMBER, 9, year);
            case TANZANIA: return new EventDate(Month.DECEMBER, 9, year);
            case TOGO: return new EventDate(Month.APRIL, 27, year);
            case TRANSNISTRIA:
            case VIETNAM:
                return new EventDate(Month.SEPTEMBER, 2, year);
            case TRINIDAD_AND_TOBAGO: return new EventDate(Month.AUGUST, 31, year);
            case TUNISIA: return new EventDate(Month.MARCH, 20 , year);
            case TURKMENISTAN: return new EventDate(Month.SEPTEMBER, 27, year);
            case TUVALU: return new EventDate(Month.OCTOBER, 1, year);
            case UGANDA: return new EventDate(Month.OCTOBER, 9, year);
            case UKRAINE: return new EventDate(Month.AUGUST, 24, year);
            case UNITED_ARAB_EMIRATES: return new EventDate(Month.DECEMBER, 2, year);
            case UNITED_STATES: return new EventDate(Month.JULY, 4, year);
            case URUGUAY: return new EventDate(Month.AUGUST, 25, year);
            case UZBEKISTAN: return new EventDate(Month.SEPTEMBER, 1, year);
            case VANUATU: return new EventDate(Month.JULY, 30, year);
            case YEMEN: return new EventDate(Month.NOVEMBER, 30, year);
            case ZAMBIA: return new EventDate(Month.OCTOBER, 24, year);
            case ZIMBABWE: return new EventDate(Month.APRIL, 18, year);
            default: return null;
        }
    }
}
