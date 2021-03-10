package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventController;
import me.randomhashtags.worldlaws.UpcomingEventType;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.util.HashMap;

public enum NewVideoGames implements EventController {
    INSTANCE;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }

    @Override
    public void refresh(CompletionHandler handler) {

    }

    @Override
    public String getCache() {
        return null;
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return null;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return null;
    }

    @Override
    public WLCountry getCountry() {
        return null;
    }

    private void refreshUpcomingVideoGames(int year, CompletionHandler handler) {
    }
}
