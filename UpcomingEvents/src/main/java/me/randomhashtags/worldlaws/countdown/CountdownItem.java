package me.randomhashtags.worldlaws.countdown;

import me.randomhashtags.worldlaws.EventDate;

public interface CountdownItem {
    String getTitle();
    EventDate getDate();
    EventDate getEndDate();
}
