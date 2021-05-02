package me.randomhashtags.worldlaws.observances.holidays.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum LabourDay implements IHoliday {
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
            case ALBANIA:
            case ALGERIA:
            case ANDORRA:
            case ANGOLA:
            case ANGUILLA:
            case ARGENTINA:
            case ARMENIA:
            case AUSTRIA:
            case BAHRAIN:
            case BANGLADESH:
            case BARBADOS:
            case BELARUS:
            case BELGIUM:
            case BELIZE:
            case BENIN:
            case BOLIVIA:
            case BOSNIA_AND_HERZEGOVINA:
            case BOTSWANA:
            case BRAZIL:
            case BURKINA_FASO:
            case BURUNDI:
            case CAMBODIA:
            case CAMEROON:
            case CAPE_VERDE:
            case CENTRAL_AFRICAN_REPUBLIC:
            case CHAD:
            case CHILE:
            case CHINA:
            case COLOMBIA:
            case COMOROS:
            case COSTA_RICA:
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO:
            case REPUBLIC_OF_THE_CONGO:
            case CROATIA:
            case CUBA:
            case CYPRUS:
            case CZECH_REPUBLIC:
            case DENMARK:
            case DJIBOUTI:
            case DOMINICAN_REPUBLIC:
            case EAST_TIMOR:
            case ECUADOR:
            case EGYPT:
            case EL_SALVADOR:
            case ERITREA:
            case ESTONIA:
            case ETHIOPIA:
            case FRANCE:
            case GABON:
            case GAMBIA:
            case GHANA:
            case GREECE:
            case GUATEMALA:
            case GUINEA:
            case GUYANA:
            case HAITI:
            case HONDURAS:
            case HONG_KONG:
            case HUNGARY:
            case ICELAND:
            case INDIA:
            case INDONESIA:
            case IRAQ:
            case IRAN:
            case ITALY:
            case IVORY_COAST:
            case JORDAN:
            case KENYA:
            case KOSOVO:
            case KYRGYZSTAN:
            case LATVIA:
            case LEBANON:
            case LIBYA:
            case LIECHTENSTEIN:
            case LITHUANIA:
            case LUXEMBOURG:
            case MACAU:
            case MADAGASCAR:
            case MALAWI:
            case MALAYSIA:
            case MALDIVES:
            case MALI:
            case MALTA:
            case MAURITIUS:
            case MEXICO:
            case MOLDOVA:
            case MONTENEGRO:
            case MOROCCO:
            case MOZAMBIQUE:
            case MYANMAR:
            case NAMIBIA:
            case NETHERLANDS:
            case NICARAGUA:
            case NIGER:
            case NIGERIA:
            case NORTH_KOREA:
            case NORTH_MACEDONIA:
            case NORWAY:
            case PAKISTAN:
            case PALESTINE:
            case PANAMA:
            case PARAGUAY:
            case PERU:
            case PHILIPPINES:
            case POLAND:
            case PORTUGAL:
            case ROMANIA:
            case RUSSIA:
            case RWANDA:
            case SAN_MARINO:
            case SENEGAL:
            case SERBIA:
            case SEYCHELLES:
            case SINGAPORE:
            case SLOVAKIA:
            case SLOVENIA:
            case SOMALIA:
            case SOUTH_AFRICA:
            case SOUTH_KOREA:
            case SOUTH_SUDAN:
            case SPAIN:
            case SRI_LANKA:
            case SURINAME:
            case SWITZERLAND:
            case SYRIA:
            case SAO_TOME_AND_PRINCIPE:
            case TAIWAN:
            case TAJIKISTAN:
            case TANZANIA:
            case THAILAND:
            case TOGO:
            case TUNISIA:
            case TURKEY:
            case UGANDA:
            case UKRAINE:
            case URUGUAY:
            case VANUATU:
            case VATICAN_CITY:
            case VENEZUELA:
            case VIETNAM:
            case YEMEN:
            case ZAMBIA:
            case ZIMBABWE:
                return new EventDate(Month.MAY, 1, year);
            case AUSTRALIA: return getFirst(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case BAHAMAS: return getFirst(DayOfWeek.FRIDAY, Month.JUNE, year);
            case CANADA: return getFirst(DayOfWeek.MONDAY, Month.SEPTEMBER, year);
            case JAMAICA: return new EventDate(Month.MAY, 23, year);
            case JAPAN:
                final EventDate date = new EventDate(Month.NOVEMBER, 23, year);
                return date.getLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY ? date.plusDays(1) : date;
            case KAZAKHSTAN: return getLast(DayOfWeek.SUNDAY, Month.SEPTEMBER, year);
            case NEW_ZEALAND: return getLast(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case TRINIDAD_AND_TOBAGO: return new EventDate(Month.JUNE, 19, year);
            case UNITED_KINGDOM: return getFirst(DayOfWeek.MONDAY, Month.MAY, year);
            default: return null;
        }
    }
}
