package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.law.Chamber;

public enum USChamber implements Chamber {
    HOUSE,
    SENATE,
    ;

    @Override
    public String getName() {
        return name();
    }
}
