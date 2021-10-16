package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;

import java.time.LocalDate;

public interface VideoGameUpdateController extends Jsoupable {
    String getName();
    String getUpdatePageURL();
    void refresh(LocalDate startingDate, CompletionHandler handler);
}
