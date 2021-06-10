package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;
import me.randomhashtags.worldlaws.observances.type.ChristianHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum FathersDay implements IHoliday {
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
            case AFGHANISTAN:
            case ALGERIA:
            case ALBANIA:
            case ANTIGUA_AND_BARBUDA:
            case ARGENTINA:
            case BAHAMAS:
            case BAHRAIN:
            case BANGLADESH:
            case BARBADOS:
            case BELIZE:
            case BERMUDA:
            case BOSNIA_AND_HERZEGOVINA:
            case BOTSWANA:
            case BRUNEI:
            case BURKINA_FASO:
            case CAMBODIA:
            case CANADA:
            case CHAD:
            case CHILE:
            case CHINA:
            case COLOMBIA:
            case COMOROS:
            case COSTA_RICA:
            case CUBA:
            case CYPRUS:
            case CZECH_REPUBLIC:
            case DOMINICA:
            case ECUADOR:
            case ETHIOPIA:
            case FRANCE:
            case GEORGIA:
            case GHANA:
            case GREECE:
            case GUERNSEY:
            case GUYANA:
            case HONG_KONG:
            case HUNGARY:
            case INDIA:
            case IRELAND:
            case IVORY_COAST:
            case JAMAICA:
            case JAPAN:
            case JERSEY:
            case KENYA:
            case KUWAIT:
            case LAOS:
            case MACAU:
            case MALI:
            case MADAGASCAR:
            case MALAYSIA:
            case MALDIVES:
            case MALTA:
            case MAURITIUS:
            case MEXICO:
            case MOROCCO:
            case NAMIBIA:
            case NETHERLANDS:
            case NIGERIA:
            case OMAN:
            case PAKISTAN:
            case PANAMA:
            case PARAGUAY:
            case PERU:
            case PHILIPPINES:
            case PUERTO_RICO:
            case QATAR:
            case SAUDI_ARABIA:
            case SENEGAL:
            case SINGAPORE:
            case SLOVAKIA:
            case SOUTH_AFRICA:
            case SRI_LANKA:
            case SURINAME:
            case TANZANIA:
            case TRINIDAD_AND_TOBAGO:
            case TUNISIA:
            case TURKEY:
            case UGANDA:
            case UKRAINE:
            case UNITED_KINGDOM:
            case UNITED_STATES:
            case VENEZUELA:
            case VIETNAM:
            case ZAMBIA:
            case ZIMBABWE:
                return getThird(DayOfWeek.SUNDAY, Month.JUNE, year);
            case ANDORRA:
            case ANGOLA:
            case BOLIVIA:
            case CROATIA:
            case HONDURAS:
            case ITALY:
            case LIECHTENSTEIN:
            case MOZAMBIQUE:
            case PORTUGAL:
            case SPAIN:
                return new EventDate(Month.MARCH, 19, year);
            case AUSTRALIA:
            case FIJI:
            case NEW_ZEALAND:
            case PAPUA_NEW_GUINEA:
                return getFirst(DayOfWeek.SUNDAY, Month.SEPTEMBER, year);
            case AUSTRIA:
            case BELGIUM:
                return getSecond(DayOfWeek.SUNDAY, Month.JUNE, year);
            case BRAZIL:
            case SAMOA:
                return getSecond(DayOfWeek.SUNDAY, Month.AUGUST, year);
            case BULGARIA:
                return new EventDate(Month.DECEMBER, 26, year);
            case DENMARK:
                return new EventDate(Month.JUNE, 5, year);
            case EGYPT:
            case JORDAN:
            case LEBANON:
            case SUDAN:
            case SYRIA:
            case UNITED_ARAB_EMIRATES:
                return new EventDate(Month.JUNE, 21, year);
            case ESTONIA:
            case FINLAND:
            case ICELAND:
            case NORWAY:
            case SWEDEN:
                return getSecond(DayOfWeek.SUNDAY, Month.NOVEMBER, year);
            case GERMANY: return ChristianHoliday.FEAST_OF_THE_ASCENSION.getDate(country, year);
            case HAITI:
                return getLast(DayOfWeek.SUNDAY, Month.JUNE, year);
            case INDONESIA:
                return new EventDate(Month.NOVEMBER, 12, year);
            case LATVIA: return getSecond(DayOfWeek.SUNDAY, Month.SEPTEMBER, year);
            case LITHUANIA:
            case SWITZERLAND:
                return getFirst(DayOfWeek.SUNDAY, Month.JUNE, year);
            case LUXEMBOURG: return getFirst(DayOfWeek.SUNDAY, Month.OCTOBER, year);
            case NICARAGUA:
            case POLAND:
                return new EventDate(Month.JUNE, 23, year);
            case ROMANIA: return getSecond(DayOfWeek.SUNDAY, Month.MAY, year);
            case SEYCHELLES: return new EventDate(Month.JUNE, 16, year);
            case SOUTH_SUDAN: return getLast(DayOfWeek.MONDAY, Month.AUGUST, year);
            case TAIWAN: return new EventDate(Month.AUGUST, 8, year);
            case THAILAND: return new EventDate(Month.DECEMBER, 5, year);
            case TONGA: return getThird(DayOfWeek.SUNDAY, Month.MAY, year);
        }
        return null;
    }
}