package me.randomhashtags.worldlaws.recent.software.console;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;

import java.time.LocalDate;

public enum XboxUpdates implements RecentEventController {
    INSTANCE;

    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final String url = "https://support.xbox.com/en-US/help/hardware-network/settings-updates/whats-new-xbox-one-system-updates";
    }
}
