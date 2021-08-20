package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum Holidays implements Jsoupable, Jsonable {
    INSTANCE;

    private String nearHolidays;
    private final HashMap<String, String> nearCountryHolidays;

    Holidays() {
        nearCountryHolidays = new HashMap<>();
    }

    public void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final int valueCount = values.length;
        final String key = values[0];
        switch (key) {
            case "all":
                if(valueCount == 1) {
                    getAllHolidays(WLUtilities.getTodayYear(), handler);
                } else {
                    final String countryBackendID = values[1];
                    final WLCountry country = WLCountry.valueOfBackendID(countryBackendID);
                    if(country != null) {
                    }
                }
                break;
            case "near":
                if(valueCount == 1) {
                    getNearHolidays(handler);
                } else {
                    final String countryBackendID = values[1];
                    final WLCountry country = WLCountry.valueOfBackendID(countryBackendID);
                    if(country != null) {
                        getNearCountryHolidays(countryBackendID, handler);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void getAllHolidays(int year, CompletionHandler handler) {
        final HashMap<String, HashSet<String>> holidays = new HashMap<>();
        for(HolidayType type : HolidayType.values()) {
            final String typeCountry = type.getCelebratingCountry().getBackendID();
            type.getHolidaysJSONObject(year, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    for(String key : json.keySet()) {
                        if(!key.equals("descriptions")) {
                            final JSONObject dayJSON = json.getJSONObject(key);
                            // TODO: finish
                        }
                    }
                }
            });
        }
    }

    private void getNearHolidays(CompletionHandler handler) {
        if(nearHolidays != null) {
            handler.handleString(nearHolidays);
        } else {
            final long started = System.currentTimeMillis();
            final int year = WLUtilities.getTodayYear();
            loadNearbyHolidays(year, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    WLLogger.log(Level.INFO, "Holidays - loaded near holidays (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(string);
                }
            });
        }
    }
    private void loadNearbyHolidays(int year, CompletionHandler handler) {
        final LocalDate rightNow = LocalDate.now();
        final List<String> nearbyHolidayDays = new ArrayList<>();
        for(int i = 0; i <= 7; i++) {
            final LocalDate date = rightNow.plusDays(i);
            final String dateString = new EventDate(date).getDateString();
            nearbyHolidayDays.add(dateString);
        }
        final ConcurrentHashMap<String, String> descriptions = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays = new ConcurrentHashMap<>();
        HolidayType.insertNearbyHolidays(year, nearbyHolidayDays, descriptions, nearbyHolidays, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                final StringBuilder builder = new StringBuilder("{\"descriptions\":{");
                boolean isFirst = true;
                for(Map.Entry<String, String> map : descriptions.entrySet()) {
                    final String name = map.getKey(), description = LocalServer.fixEscapeValues(map.getValue());
                    builder.append(isFirst ? "" : ",").append("\"").append(name).append("\":\"").append(description).append("\"");
                    isFirst = false;
                }
                builder.append("}");
                for(Map.Entry<String, ConcurrentHashMap<String, HolidayObj>> map : nearbyHolidays.entrySet()) {
                    final String holidayDay = map.getKey();
                    final StringBuilder nearHolidayBuilder = new StringBuilder("\"").append(holidayDay).append("\":{");
                    isFirst = true;

                    final ConcurrentHashMap<String, HolidayObj> holidays = map.getValue();
                    for(HolidayObj holiday : holidays.values()) {
                        nearHolidayBuilder.append(isFirst ? "" : ",").append(holiday.toString());
                        isFirst = false;
                    }
                    nearHolidayBuilder.append("}");
                    builder.append(",").append(nearHolidayBuilder);
                }
                builder.append("}");
                final String value = builder.toString();
                nearHolidays = value;
                handler.handleString(value);
            }
        });
    }

    public void getNearCountryHolidays(String countryBackendID, CompletionHandler handler) {
        if(nearCountryHolidays.containsKey(countryBackendID)) {
            handler.handleString(nearCountryHolidays.get(countryBackendID));
        } else {
            // TODO: finish
            String string = null;
            nearCountryHolidays.put(countryBackendID, string);
        }
    }
}