package me.randomhashtags.worldlaws.people;

import me.randomhashtags.worldlaws.law.LegislationType;

public interface Politician extends Person {
    String getGovernedTerritory();
    String getDistrict();
    PoliticalParty getCurrentParty();
    String getImageURL();
    String getURL();
    String getWebsite();
    String getSignedLegislationJSON(LegislationType type, int administration);

    default String toJSON() {
        return "{\"name\":" + getName().toString() + "," +
                "\"governedTerritory\":\"" + getGovernedTerritory() + "\"," +
                "\"district\":\"" + getDistrict() + "\"," +
                "\"party\":\"" + getCurrentParty().getName() + "\"," +
                "\"imageURL\":\"" + getImageURL() + "\"," +
                "\"url\":\"" + getURL() + "\"," +
                "\"website\":\"" + getWebsite() + "\"" +
                "}";
    }
}
