package me.randomhashtags.worldlaws.federal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.people.Politician;

import java.util.HashSet;
import java.util.List;

public final class EnactedBill {
    private String title, summary, pdfURL;
    private Politician sponsor;
    private PolicyArea policyArea;
    private HashSet<String> subjects;
    private List<Politician> cosponsors;
    private List<BillAction> actions;
    private List<EnactedBill> relatedBills;

    public EnactedBill(String title, Politician sponsor, String summary, PolicyArea policyArea, HashSet<String> subjects, List<Politician> cosponsors, List<BillAction> actions, String pdfURL, List<EnactedBill> relatedBills) {
        this.title = title;
        this.sponsor = sponsor;
        this.summary = summary;
        this.policyArea = policyArea;
        this.subjects = subjects;
        this.cosponsors = cosponsors;
        this.actions = actions;
        this.pdfURL = pdfURL;
        this.relatedBills = relatedBills;
    }

    public String getTitle() {
        return title;
    }
    public Politician getSponsor() {
        return sponsor;
    }
    public String getSummary() {
        return summary;
    }
    public PolicyArea getPolicyArea() {
        return policyArea;
    }
    public HashSet<String> getSubjects() {
        return subjects;
    }
    public List<Politician> getCosponsors() {
        return cosponsors;
    }
    public List<BillAction> getActions() {
        return actions;
    }
    public String getPDFURL() {
        return pdfURL;
    }
    public List<EnactedBill> getRelatedBills() {
        return relatedBills;
    }

    @Override
    public String toString() {
        return "{\"title\":\"" + LocalServer.fixEscapeValues(title) + "\"," +
                "\"sponsor\":" + sponsor.toString() + "," +
                "\"summary\":\"" + summary + "\"," +
                "\"policyArea\":\"" + policyArea.getTag() + "\"," +
                "\"subjects\":" + subjects.toString() + "," +
                "\"cosponsors\":" + cosponsors.toString() + "," +
                "\"actions\":" + actions.toString() + "," +
                "\"pdfURL\":\"" + pdfURL + "\"" +
                "}";
    }
}
