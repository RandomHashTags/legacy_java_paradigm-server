package me.randomhashtags.worldlaws.recent.software.videogame;

import java.time.LocalDate;

public enum DeadByDaylight implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "Dead by Daylight";
    }

    @Override
    public String getCoverArtURL() {
        return "https://upload.wikimedia.org/wikipedia/en/b/b7/Dead_by_Daylight_Steam_header.jpg";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://forum.deadbydaylight.com/en/kb/patchnotes";
    }

    @Override
    public VideoGameUpdate refresh(LocalDate startingDate) {
        return null;
    }
}
