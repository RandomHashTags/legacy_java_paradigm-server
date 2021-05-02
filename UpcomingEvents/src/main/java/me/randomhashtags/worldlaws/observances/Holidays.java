package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.HolidayType;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Holidays implements Jsoupable, Jsonable {
    INSTANCE;

    private String nearHolidays;

    public void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final int valueCount = values.length;
        final String key = values[0];
        switch (key) {
            case "near":
                if(valueCount == 1) {
                    getNearHolidays(handler);
                } else {
                }
                break;
            default:
                final WLCountry country = WLCountry.valueOfBackendID(key);
                if(country != null) {
                    final int year = WLUtilities.getTodayYear();
                }
                break;
        }
    }

    private void loadNearbyHolidays(int year, CompletionHandler handler) {
        final LocalDate rightNow = LocalDate.now();
        final List<String> nearbyHolidayDays = new ArrayList<>();
        for(int i = 0; i <= 7; i++) {
            final LocalDate date = rightNow.plusDays(i);
            final int day = date.getDayOfMonth(), month = date.getMonthValue();
            final String target = month + "-" + day;
            nearbyHolidayDays.add(target);
        }
        final HashMap<String, String> descriptions = new HashMap<>();
        final HashMap<String, HashMap<String, HolidayObj>> nearbyHolidays = new HashMap<>();
        HolidayType.insertNearbyHolidays(year, nearbyHolidayDays, descriptions, nearbyHolidays, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final StringBuilder builder = new StringBuilder("{\"descriptions\":{");
                boolean isFirst = true;
                for(Map.Entry<String, String> map : descriptions.entrySet()) {
                    final String name = map.getKey(), description = map.getValue();
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
}