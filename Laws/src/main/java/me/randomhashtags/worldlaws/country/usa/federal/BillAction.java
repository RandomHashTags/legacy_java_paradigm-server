package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.time.LocalDateTime;

public final class BillAction {
    private final USChamber chamber;
    private final LocalDateTime date;
    private final String title;

    public BillAction(USChamber chamber, String title, LocalDateTime date) {
        this.chamber = chamber;
        this.title = title;
        this.date = date;
    }

    public USChamber getChamber() {
        return chamber;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(chamber != null) {
            json.put("chamber", chamber.name());
        }
        json.put("title", title);
        json.put("date", date.toString());
        return json;
    }
}
