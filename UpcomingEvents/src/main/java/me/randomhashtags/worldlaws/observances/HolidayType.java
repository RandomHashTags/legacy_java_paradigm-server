package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.type.*;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum HolidayType implements Jsonable {
    AMERICAN(
            "Americans around the world",
            "\uD83C\uDDFA\uD83C\uDDF8"
    ),
    AUSTRALIAN(
            "Australian citizens",
            "\uD83C\uDDE6\uD83C\uDDFA"
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
            "Anyone",
            ""
    ),

    GREGORIAN(
            "People using the Gregorian calendar",
            "\uD83D\uDCC5"
    ),
    MEXICAN(
            "Mexicans around the world",
            "\uD83C\uDDF2\uD83C\uDDFD"
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
    private JSONObject cache;

    HolidayType(String celebrators, String emoji) {
        this.celebrators = celebrators;
        this.emoji = emoji;
    }

    public static void insertNearbyHolidays(int year, Collection<String> nearbyHolidayDays, HashMap<String, String> descriptions, HashMap<String, HashMap<String, HolidayObj>> nearbyHolidays, CompletionHandler handler) {
        final AtomicInteger completed = new AtomicInteger(0);
        final HolidayType[] types = HolidayType.values();
        final int max = types.length;
        Arrays.stream(types).parallel().forEach(type -> {
            final boolean isCountries = type == COUNTRIES;
            type.getHolidaysJSONObject(year, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    final JSONObject descriptionsJSON = json.getJSONObject("descriptions");
                    final Set<String> keys = isCountries ? json.keySet() : null;
                    for(String holidayDay : nearbyHolidayDays) {
                        if(isCountries) {
                            for(String country : keys) {
                                final JSONObject countryJSON = json.getJSONObject(country);
                                insertNearbyHolidays(descriptionsJSON, country, holidayDay, countryJSON, descriptions, nearbyHolidays);
                            }
                        } else {
                            insertNearbyHolidays(descriptionsJSON, null, holidayDay, json, descriptions, nearbyHolidays);
                        }
                    }
                    if(completed.addAndGet(1) == max) {
                        handler.handleObject(null);
                    }
                }
            });
        });
    }
    private static void insertNearbyHolidays(JSONObject descriptionsJSON, String country, String holidayDay, JSONObject json, HashMap<String, String> descriptions, HashMap<String, HashMap<String, HolidayObj>> nearbyHolidays) {
        if(json.has(holidayDay)) {
            final JSONObject array = json.getJSONObject(holidayDay);
            final boolean isCountries = country != null;
            for(String key : array.keySet()) {
                final JSONObject holidayJSON = array.getJSONObject(key);
                final HolidayObj holiday = isCountries ? new HolidayObj(key, holidayJSON) : new CustomHoliday(key, holidayJSON);
                if(isCountries) {
                    holiday.addCountry(country);
                }
                final String holidayEnglishName = holiday.getEnglishName(), description = descriptionsJSON.getString(holidayEnglishName);
                descriptions.put(holidayEnglishName, description);
                nearbyHolidays.putIfAbsent(holidayDay, new HashMap<>());
                final HashMap<String, HolidayObj> map = nearbyHolidays.get(holidayDay);
                if(isCountries && map.containsKey(holidayEnglishName)) {
                    map.get(holidayEnglishName).addCountry(country);
                } else {
                    map.put(holidayEnglishName, holiday);
                }
            }
        }
    }

    private IHoliday[] getHolidays() {
        switch (this) {
            case AMERICAN: return AmericanHoliday.values();
            case AUSTRALIAN: return AustralianHoliday.values();
            //case CHINESE: return ChineseHoliday.values();
            case CHRISTIAN_EAST:
            case CHRISTIAN_WEST:
                return ChristianHoliday.values();
            case FUN: return FunHoliday.values();
            case GREGORIAN: return GregorianHoliday.values();
            case MEXICAN: return MexicanHoliday.values();
            case UNITED_NATIONS: return UnitedNationHoliday.values();
            case UNOFFICIAL: return UnofficialHoliday.values();

            case COUNTRIES: return SocialHoliday.values();
            default: return null;
        }
    }
    public WLCountry getCelebratingCountry() {
        switch (this) {
            case AMERICAN: return WLCountry.UNITED_STATES;
            case AUSTRALIAN: return WLCountry.AUSTRALIA;
            case MEXICAN: return WLCountry.MEXICO;
            default: return null;
        }
    }

    public void getHolidaysJSONObject(int year, CompletionHandler handler) {
        if(cache != null) {
            handler.handleJSONObject(cache);
        } else {
            final String fileName = year + "_" + name();
            final HolidayType holidayType = this;
            final Folder folder = Folder.UPCOMING_EVENTS_HOLIDAYS;
            folder.setCustomFolderName(folder.getFolderName(false).replace("%year%", Integer.toString(year)));
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final boolean isCountries = holidayType == COUNTRIES;
                    final IHoliday[] holidays = getHolidays();
                    final HashMap<String, String> descriptions = new HashMap<>();
                    if(isCountries) {
                        loadCountryHolidays(year, holidays, descriptions, handler);
                    } else if(holidays != null) {
                        final boolean isChristian = holidayType == CHRISTIAN_EAST || holidayType == CHRISTIAN_WEST, isWestChristian = isChristian && holidayType == CHRISTIAN_WEST;
                        final int max = holidays.length;
                        final AtomicInteger completed = new AtomicInteger(0);
                        final HashMap<String, StringBuilder> values = new HashMap<>();
                        Arrays.stream(holidays).parallel().forEach(holiday -> {
                            final EventDate date = isChristian ? ((ChristianHoliday) holiday).getDate(isWestChristian, null, year) : holiday.getDate(null, year);
                            if(date != null) {
                                final String dateString = date.getDateString();
                                holiday.getImageURL(holidayType, new CompletionHandler() {
                                    @Override
                                    public void handleString(String imageURL) {
                                        holiday.getDescription(holidayType, new CompletionHandler() {
                                            @Override
                                            public void handleString(String description) {
                                                final HolidayObj customHoliday = getCustomHoliday(holiday, imageURL);
                                                final String englishName = customHoliday.getEnglishName();
                                                descriptions.putIfAbsent(englishName, description);

                                                final boolean isFirstDate = !values.containsKey(dateString);
                                                if(isFirstDate) {
                                                    values.put(dateString, new StringBuilder());
                                                }
                                                final String holidayString = customHoliday.toString();
                                                values.get(dateString).append(isFirstDate ? "" : ",").append(holidayString);

                                                if(completed.addAndGet(1) == max) {
                                                    final JSONObject json = new JSONObject();
                                                    final JSONObject descriptionsJSON = new JSONObject();
                                                    for(Map.Entry<String, String> map : descriptions.entrySet()) {
                                                        descriptionsJSON.put(map.getKey(), map.getValue());
                                                    }
                                                    json.put("descriptions", descriptionsJSON);
                                                    for(Map.Entry<String, StringBuilder> map : values.entrySet()) {
                                                        json.put(map.getKey(), "{" + map.getValue() + "}");
                                                    }
                                                    handler.handleJSONObject(json);
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                completed.addAndGet(1);
                            }
                        });
                    } else {
                        handler.handleString(null);
                    }
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    cache = json;
                    handler.handleJSONObject(json);
                }
            });
        }
    }

    private CustomHoliday getCustomHoliday(IHoliday holiday, String imageURL) {
        final String englishName = holiday.getName(), url = holiday.getURL();
        final String[] aliases = holiday.getAliases();
        return new CustomHoliday(celebrators, emoji, englishName, imageURL, aliases, url);
    }
    private HolidayObj getHolidayObj(IHoliday holiday, String imageURL, String description) {
        final String englishName = holiday.getName(), url = holiday.getURL();
        final EventSources otherSources = holiday.getOtherSources();
        final String[] aliases = holiday.getAliases();
        return new HolidayObj(englishName, imageURL, aliases, description, url, otherSources);
    }

    private void loadCountryHolidays(int year, IHoliday[] holidays, HashMap<String, String> descriptions, CompletionHandler handler) {
        final WLCountry[] countries = WLCountry.values();
        final int max = countries.length;
        final HashSet<String> countryHolidays = new HashSet<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.stream(countries).parallel().forEach(country -> {
            final String backendID = country.getBackendID();
            loadCountryHolidays(descriptions, holidays, country, year, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        countryHolidays.add("\"" + backendID + "\":" + string);
                    }
                    if(completed.addAndGet(1) == max) {
                        final StringBuilder builder = new StringBuilder("{\"descriptions\":{");
                        boolean isFirst = true;
                        for(Map.Entry<String, String> map : descriptions.entrySet()) {
                            final String name = map.getKey(), description = map.getValue();
                            builder.append(isFirst ? "" : ",").append("\"").append(name).append("\":\"").append(description).append("\"");
                            isFirst = false;
                        }
                        builder.append("}");
                        for(String countryHoliday : countryHolidays) {
                            builder.append(",").append(countryHoliday);
                        }
                        builder.append("}");
                        handler.handleString(builder.toString());
                    }
                }
            });
        });
    }
    private void loadCountryHolidays(HashMap<String, String> descriptions, IHoliday[] socialHolidays, WLCountry country, int year, CompletionHandler handler) {
        final HolidayType holidayType = this;
        final HashMap<String, HashSet<String>> holidays = new HashMap<>();
        final int max = socialHolidays.length;
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.stream(socialHolidays).parallel().forEach(socialHoliday -> {
            final EventDate date = socialHoliday.getDate(country, year);
            if(date != null) {
                final String dateString = date.getDateString();
                holidays.putIfAbsent(dateString, new HashSet<>());
                socialHoliday.getImageURL(holidayType, new CompletionHandler() {
                    @Override
                    public void handleString(String imageURL) {
                        socialHoliday.getDescription(holidayType, new CompletionHandler() {
                            @Override
                            public void handleString(String description) {
                                final HolidayObj holiday = getHolidayObj(socialHoliday, imageURL, description);
                                final String englishName = holiday.getEnglishName();
                                descriptions.putIfAbsent(englishName, description);
                                holidays.get(dateString).add(holiday.toString());
                                tryCompletingCountryHolidays(completed, max, holidays, handler);
                            }
                        });
                    }
                });
            } else {
                tryCompletingCountryHolidays(completed, max, holidays, handler);
            }
        });
    }
    private void tryCompletingCountryHolidays(AtomicInteger completed, int max, HashMap<String, HashSet<String>> holidays, CompletionHandler handler) {
        if(completed.addAndGet(1) == max) {
            String string = null;
            if(!holidays.isEmpty()) {
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(Map.Entry<String, HashSet<String>> map : holidays.entrySet()) {
                    final String dateKey = map.getKey();
                    final StringBuilder arrayBuilder = new StringBuilder("{");
                    boolean isFirstHoliday = true;
                    for(String holidayString : map.getValue()) {
                        arrayBuilder.append(isFirstHoliday ? "" : ",").append(holidayString);
                        isFirstHoliday = false;
                    }
                    arrayBuilder.append("}");
                    builder.append(isFirst ? "" : ",").append("\"").append(dateKey).append("\":").append(arrayBuilder);
                    isFirst = false;
                }
                builder.append("}");
                string = builder.toString();
            }
            handler.handleString(string);
        }
    }
}
