package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum Overwatch implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getUpdatePageURL() {
        return "https://playoverwatch.com/en-us/news/patch-notes/";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
    }
}
