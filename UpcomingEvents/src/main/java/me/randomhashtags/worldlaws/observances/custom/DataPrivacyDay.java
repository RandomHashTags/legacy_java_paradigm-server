package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum DataPrivacyDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Data Privacy Day";
    }

    @Override
    public String[] getAliases() {
        return collectAliases(
                "Data Protection Day",
                "National Data Privacy Day"
        );
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ALBANIA:
            case ANDORRA:
            case ARMENIA:
            case AUSTRIA:
            case AZERBAIJAN:

            case BELGIUM:
            case BOSNIA_AND_HERZEGOVINA:
            case BULGARIA:

            case CANADA:
            case CROATIA:
            case CYPRUS:
            case CZECH_REPUBLIC:

            case DENMARK:

            case ESTONIA:

            case FAROE_ISLANDS:
            case FINLAND:
            case FRANCE:

            case GEORGIA:
            case GERMANY:
            case GREECE:
            case GREENLAND:

            case HUNGARY:

            case ICELAND:
            case IRELAND:
            case ISRAEL:
            case ITALY:

            case LATVIA:
            case LIECHTENSTEIN:
            case LITHUANIA:
            case LUXEMBOURG:

            case MALTA:
            case MOLDOVA:
            case MONTENEGRO:

            case NETHERLANDS:
            case NIGERIA:
            case NORTH_MACEDONIA:
            case NORWAY:

            case POLAND:
            case PORTUGAL:

            case ROMANIA:
            case RUSSIA:

            case SAN_MARINO:
            case SCOTLAND:
            case SERBIA:
            case SLOVAKIA:
            case SLOVENIA:
            case SPAIN:
            case SWEDEN:
            case SWITZERLAND:

            case TURKEY:

            case UKRAINE:
            case UNITED_KINGDOM:
            case UNITED_STATES:
                return new EventDate(Month.JANUARY, 28, year);
            default:
                return null;
        }
    }
}
