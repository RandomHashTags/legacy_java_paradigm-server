package me.randomhashtags.worldlaws.people;

import me.randomhashtags.worldlaws.LegislationType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface Politician extends Person {
    String getGovernedTerritory();
    String getDistrict();
    PoliticalParty getCurrentParty();
    String getImageURL();
    String getURL();
    String getWebsite();
    JSONObjectTranslatable getSignedLegislationJSON(LegislationType type, int administration);

    default JSONObjectTranslatable toJSONObject() {
        final HumanName name = getName();
        final String imageURL = getImageURL(), district = getDistrict(), website = getWebsite();
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(name != null) {
            json.put("name", name.toJSONObject());
        }
        json.put("governedTerritory", getGovernedTerritory());
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
        if(district != null) {
            json.put("district", district);
        }
        if(website != null) {
            json.put("website", website);
        }
        json.put("party", getCurrentParty().getName());
        json.put("url", getURL());
        return json;
    }
}
