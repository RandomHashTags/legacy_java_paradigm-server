package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.observances.type.*;

import java.time.LocalDate;
import java.util.*;

public enum HolidayType {
    AMERICAN(
            "Americans around the world",
            WLCountry.UNITED_STATES.getFlagEmoji()
    ),
    AUSTRALIAN(
            "Australian citizens",
            WLCountry.AUSTRALIA.getFlagEmoji()
    ),
    /*CHINESE(
            "Chinese around the world",
            "\uD83C\uDDE8\uD83C\uDDF3"
    ),*/
    CHRISTIAN_EAST(
            "Eastern Orthodox Christians around the world",
            "✝️"
    ),
    CHRISTIAN_WEST(
            "Western Orthodox Christians around the world",
            "✝️"
    ),

    FUN(
            "Anyone who recognizes the importance of this holiday",
            "\uD83E\uDD2A"
    ),

    GREGORIAN(
            "People using the Gregorian calendar",
            "\uD83D\uDCC5"
    ),
    MEXICAN(
            "Mexicans around the world",
            WLCountry.MEXICO.getFlagEmoji()
    ),
    UNITED_NATIONS(
            "Sovereign states recognized by the United Nations",
            "\uD83C\uDDFA\uD83C\uDDF3"
    ),
    UNOFFICIAL(
            "Various groups, individuals, and organizations",
            "\uD83D\uDCC6"
    ),

    COUNTRIES(null, null),
    ;

    private final String celebrators, emoji;

    HolidayType(String celebrators, String emoji) {
        this.celebrators = celebrators;
        this.emoji = emoji;
    }

    private Holiday[] getHolidays() {
        switch (this) {
            case AMERICAN: return AmericanHoliday.values();
            case AUSTRALIAN: return AustralianHoliday.values();
            //case CHINESE: return ChineseHoliday.values();
            case CHRISTIAN_EAST: return ChristianEastHoliday.values();
            case CHRISTIAN_WEST: return ChristianWestHoliday.values();
            case FUN: return FunHoliday.values();
            case GREGORIAN: return GregorianHoliday.values();
            case MEXICAN: return MexicanHoliday.values();
            case UNITED_NATIONS: return UnitedNationHoliday.values();
            case UNOFFICIAL: return UnofficialHoliday.values();

            case COUNTRIES: return SocialHoliday.values();
            default: return null;
        }
    }
    public Holiday getHoliday(String identifier) {
        final Holiday[] holidays = getHolidays();
        if(holidays != null) {
            for(Holiday holiday : holidays) {
                final JSONObjectTranslatable json = holiday.getJSONObjectTranslatable();
                if(json != null && json.optString("identifier", "").equals(identifier)) {
                    return holiday;
                }
            }
        }
        return null;
    }
    public WLCountry getCelebratingCountry() {
        switch (this) {
            case AMERICAN: return WLCountry.UNITED_STATES;
            case AUSTRALIAN: return WLCountry.AUSTRALIA;
            case MEXICAN: return WLCountry.MEXICO;
            default: return null;
        }
    }

    public HashMap<String, HashMap<String, PreHoliday>> getNearHolidays(LocalDate startingDate, LocalDate endingDate) {
        final HashMap<String, HashMap<String, PreHoliday>> list = new HashMap<>();
        final Holiday[] holidays = getHolidays();
        if(holidays != null) {
            final int startingYear = startingDate.getYear(), endingYear = endingDate.getYear();
            final List<Integer> years = new ArrayList<>() {{
                add(startingYear);
            }};
            if(startingYear != endingYear) {
                years.add(endingYear);
            }

            final Collection<WLCountry> countries = this == COUNTRIES ? Arrays.asList(WLCountry.values()) : null;
            final WLCountry unCountry = this == UNITED_NATIONS ? WLCountry.UNITED_STATES : getCelebratingCountry();
            for(Holiday holiday : holidays) {
                for(int year : years) {
                    if(countries != null) {
                        for(WLCountry country : countries) {
                            insertNearHolidays(country, holiday, startingDate, endingDate, year, list);
                        }
                    } else {
                        insertNearHolidays(unCountry, holiday, startingDate, endingDate, year, list);
                    }
                }
            }
        }
        return list;
    }
    public HashMap<String, HashMap<String, PreHoliday>> getHolidays(WLCountry country, int year) {
        final HashMap<String, HashMap<String, PreHoliday>> list = new HashMap<>();
        final Holiday[] holidays = getHolidays();
        if(holidays != null) {
            for(Holiday holiday : holidays) {
                insertNearHolidays(country, holiday, null, null, year, list);
            }
        }
        return list;
    }
    private void insertNearHolidays(WLCountry country, Holiday holiday, LocalDate startingDate, LocalDate endingDate, int year, HashMap<String, HashMap<String, PreHoliday>> list) {
        final EventDate eventDate = holiday.getDate(country, year);
        if(eventDate != null) {
            final LocalDate date = eventDate.getLocalDate();
            if(startingDate == null || endingDate == null
                    || ((date.equals(startingDate) || date.isAfter(startingDate)) && (date.equals(endingDate) || date.isBefore(endingDate)))
            ) {
                final String dateString = eventDate.getDateString();
                list.putIfAbsent(dateString, new HashMap<>());
                final HashMap<String, PreHoliday> preHolidays = list.get(dateString);
                final String name = holiday.getName();
                final HolidayType type = holiday.getType();
                preHolidays.putIfAbsent(name, new PreHoliday(holiday.getIdentifier(), name, type, emoji, celebrators));
                if(type != UNITED_NATIONS) {
                    preHolidays.get(name).addCountry(country);
                }
            }
        }
    }
}
