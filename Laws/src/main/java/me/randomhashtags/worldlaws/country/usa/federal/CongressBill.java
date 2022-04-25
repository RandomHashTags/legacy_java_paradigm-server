package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.util.List;

public final class CongressBill {
    private final JSONObjectTranslatable sponsor;
    private final String summary;
    private final JSONArrayTranslatable cosponsors, actions, subjects;
    private final PolicyArea policyArea;
    private final List<CongressBill> relatedBills;
    private final EventSources sources;

    public CongressBill(JSONObjectTranslatable sponsor, String summary, PolicyArea policyArea, JSONArrayTranslatable subjects, JSONArrayTranslatable cosponsors, JSONArrayTranslatable actions, List<CongressBill> relatedBills, EventSources sources) {
        this.sponsor = sponsor;
        this.summary = summary;
        this.policyArea = policyArea;
        this.subjects = subjects;
        this.cosponsors = cosponsors;
        this.actions = actions;
        this.relatedBills = relatedBills;
        this.sources = sources;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("summary", "actions");
        json.put("sponsor", sponsor);
        json.put("summary", summary);
        json.put("subjects", subjects);
        json.put("actions", actions);
        if(policyArea != null) {
            json.put("policyArea", policyArea.getTag());
        }
        if(cosponsors != null) {
            json.put("cosponsors", cosponsors);
        }
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
