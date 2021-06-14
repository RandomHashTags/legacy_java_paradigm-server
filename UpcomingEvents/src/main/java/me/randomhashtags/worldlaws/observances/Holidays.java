package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void loadNearbyHolidays(int year, CompletionHandler handler) {
        final LocalDate rightNow = LocalDate.now();
        final List<String> nearbyHolidayDays = new ArrayList<>();
        for(int i = 0; i <= 7; i++) {
            final LocalDate date = rightNow.plusDays(i);
            final String dateString = new EventDate(date).getDateString();
            nearbyHolidayDays.add(dateString);
        }
        final HashMap<String, String> descriptions = new HashMap<>();
        final HashMap<String, HashMap<String, HolidayObj>> nearbyHolidays = new HashMap<>();
        HolidayType.insertNearbyHolidays(year, nearbyHolidayDays, descriptions, nearbyHolidays, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final StringBuilder builder = new StringBuilder("{\"descriptions\":{");
                boolean isFirst = true;
                for(Map.Entry<String, String> map : descriptions.entrySet()) {
                    final String name = map.getKey(), description = LocalServer.fixEscapeValues(map.getValue());
                    builder.append(isFirst ? "" : ",").append("\"").append(name).append("\":\"").append(description).append("\"");
                    isFirst = false;
                }
                builder.append("}");
                for(Map.Entry<String, HashMap<String, HolidayObj>> map : nearbyHolidays.entrySet()) {
                    final String holidayDay = map.getKey();
                    final StringBuilder nearHolidayBuilder = new StringBuilder("\"").append(holidayDay).append("\":{");
                    isFirst = true;

                    final HashMap<String, HolidayObj> holidays = map.getValue();
                    for(HolidayObj holiday : holidays.values()) {
                        nearHolidayBuilder.append(isFirst ? "" : ",").append(holiday.toString());
                        isFirst = false;
                    }
                    nearHolidayBuilder.append("}");
                    builder.append(",").append(nearHolidayBuilder);
                }
                builder.append("}");
                final String string = builder.toString();
                nearHolidays = string;
                handler.handle(string);
            }
        });
    }

    public void getNearHolidays(CompletionHandler handler) {
        if(nearHolidays != null) {
            handler.handle(nearHolidays);
        } else {
            final long started = System.currentTimeMillis();
            final int year = WLUtilities.getTodayYear();
            loadNearbyHolidays(year, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    WLLogger.log(Level.INFO, "Holidays - loaded near holidays (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(object);
                }
            });
        }
    }
    public void getNearCountryHolidays(String countryBackendID, CompletionHandler handler) {
        if(nearCountryHolidays.containsKey(countryBackendID)) {
            handler.handle(nearCountryHolidays.get(countryBackendID));
        } else {
            // TODO
            String string = null;
            nearCountryHolidays.put(countryBackendID, string);
        }
    }
}