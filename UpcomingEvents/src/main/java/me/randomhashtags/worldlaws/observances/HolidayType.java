package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.type.*;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            "Anyone who recognizes the importance of this holiday",
            "\uD83E\uDD2A"
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

    HolidayType(String celebrators, String emoji) {
        this.celebrators = celebrators;
        this.emoji = emoji;
    }

    public static void insertNearbyHolidays(int year, Collection<String> nearbyHolidayDays, ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays) {
        final HolidayType[] types = HolidayType.values();
        ParallelStream.stream(Arrays.asList(types), typeObj -> {
            final HolidayType type = (HolidayType) typeObj;
            final boolean isCountries = type == COUNTRIES;
            final JSONObject json = type.getHolidaysJSONObject(year);
            final JSONObject descriptionsJSON = json.getJSONObject("descriptions");
            final Set<String> keys = new HashSet<>(json.keySet());
            keys.remove("descriptions");
            if(isCountries) {
                for(String country : keys) {
                    final JSONObject countryJSON = json.getJSONObject(country);
                    final Collection<String> days = nearbyHolidayDays != null ? nearbyHolidayDays : countryJSON.keySet();
                    for(String holidayDay : days) {
                        insertNearbyHolidays(descriptionsJSON, country, holidayDay, countryJSON, descriptions, nearbyHolidays);
                    }
                }
            } else {
                final String celebratingCountry = type.getCelebratingCountryBackendID();
                final Collection<String> days = nearbyHolidayDays != null ? nearbyHolidayDays : keys;
                for(String holidayDay : days) {
                    insertNearbyHolidays(descriptionsJSON, celebratingCountry, holidayDay, json, descriptions, nearbyHolidays);
                }
            }
        });
    }
    private static void insertNearbyHolidays(JSONObject descriptionsJSON, String country, String holidayDay, JSONObject json, ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays) {
        if(json.has(holidayDay)) {
            final JSONObject array = json.getJSONObject(holidayDay);
            final boolean isCountries = country != null;
            for(String key : array.keySet()) {
                final JSONObject holidayJSON = array.getJSONObject(key);
                final HolidayObj holiday = new HolidayObj(key, holidayJSON);
                if(isCountries) {
                    holiday.addCountry(country);
                }
                final String holidayEnglishName = holiday.getEnglishName(), description = descriptionsJSON.getString(holidayEnglishName);
                descriptions.put(holidayEnglishName, description);
                nearbyHolidays.putIfAbsent(holidayDay, new ConcurrentHashMap<>());
                final ConcurrentHashMap<String, HolidayObj> map = nearbyHolidays.get(holidayDay);
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
    public String getCelebratingCountryBackendID() {
        final WLCountry country = getCelebratingCountry();
        return country != null ? country.getBackendID() : null;
    }

    private JSONObject getHolidaysJSONObject(int year) {
        final String fileName = year + "_" + name();
        final HolidayType self = this;
        final boolean isCountries = self == COUNTRIES;

        final Folder folder = Folder.UPCOMING_EVENTS_HOLIDAYS;
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%year%", Integer.toString(year)));
        return getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                final IHoliday[] holidays = getHolidays();
                final ConcurrentHashMap<String, String> descriptions = new ConcurrentHashMap<>();
                if(isCountries) {
                    return loadCountryHolidays(year, holidays, descriptions);
                } else if(holidays != null) {
                    final boolean isChristian = self == CHRISTIAN_EAST || self == CHRISTIAN_WEST, isWestChristian = isChristian && self == CHRISTIAN_WEST;
                    final ConcurrentHashMap<String, HashSet<String>> values = new ConcurrentHashMap<>();
                    ParallelStream.stream(Arrays.asList(holidays), holidayObj -> {
                        final IHoliday holiday = (IHoliday) holidayObj;
                        final EventDate date = isChristian ? ((ChristianHoliday) holiday).getDate(isWestChristian, null, year) : holiday.getDate(null, year);
                        if(date != null) {
                            final String dateString = date.getDateString();
                            final JSONObject json = holiday.getHolidayJSON(self);
                            final String imageURL = json.getString("imageURL"), description = json.getString("description");
                            final HolidayObj customHoliday = getHolidayObj(holiday, imageURL);
                            final String englishName = customHoliday.getEnglishName();
                            descriptions.putIfAbsent(englishName, description);

                            values.putIfAbsent(dateString, new HashSet<>());
                            values.get(dateString).add(customHoliday.toString());
                        }
                    });
                    return completeHolidayJSONObject(descriptions, values);
                } else {
                    return null;
                }
            }
        });
    }
    private JSONObject completeHolidayJSONObject(ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, HashSet<String>> values) {
        final JSONObject json = new JSONObject();
        final JSONObject descriptionsJSON = new JSONObject();
        for(Map.Entry<String, String> map : descriptions.entrySet()) {
            descriptionsJSON.put(map.getKey(), map.getValue());
        }
        json.put("descriptions", descriptionsJSON);
        for(Map.Entry<String, HashSet<String>> map : values.entrySet()) {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(String holiday : map.getValue()) {
                builder.append(isFirst ? "" : ",").append(holiday);
                isFirst = false;
            }
            builder.append("}");
            final String string = builder.toString();
            final JSONObject targetJSON = new JSONObject(string);
            json.put(map.getKey(), targetJSON);
        }
        return json;
    }

    private HolidayObj getHolidayObj(IHoliday holiday, String imageURL) {
        final String englishName = holiday.getName();
        final EventSources sources = holiday.getSources(null);
        final String[] aliases = holiday.getAliases();
        return new HolidayObj(celebrators, emoji, englishName, imageURL, aliases, sources);
    }
    private HolidayObj getHolidayObj(WLCountry country, IHoliday holiday, String imageURL, String description) {
        final String englishName = holiday.getName();
        final EventSources sources = holiday.getSources(country);
        final String[] aliases = holiday.getAliases();
        return new HolidayObj(englishName, imageURL, aliases, description, sources);
    }

    private JSONObject loadCountryHolidays(int year, IHoliday[] holidays, ConcurrentHashMap<String, String> descriptions) {
        final WLCountry[] countries = WLCountry.values();
        final HashSet<String> countryHolidays = new HashSet<>();
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleStringValue(String backendID, String value) {
                if(value != null) {
                    countryHolidays.add("\"" + backendID + "\":" + value);
                }
            }
        };
        ParallelStream.stream(Arrays.asList(countries), countryObj -> {
            final WLCountry country = (WLCountry) countryObj;
            loadCountryHolidays(descriptions, holidays, country, year, completionHandler);
        });
        final StringBuilder builder = new StringBuilder("{\"descriptions\":{");
        boolean isFirst = true;
        for(Map.Entry<String, String> map : descriptions.entrySet()) {
            final String name = map.getKey(), description = LocalServer.fixEscapeValues(map.getValue());
            builder.append(isFirst ? "" : ",").append("\"").append(name).append("\":\"").append(description).append("\"");
            isFirst = false;
        }
        builder.append("}");
        for(String countryHoliday : countryHolidays) {
            builder.append(",").append(countryHoliday);
        }
        builder.append("}");
        return new JSONObject(builder.toString());
    }
    private void loadCountryHolidays(ConcurrentHashMap<String, String> descriptions, IHoliday[] socialHolidays, WLCountry country, int year, CompletionHandler handler) {
        final HolidayType holidayType = this;
        final ConcurrentHashMap<String, HashSet<String>> holidays = new ConcurrentHashMap<>();
        final String backendID = country != null ? country.getBackendID() : null;
        ParallelStream.stream(Arrays.asList(socialHolidays), socialHolidayObj -> {
            final IHoliday socialHoliday = (IHoliday) socialHolidayObj;
            final EventDate date = socialHoliday.getDate(country, year);
            if(date != null) {
                final String dateString = date.getDateString();
                holidays.putIfAbsent(dateString, new HashSet<>());
                final JSONObject json = socialHoliday.getHolidayJSON(holidayType);
                final String imageURL = json.getString("imageURL"), description = json.getString("description");
                final HolidayObj holiday = getHolidayObj(country, socialHoliday, imageURL, description);
                final String englishName = holiday.getEnglishName();
                descriptions.putIfAbsent(englishName, description);
                holidays.get(dateString).add(holiday.toString());
            }
        });
        completeCountryHolidays(holidays, backendID, handler);
    }
    private void completeCountryHolidays(ConcurrentHashMap<String, HashSet<String>> holidays, String backendID, CompletionHandler handler) {
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
        handler.handleStringValue(backendID, string);
    }
}
