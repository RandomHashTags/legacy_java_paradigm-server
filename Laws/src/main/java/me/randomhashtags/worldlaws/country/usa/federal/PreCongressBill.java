package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.Chamber;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class PreCongressBill {
    private final USChamber chamber;
    private final String id, title, committees, notes;
    private final EventDate date;

    public PreCongressBill(USChamber chamber, String id, String title, String committees, String notes, EventDate date) {
        this.chamber = chamber;
        this.id = id;
        this.title = title;
        this.committees = committees;
        this.notes = notes;
        this.date = date;
    }

    public Chamber getChamber() {
        return chamber;
    }
    public String getID() {
        return id;
    }
    public EventDate getDate() {
        return date;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");
        if(notes != null) {
            json.put("notes", notes);
            json.addTranslatedKey("notes");
        }
        json.put("title", title);
        json.put("committees", committees);
        return json;
    }
}
