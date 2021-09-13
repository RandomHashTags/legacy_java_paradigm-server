package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum Minecraft implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getUpdatePageURL() {
        return "https://feedback.minecraft.net/hc/en-us/sections/360001186971-Release-Changelogs";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
    }
}
