package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.apache.logging.log4j.Level;

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

    public void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final int valueCount = values.length;
        final String key = values[0];
        switch (key) {
            case "all":
                final int year = WLUtilities.getTodayYear();
                if(valueCount == 1) {
                    getAllHolidays(year, handler);
                } else {
                    final String countryBackendID = values[1];
                    getCountryHolidays(year, countryBackendID, handler);
                }
                break;
            case "near":
                getNearHolidays(handler);
                break;
            default:
                break;
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
    private void getCountryHolidays(int year, String countryBackendID, CompletionHandler handler) {
        if(countryHolidays.containsKey(countryBackendID)) {
            handler.handleString(countryHolidays.get(countryBackendID));
        } else {
            final WLCountry country = WLCountry.valueOfBackendID(countryBackendID);
            if(country != null) {
                loadHolidays(year, countryBackendID, null, new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        countryHolidays.put(countryBackendID, string);
                        handler.handleString(string);
                    }
                });
            } else {
                WLLogger.log(Level.WARN, "Holidays - no WLCountry found using backend id \"" + countryBackendID + "\"!");
                handler.handleString(null);
            }
        }
    }
    private void getAllHolidays(int year, CompletionHandler handler) {
        if(allHolidays != null) {
            handler.handleString(allHolidays);
        } else {
            final ConcurrentHashMap<String, String> descriptions = new ConcurrentHashMap<>();
            final ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays = new ConcurrentHashMap<>();
            loadHolidays(year, null, null, descriptions, nearbyHolidays, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    allHolidays = string;
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
        loadHolidays(year, null, nearbyHolidayDays, descriptions, nearbyHolidays, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                nearHolidays = string;
                handler.handleString(string);
            }
        });
    }
    private void loadHolidays(int year, String country, List<String> days, ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> holidays, CompletionHandler handler) {
        HolidayType.insertNearbyHolidays(year, days, descriptions, holidays, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                final StringBuilder dateBuilder = new StringBuilder();
                for(Map.Entry<String, ConcurrentHashMap<String, HolidayObj>> map : holidays.entrySet()) {
                    final String holidayDay = map.getKey();
                    final StringBuilder holidayBuilder = new StringBuilder("\"").append(holidayDay).append("\":{");
                    boolean isFirst = true;

                    final ConcurrentHashMap<String, HolidayObj> holidays = map.getValue();
                    boolean hasHoliday = false;
                    for(HolidayObj holiday : holidays.values()) {
                        final HashSet<String> countries = holiday.getCountries();
                        if(countries == null || countries.contains(country)) {
                            holidayBuilder.append(isFirst ? "" : ",").append(holiday.toString());
                            isFirst = false;
                            hasHoliday = true;
                        }
                    }
                    if(hasHoliday) {
                        holidayBuilder.append("}");
                        dateBuilder.append(",").append(holidayBuilder);
                    }
                }

                final StringBuilder builder = new StringBuilder("{\"descriptions\":{");
                boolean isFirst = true;
                for(Map.Entry<String, String> map : descriptions.entrySet()) {
                    final String name = map.getKey(), description = LocalServer.fixEscapeValues(map.getValue());
                    builder.append(isFirst ? "" : ",").append("\"").append(name).append("\":\"").append(description).append("\"");
                    isFirst = false;
                }
                builder.append("}");
                builder.append(dateBuilder);
                builder.append("}");
                final String value = builder.toString();
                handler.handleString(value);
            }
        });
    }
}