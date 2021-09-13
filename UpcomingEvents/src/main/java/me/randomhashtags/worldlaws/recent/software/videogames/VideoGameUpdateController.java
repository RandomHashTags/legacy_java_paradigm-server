package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDate;

public interface VideoGameUpdateController extends Jsoupable {
    String name();
    default String getName() {
        return LocalServer.toCorrectCapitalization(name(), "by", "of");
    }
    String getUpdatePageURL();
    void refresh(LocalDate startingDate, CompletionHandler handler);
}
