package me.randomhashtags.worldlaws.country.usa.federal;

import java.util.List;

public final class CongressBill {
    private final String sponsor, summary, url, pdfURL, subjects, cosponsors, actions;
    private final PolicyArea policyArea;
    private final List<CongressBill> relatedBills;

    public CongressBill(String sponsor, String summary, PolicyArea policyArea, String subjects, String cosponsors, String actions, String url, String pdfURL, List<CongressBill> relatedBills) {
        this.sponsor = sponsor;
        this.summary = summary;
        this.policyArea = policyArea;
        this.subjects = subjects;
        this.cosponsors = cosponsors;
        this.actions = actions;
        this.url = url;
        this.pdfURL = pdfURL;
        this.relatedBills = relatedBills;
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
                "\"url\":\"" + url + "\"," +
                "\"pdfURL\":\"" + pdfURL + "\"" +
                "}";
    }
}
