package me.randomhashtags.worldlaws.recent.software.videogame;

import java.time.LocalDate;

public enum Minecraft implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getCoverArtURL() {
        return "https://upload.wikimedia.org/wikipedia/en/5/51/Minecraft_cover.png";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://feedback.minecraft.net/hc/en-us/sections/360001186971-Release-Changelogs";
    }

    @Override
    public VideoGameUpdate refresh(LocalDate startingDate) {
        return null;
    }
}
