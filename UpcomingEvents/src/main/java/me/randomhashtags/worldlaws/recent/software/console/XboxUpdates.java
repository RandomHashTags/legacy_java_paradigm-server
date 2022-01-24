package me.randomhashtags.worldlaws.recent.software.console;

import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;

import java.time.LocalDate;
import java.util.HashSet;

public final class XboxUpdates extends RecentEventController {
    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public HashSet<PreRecentEvent> refreshHashSet(LocalDate startingDate) {
        final String url = "https://support.xbox.com/en-US/help/hardware-network/settings-updates/whats-new-xbox-one-system-updates";
        return null;
    }
}
