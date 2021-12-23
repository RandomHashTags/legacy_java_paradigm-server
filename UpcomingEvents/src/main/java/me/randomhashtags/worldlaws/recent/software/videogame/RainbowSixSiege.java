package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum RainbowSixSiege implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "Rainbow Six Siege";
    }

    @Override
    public String getCoverArtURL() {
        return "https://upload.wikimedia.org/wikipedia/en/4/47/Tom_Clancy%27s_Rainbow_Six_Siege_cover_art.jpg";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://www.ubisoft.com/en-us/game/rainbow-six/siege/news-updates?category=patch-notes";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {

    }
}
