package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum Holidays implements Jsoupable, Jsonable {
    INSTANCE;

    private String allHolidays, nearHolidays;
    private final HashMap<String, String> countryHolidays;

    Holidays() {
        countryHolidays = new HashMap<>();
    }

    public String getResponse(String value) {
        final String[] values = value.split("/");
        final int valueCount = values.length;
        final String key = values[0];
        switch (key) {
            case "all":
                final int year = WLUtilities.getTodayYear();
                if(valueCount == 1) {
                    return getAllHolidays(year);
                } else {
                    final String countryBackendID = values[1];
                    return getCountryHolidays(year, countryBackendID);
                }
            case "near":
                return getNearHolidays();
            default:
                return null;
        }
    }

    public String refreshNearHolidays() {
        final long started = System.currentTimeMillis();
        final int year = WLUtilities.getTodayYear();
        final String string = loadNearbyHolidays(year);
        WLLogger.logInfo("Holidays - refreshed near holidays (took " + (System.currentTimeMillis()-started) + "ms)");
        return string;
    }
    private String getNearHolidays() {
        if(nearHolidays == null) {
            refreshNearHolidays();
        }
        return nearHolidays;
    }
    private String getCountryHolidays(int year, String countryBackendID) {
        if(!countryHolidays.containsKey(countryBackendID)) {
            final WLCountry country = WLCountry.valueOfBackendID(countryBackendID);
            if(country != null) {
                final String string = loadHolidays(year, countryBackendID, null, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
                countryHolidays.put(countryBackendID, string);
            } else {
                WLLogger.logError(this, "getCountryHolidays - no WLCountry found using backend id \"" + countryBackendID + "\"!");
            }
        }
        return countryHolidays.get(countryBackendID);
    }
    private String getAllHolidays(int year) {
        if(allHolidays == null) {
            final ConcurrentHashMap<String, String> descriptions = new ConcurrentHashMap<>();
            final ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays = new ConcurrentHashMap<>();
            allHolidays = loadHolidays(year, null, null, descriptions, nearbyHolidays);
        }
        return allHolidays;
    }
    private String loadNearbyHolidays(int year) {
        final LocalDate rightNow = LocalDate.now();
        final List<String> nearbyHolidayDays = new ArrayList<>();
        for(int i = -1; i <= 7; i++) {
            final LocalDate date = rightNow.plusDays(i);
            final String dateString = new EventDate(date).getDateString();
            nearbyHolidayDays.add(dateString);
        }
        final ConcurrentHashMap<String, String> descriptions = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays = new ConcurrentHashMap<>();
        nearHolidays = loadHolidays(year, null, nearbyHolidayDays, descriptions, nearbyHolidays);
        return nearHolidays;
    }
    private String loadHolidays(int year, String country, List<String> days, ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> holidays) {
        if(country != null) {
            final Folder folder = Folder.UPCOMING_EVENTS_HOLIDAYS_COUNTRIES;
            final String fileName = folder.getFolderName().replace("%year%", Integer.toString(year));
            folder.setCustomFolderName(country, fileName);
            final JSONObject json = getJSONObject(folder, country, new CompletionHandler() {
                @Override
                public String loadJSONObjectString() {
                    HolidayType.insertNearbyHolidays(year, days, descriptions, holidays);
                    return getLoadedHolidays(country, descriptions, holidays);
                }
            });
            return json.toString();
        } else {
            HolidayType.insertNearbyHolidays(year, days, descriptions, holidays);
            return getLoadedHolidays(null, descriptions, holidays);
        }
    }
    private String getLoadedHolidays(String country, ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> holidays) {
        final StringBuilder dateBuilder = new StringBuilder();
        final StringBuilder descriptionBuilder = new StringBuilder("{\"descriptions\":{");
        final HashSet<String> loadedDescriptions = new HashSet<>();
        boolean isFirstDescription = true;
        for(Map.Entry<String, ConcurrentHashMap<String, HolidayObj>> map : holidays.entrySet()) {
            final String holidayDay = map.getKey();
            final StringBuilder holidayBuilder = new StringBuilder("\"").append(holidayDay).append("\":{");
            boolean isFirst = true;

            final ConcurrentHashMap<String, HolidayObj> holidaysMap = map.getValue();
            boolean hasHoliday = false;
            for(HolidayObj holiday : holidaysMap.values()) {
                final HashSet<String> countries = holiday.getCountries();
                if(countries == null || country == null || countries.contains(country)) {
                    holidayBuilder.append(isFirst ? "" : ",").append(holiday);
                    isFirst = false;
                    hasHoliday = true;
                    final String englishName = holiday.getEnglishName();
                    if(!loadedDescriptions.contains(englishName)) {
                        loadedDescriptions.add(englishName);
                        final String description = LocalServer.fixEscapeValues(descriptions.get(englishName));
                        descriptionBuilder.append(isFirstDescription ? "" : ",").append("\"").append(englishName).append("\":\"").append(description).append("\"");
                        isFirstDescription = false;
                    }
                }
            }
            if(hasHoliday) {
                holidayBuilder.append("}");
                dateBuilder.append(",").append(holidayBuilder);
            }
        }
        descriptionBuilder.append("}");
        dateBuilder.append("}");
        return descriptionBuilder.append(dateBuilder).toString();
    }
}