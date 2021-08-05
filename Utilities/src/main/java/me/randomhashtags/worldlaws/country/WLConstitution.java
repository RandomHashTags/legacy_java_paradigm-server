package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.country.constitutions.UnitedStatesConstitution;

import java.util.List;

public interface WLConstitution {
    String getURL();
    String getWikipediaURL();
    List<ConstitutionArticle> getSummary();

    static WLConstitution get(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return UnitedStatesConstitution.INSTANCE;
            default: return null;
        }
    }
}
