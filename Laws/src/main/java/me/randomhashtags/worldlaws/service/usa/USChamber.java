package me.randomhashtags.worldlaws.service.usa;

import me.randomhashtags.worldlaws.Chamber;

public enum USChamber implements Chamber {
    HOUSE,
    SENATE,
    ;

    @Override
    public String getName() {
        return name();
    }
}
