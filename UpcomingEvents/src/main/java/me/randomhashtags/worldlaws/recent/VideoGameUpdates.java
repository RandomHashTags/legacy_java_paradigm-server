package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;

import java.time.LocalDate;

public enum VideoGameUpdates implements RecentEventController {
    CALL_OF_DUTY(
            "https://www.callofduty.com/blog"
    ),
    DEAD_BY_DAYLIGHT(
            "https://forum.deadbydaylight.com/en/kb/patchnotes"
    ),
    MINECRAFT(
            "https://feedback.minecraft.net/hc/en-us/sections/360001186971-Release-Changelogs"
    ),
    NO_MANS_SKY(
            "No Man's Sky",
            "https://www.nomanssky.com/release-log/"
    ),
    OVERWATCH(
            "https://playoverwatch.com/en-us/news/patch-notes/"
    ),
    ;

    private final String name, url;

    VideoGameUpdates(String url) {
        this(null, url);
    }
    VideoGameUpdates(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name != null ? name : name();
    }

    @Override
    public RecentEventType getType() {
        return RecentEventType.VIDEO_GAME_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        switch (this) {
            case CALL_OF_DUTY:
                refreshCallOfDuty(startingDate, handler);
                break;
            case MINECRAFT:
                refreshMinecraft(startingDate, handler);
                break;
            case NO_MANS_SKY:
                refreshNoMansSky(startingDate, handler);
                break;
            case OVERWATCH:
                refreshOverwatch(startingDate, handler);
                break;
        }
    }

    private void refreshCallOfDuty(LocalDate startingDate, CompletionHandler handler) {
    }
    private void refreshNoMansSky(LocalDate startingDate, CompletionHandler handler) {
    }
    private void refreshMinecraft(LocalDate startingDate, CompletionHandler handler) {
    }
    private void refreshOverwatch(LocalDate startingDate, CompletionHandler handler) {
    }
}
