package me.randomhashtags.worldlaws.recent.software;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;

import java.time.LocalDate;

public enum PlayStation5Updates implements RecentEventController, Jsoupable {
    INSTANCE;

    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = "https://www.playstation.com/en-us/support/hardware/ps5/system-software/";
    }
}
