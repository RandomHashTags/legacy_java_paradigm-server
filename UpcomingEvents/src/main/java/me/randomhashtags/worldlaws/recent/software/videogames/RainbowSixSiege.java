package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum RainbowSixSiege implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "Rainbow Six Siege";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://www.ubisoft.com/en-us/game/rainbow-six/siege/news-updates?category=patch-notes";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {

    }
}
