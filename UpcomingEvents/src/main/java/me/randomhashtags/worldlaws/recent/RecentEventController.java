package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public interface RecentEventController {
    RecentEventType getType();
    void refresh(LocalDate startingDate, CompletionHandler handler);
}
