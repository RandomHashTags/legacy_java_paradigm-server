package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDate;

public interface VideoGameUpdateController extends Jsoupable {
    String getName();
    String getCovertArtURL();
    String getUpdatePageURL();
    void refresh(LocalDate startingDate, CompletionHandler handler);

    default String toVideoGameJSON() {
        return "\"" + LocalServer.fixEscapeValues(getName()) + "\":{" +
                "\"imageURL\":\"" + getCovertArtURL() + "\"" +
                "}";
    }
}
