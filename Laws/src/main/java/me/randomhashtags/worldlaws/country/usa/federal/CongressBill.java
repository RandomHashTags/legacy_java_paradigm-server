package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.util.List;

public final class CongressBill {
    private final String sponsor, summary, subjects, cosponsors, actions;
    private final PolicyArea policyArea;
    private final List<CongressBill> relatedBills;
    private final EventSources sources;

    public CongressBill(String sponsor, String summary, PolicyArea policyArea, String subjects, String cosponsors, String actions, List<CongressBill> relatedBills, EventSources sources) {
        this.sponsor = sponsor;
        this.summary = summary;
        this.policyArea = policyArea;
        this.subjects = subjects;
        this.cosponsors = cosponsors;
        this.actions = actions;
        this.relatedBills = relatedBills;
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "{" +
                "\"sponsor\":" + sponsor + "," +
                "\"summary\":\"" + summary + "\"," +
                (policyArea != null ? "\"policyArea\":\"" + policyArea.getTag() + "\"," : "") +
                "\"subjects\":" + subjects + "," +
                (cosponsors != null ? "\"cosponsors\":" + cosponsors + "," : "") +
                "\"actions\":" + actions + "," +
                "\"sources\":" + sources.toString() +
                "}";
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("summary");
        json.put("summary", summary);
        return json;
    }
}
