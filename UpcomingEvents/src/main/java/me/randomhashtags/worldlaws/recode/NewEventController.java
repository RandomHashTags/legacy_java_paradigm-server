package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.util.HashMap;

public interface NewEventController extends RestAPI, Jsoupable {
    WLCountry getCountry();
    UpcomingEventType getType();
    EventSource getSource();

    HashMap<String, PreUpcomingEvent> getPreEvents();

    void refresh(CompletionHandler handler);
    void getEvent(String id, CompletionHandler handler);

    default String getEventIdentifier(EventDate date, String title) {
        return getEventDateIdentifier(date) + "." + title.toLowerCase().replace(" ", "").replace("|", "-");
    }
    private String getEventDateIdentifier(EventDate date) {
        return date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
    }
}
