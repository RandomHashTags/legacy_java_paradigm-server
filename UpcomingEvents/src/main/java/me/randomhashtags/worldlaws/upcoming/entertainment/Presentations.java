package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.PresentationEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public final class Presentations extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.PRESENTATION;
    }

    @Override
    public void load() {
        final UpcomingEventType type = getType();
        final LocalDate startingDay = LocalDate.now();
        new CompletableFutures<PresentationType>().stream(Arrays.asList(PresentationType.values()), presentationType -> {
            final List<PresentationEvent> events = presentationType.refresh(startingDay);
            if(events != null) {
                for(PresentationEvent event : events) {
                    final EventDate date = event.getDate();
                    final String title = event.getTitle();
                    final String identifier = getEventDateIdentifier(date.getDateString(), title);

                    putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(type, identifier, event.getTag()));
                    putUpcomingEvent(identifier, event);
                }
            }
        });
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new PresentationEvent(json);
    }
}
