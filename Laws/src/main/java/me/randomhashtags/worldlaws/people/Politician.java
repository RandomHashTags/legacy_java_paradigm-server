package me.randomhashtags.worldlaws.people;

import me.randomhashtags.worldlaws.LegislationType;
import me.randomhashtags.worldlaws.ServerObject;

public interface Politician extends Person, ServerObject {
    String getGovernedTerritory();
    String getDistrict();
    PoliticalParty getCurrentParty();
    String getImageURL();
    String getURL();
    String getWebsite();
    String getSignedLegislationJSON(LegislationType type, int administration);

    default String toJSON() {
        final HumanName name = getName();
        final String imageURL = getImageURL(), district = getDistrict(), website = getWebsite();
        return "{" +
                (name != null ? "\"name\":" + name.toString() + "," : "") +
                "\"governedTerritory\":\"" + getGovernedTerritory() + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (district != null ? "\"district\":\"" + getDistrict() + "\"," : "") +
                (website != null ? "\"website\":\"" + website + "\"," : "") +
                "\"party\":\"" + getCurrentParty().getName() + "\"," +
                "\"url\":\"" + getURL() + "\"" +
                "}";
    }

    @Override
    default String toServerJSON() {
        final HumanName name = getName();
        final String imageURL = getImageURL(), district = getDistrict(), website = getWebsite();
        return "{" +
                (name != null ? "\"name\":" + name.toString() + "," : "") +
                "\"governedTerritory\":\"" + getGovernedTerritory() + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (district != null ? "\"district\":\"" + getDistrict() + "\"," : "") +
                (website != null ? "\"website\":\"" + getWebsite() + "\"," : "") +
                "\"party\":\"" + getCurrentParty().getName() + "\"," +
                "\"url\":\"" + getURL() + "\"" +
                "}";
    }
}
