package me.randomhashtags.worldlaws.past.science;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.service.WikipediaEvent;

public final class ScienceEvent extends WikipediaEvent {
    public ScienceEvent(String description, EventSources externalLinks, EventSources sources) {
        super(description, externalLinks, sources);
    }
}
