package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.ServerObject;

import java.util.List;

public final class CongressBill implements ServerObject {
    private final String chamber, id, title, sponsor, summary, url, pdfURL, subjects, cosponsors, actions;
    private final PolicyArea policyArea;
    private final List<CongressBill> relatedBills;

    public CongressBill(String chamber, String id, String title, String sponsor, String summary, PolicyArea policyArea, String subjects, String cosponsors, String actions, String url, String pdfURL, List<CongressBill> relatedBills) {
        this.chamber = chamber;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
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
                (chamber != null ? "\"chamber\":\"" + chamber + "\"," : "") +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"sponsor\":" + sponsor + "," +
                "\"summary\":\"" + summary + "\"," +
                (policyArea != null ? "\"policyArea\":\"" + policyArea.getTag() + "\"," : "") +
                "\"subjects\":" + subjects + "," +
                "\"cosponsors\":" + cosponsors + "," +
                "\"actions\":" + actions + "," +
                "\"url\":\"" + url + "\"," +
                "\"pdfURL\":\"" + pdfURL + "\"" +
                "}";
    }
    @Override
    public String toServerJSON() {
        return "{" +
                (chamber != null ? "\"chamber\":\"" + chamber + "\"," : "") +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"sponsor\":" + sponsor + "," +
                "\"summary\":\"" + summary + "\"," +
                (policyArea != null ? "\"policyArea\":\"" + policyArea.getTag() + "\"," : "") +
                "\"subjects\":" + subjects + "," +
                "\"cosponsors\":" + cosponsors + "," +
                "\"actions\":" + actions + "," +
                "\"url\":\"" + url + "\"," +
                "\"pdfURL\":\"" + pdfURL + "\"" +
                "}";
    }
}
