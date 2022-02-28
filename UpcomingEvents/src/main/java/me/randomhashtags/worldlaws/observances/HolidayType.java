package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.type.*;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
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
        new CompletableFutures<HolidayType>().stream(Arrays.asList(types), type -> {
            final boolean isCountries = type == COUNTRIES;
            final JSONObject json = type.getHolidaysJSONObject(year);
            final JSONObject descriptionsJSON = json.getJSONObject("descriptions");
            final Set<String> keys = new HashSet<>(json.keySet());
            keys.remove("response_version");
            keys.remove("descriptions");
            if(isCountries) {
                for(String country : keys) {
                    final JSONObject countryJSON = json.getJSONObject(country);
                    final Collection<String> days = nearbyHolidayDays != null ? nearbyHolidayDays : countryJSON.keySet();
                    insertNearbyHolidays(days, descriptionsJSON, country, json, descriptions, nearbyHolidays);
                }
            } else {
                final String celebratingCountry = type.getCelebratingCountryBackendID();
                final Collection<String> days = nearbyHolidayDays != null ? nearbyHolidayDays : keys;
                insertNearbyHolidays(days, descriptionsJSON, celebratingCountry, json, descriptions, nearbyHolidays);
            }
        });
    }
    private static void insertNearbyHolidays(Collection<String> days, JSONObject descriptionsJSON, String country, JSONObject json, ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, ConcurrentHashMap<String, HolidayObj>> nearbyHolidays) {
        for(String holidayDay : days) {
            insertNearbyHolidays(descriptionsJSON, country, holidayDay, json, descriptions, nearbyHolidays);
        }
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
        final boolean isCountries = this == COUNTRIES;

        final Folder folder = Folder.UPCOMING_EVENTS_HOLIDAYS;
        final String realFileName = folder.getFolderName().replace("%year%", Integer.toString(year));
        folder.setCustomFolderName(fileName, realFileName);
        JSONObject json = getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return loadHolidaysJSONObject(isCountries, year);
            }
        });
        final int responseVersion = json.has("response_version") ? json.getInt("response_version") : 0;
        if(responseVersion < ResponseVersions.HOLIDAYS.getValue()) {
            json = loadHolidaysJSONObject(isCountries, year);
            if(json != null) {
                folder.setCustomFolderName(fileName, realFileName);
                setFileJSON(folder, fileName, json.toString());
            }
        }
        return json;
    }
    private JSONObject loadHolidaysJSONObject(boolean isCountries, int year) {
        final IHoliday[] holidays = getHolidays();
        final ConcurrentHashMap<String, String> descriptions = new ConcurrentHashMap<>();
        JSONObject json = null;
        if(isCountries) {
            json = loadCountryHolidays(year, holidays, descriptions);
        } else if(holidays != null) {
            final HolidayType self = this;
            final boolean isChristian = self == CHRISTIAN_EAST || self == CHRISTIAN_WEST, isWestChristian = isChristian && self == CHRISTIAN_WEST;
            final ConcurrentHashMap<String, HashSet<HolidayObj>> values = new ConcurrentHashMap<>();
            new CompletableFutures<IHoliday>().stream(Arrays.asList(holidays), holiday -> {
                final EventDate date = isChristian ? ((ChristianHoliday) holiday).getDate(isWestChristian, null, year) : holiday.getDate(null, year);
                if(date != null) {
                    final JSONObject holidayJSON = holiday.getHolidayJSON(self);
                    final String imageURL = holidayJSON.getString("imageURL"), description = holidayJSON.has("description") ? holidayJSON.getString("description") : "null";
                    final HolidayObj customHoliday = getHolidayObj(holiday, imageURL);
                    descriptions.putIfAbsent(customHoliday.getEnglishName(), description);

                    final String dateString = date.getDateString();
                    values.putIfAbsent(dateString, new HashSet<>());
                    values.get(dateString).add(customHoliday);
                }
            });
            json = completeHolidayJSONObject(descriptions, values);
        }
        if(json != null) {
            json.put("response_version", ResponseVersions.HOLIDAYS.getValue());
        }
        return json;
    }
    private JSONObject completeHolidayJSONObject(ConcurrentHashMap<String, String> descriptions, ConcurrentHashMap<String, HashSet<HolidayObj>> values) {
        final JSONObject json = new JSONObject(), descriptionsJSON = new JSONObject();
        for(Map.Entry<String, String> map : descriptions.entrySet()) {
            descriptionsJSON.put(map.getKey(), map.getValue());
        }
        json.put("descriptions", descriptionsJSON);
        for(Map.Entry<String, HashSet<HolidayObj>> map : values.entrySet()) {
            final JSONObject targetJSON = new JSONObject();
            for(HolidayObj holiday : map.getValue()) {
                targetJSON.put(holiday.getEnglishName(), holiday.getJSONObject());
            }
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
        final JSONObject json = new JSONObject();
        new CompletableFutures<WLCountry>().stream(Arrays.asList(countries), country -> {
            final JSONObject targetJSON = loadCountryHolidays(descriptions, holidays, country, year);
            if(targetJSON != null) {
                final String backendID = country != null ? country.getBackendID() : null;
                json.put(backendID, targetJSON);
            }
        });
        json.put("descriptions", descriptions);
        return json;
    }
    private JSONObject loadCountryHolidays(ConcurrentHashMap<String, String> descriptions, IHoliday[] socialHolidays, WLCountry country, int year) {
        final HolidayType self = this;
        final ConcurrentHashMap<String, HashSet<HolidayObj>> holidays = new ConcurrentHashMap<>();
        new CompletableFutures<IHoliday>().stream(Arrays.asList(socialHolidays), socialHoliday -> {
            final EventDate date = socialHoliday.getDate(country, year);
            if(date != null) {
                final JSONObject json = socialHoliday.getHolidayJSON(self);
                final String imageURL = json.getString("imageURL"), description = json.getString("description");
                final HolidayObj holiday = getHolidayObj(country, socialHoliday, imageURL, description);
                final String englishName = holiday.getEnglishName();
                descriptions.putIfAbsent(englishName, description);

                final String dateString = date.getDateString();
                holidays.putIfAbsent(dateString, new HashSet<>());
                holidays.get(dateString).add(holiday);
            }
        });
        return completeCountryHolidays(holidays);
    }
    private JSONObject completeCountryHolidays(ConcurrentHashMap<String, HashSet<HolidayObj>> holidays) {
        JSONObject json = null;
        if(!holidays.isEmpty()) {
            json = new JSONObject();
            for(Map.Entry<String, HashSet<HolidayObj>> map : holidays.entrySet()) {
                final String dateKey = map.getKey();
                final JSONObject holidaysJSON = new JSONObject();
                for(HolidayObj holidayString : map.getValue()) {
                    holidaysJSON.put(holidayString.getEnglishName(), holidayString.getJSONObject());
                }
                json.put(dateKey, holidaysJSON);
            }
        }
        return json;
    }
}
