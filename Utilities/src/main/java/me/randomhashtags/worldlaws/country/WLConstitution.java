package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.country.constitutions.ConstitutionUnitedStates;

import java.util.List;

public interface WLConstitution {
    String getURL();
    String getWikipediaURL();
    List<ConstitutionArticle> getSummary();

    static WLConstitution get(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return ConstitutionUnitedStates.INSTANCE;
            default: return null;
        }
    }
}
