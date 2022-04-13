package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

import java.util.HashSet;

public final class PreHoliday extends JSONObjectTranslatable {
    private final String identifier;
    private HashSet<WLCountry> countries;

    public PreHoliday(String identifier, String name, HolidayType type, String emoji, String celebrators) {
        super("name", "celebrators");
        this.identifier = identifier;
        put("name", name);
        put("type", type.name());
        if(emoji != null) {
            put("emoji", emoji);
        }
        if(celebrators != null) {
            put("celebrators", celebrators);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addCountry(WLCountry country) {
        if(country != null) {
            if(countries == null) {
                countries = new HashSet<>();
            }
            countries.add(country);
        }
    }

    public void insertCountries() {
        if(countries != null) {
            final JSONArray array = new JSONArray();
            for(WLCountry country : countries) {
                array.put(country.getBackendID());
            }
            put("countries", array);
        }
    }
}
