package me.randomhashtags.worldlaws.country;

import java.util.List;

public enum LawCategory {
    AGRICULTURE,
    ALCOHOL,
    EDUCATION,
    ELECTIONS,
    HEALTH,
    INSURANCE,
    TAXES,
    TRANSPORTATION,
    ;

    public List<String> getAliases() {
        switch (this) {
            case AGRICULTURE: return null;
            default: return null;
        }
    }
}
