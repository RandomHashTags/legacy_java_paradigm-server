package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;

import java.time.LocalDate;

public interface RecentEventController extends Jsoupable {
    RecentEventType getType();
    void refresh(LocalDate startingDate, CompletionHandler handler);
}
