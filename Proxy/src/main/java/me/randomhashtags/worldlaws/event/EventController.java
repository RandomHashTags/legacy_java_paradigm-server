package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.NotNull;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.location.Country;

import java.time.Month;
import java.util.HashMap;
import java.util.Set;

public interface EventController extends RestAPI, Jsoupable {
    HashMap<String, Month> MONTHS = monthMap();

    static HashMap<String, Month> monthMap() {
        final HashMap<String, Month> months = new HashMap<>();
        for(Month month : Month.values()) {
            String target = "";
            final String name = month.name();
            for(int i = 0; i < name.length(); i++) {
                final String string = Character.toString(name.charAt(i));
                target = target.concat(string).concat(" ");
            }
            months.put(target, month);
        }
        return months;
    }

    String getIdentifier();
    void getUpcomingEvents(@NotNull CompletionHandler handler);
    Country getCountryOrigin(); // if null, it is worldwide/global

    default Month getMonthFrom(String text, Set<String> keys) {
        for(String key : keys) {
            if(text.startsWith(key)) {
                return MONTHS.get(key);
            }
        }
        return null;
    }
    default String getMonthKey(Month month, Set<String> keys) {
        for(String key : keys) {
            if(MONTHS.get(key).equals(month)) {
                return key;
            }
        }
        return null;
    }
}
