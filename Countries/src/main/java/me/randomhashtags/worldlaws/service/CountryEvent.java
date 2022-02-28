package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

import java.util.List;

public final class CountryEvent {

    private final List<EventDate> dates;
    private final String title;
    private final EventSources sources;

    public CountryEvent(List<EventDate> dates, String title, EventSources sources) {
        this.dates = dates;
        this.title = LocalServer.fixEscapeValues(title);
        this.sources = sources;
    }

    public String getTitle() {
        return title;
    }
    public EventSources getSources() {
        return sources;
    }
}
