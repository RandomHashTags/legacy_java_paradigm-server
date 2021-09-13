package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum DeadByDaylight implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "Dead by Daylight";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://forum.deadbydaylight.com/en/kb/patchnotes";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
    }
}
