package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public enum Holidays {
    INSTANCE;

    private JSONObjectTranslatable near;
    private final HashMap<String, JSONObjectTranslatable> countryHolidays;

    Holidays() {
        countryHolidays = new HashMap<>();
    }

    public JSONObjectTranslatable getResponse(String[] values) {
        final int valueCount = values.length;
        final String key = values[0];
        switch (key) {
            case "all":
                final int year = WLUtilities.getTodayYear();
                if(valueCount == 1) {
                    return null;
                    //return getAllHolidays(year);
                } else {
                    final String countryBackendID = values[1];
                    return getCountryHolidays(year, countryBackendID);
                }
            case "near":
                return getNear();
            case "holiday":
                return values.length == 3 ? getHoliday(values[1], values[2]) : null;
            default:
                return null;
        }
    }

    private JSONObjectTranslatable getNear() {
        if(near == null) {
            refreshNearHolidays();
        }
        return near;
    }
    public void refreshNearHolidays() {
        final long started = System.currentTimeMillis();
        final LocalDate startingDate = LocalDate.now().minusDays(1), endingDate = startingDate.plusDays(8);
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(HolidayType type : HolidayType.values()) {
            final HashMap<String, HashMap<String, PreHoliday>> near = type.getNearHolidays(startingDate, endingDate);
            insertHolidays(json, near);
        }
        near = json;
        WLLogger.logInfo("NewHolidays - refreshed near holidays (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private void insertHolidays(JSONObjectTranslatable json, HashMap<String, HashMap<String, PreHoliday>> holidays) {
        for(Map.Entry<String, HashMap<String, PreHoliday>> entry : holidays.entrySet()) {
            final String dateString = entry.getKey();
            final JSONObjectTranslatable holidaysJSON = new JSONObjectTranslatable();
            for(Map.Entry<String, PreHoliday> preHolidayMap : entry.getValue().entrySet()) {
                final PreHoliday preHoliday = preHolidayMap.getValue();
                final String identifier = preHoliday.getIdentifier();
                preHoliday.insertCountries();
                holidaysJSON.put(identifier, preHoliday);
                holidaysJSON.addTranslatedKey(identifier);
            }
            if(json.has(dateString)) {
                json.append(holidaysJSON);
            } else {
                json.put(dateString, holidaysJSON);
                json.addTranslatedKey(dateString);
            }
        }
    }

    public JSONObjectTranslatable getHoliday(String type, String identifier) {
        final HolidayType holidayType = HolidayType.valueOf(type);
        final Holiday holiday = holidayType.getHoliday(identifier);
        return holiday != null ? holiday.getJSONObjectTranslatable() : null;
    }

    private JSONObjectTranslatable getCountryHolidays(int year, String countryBackendID) {
        if(!countryHolidays.containsKey(countryBackendID)) {
            final WLCountry country = WLCountry.valueOfString(countryBackendID);
            if(country != null) {
                final JSONObjectTranslatable json = new JSONObjectTranslatable();
                for(HolidayType type : HolidayType.values()) {
                    final HashMap<String, HashMap<String, PreHoliday>> near = type.getHolidays(country, year);
                    insertHolidays(json, near);
                }
                countryHolidays.put(countryBackendID, json);
            } else {
                WLLogger.logError(this, "getCountryHolidays - no WLCountry found using backend id \"" + countryBackendID + "\"!");
            }
        }
        return countryHolidays.get(countryBackendID);
    }
}
