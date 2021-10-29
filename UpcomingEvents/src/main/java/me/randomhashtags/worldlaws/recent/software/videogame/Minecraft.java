package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum Minecraft implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getCovertArtURL() {
        return "https://upload.wikimedia.org/wikipedia/en/5/51/Minecraft_cover.png";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://feedback.minecraft.net/hc/en-us/sections/360001186971-Release-Changelogs";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
    }
}
