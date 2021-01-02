package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.usa.USPolitician;
import me.randomhashtags.worldlaws.people.Politician;

import java.util.HashSet;
import java.util.List;

public final class CongressBill {
    private final String chamber, id, title, summary, url, pdfURL;
    private final Politician sponsor;
    private final PolicyArea policyArea;
    private final HashSet<String> subjects;
    private final List<Politician> cosponsors;
    private final List<BillAction> actions;
    private final List<CongressBill> relatedBills;

    public CongressBill(String chamber, String id, String title, Politician sponsor, String summary, PolicyArea policyArea, HashSet<String> subjects, List<Politician> cosponsors, List<BillAction> actions, String url, String pdfURL, List<CongressBill> relatedBills) {
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

    public String getChamber() {
        return chamber;
    }
    public String getID() {
        return id;
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
    public String getURL() {
        return url;
    }
    public String getPDFURL() {
        return pdfURL;
    }
    public List<CongressBill> getRelatedBills() {
        return relatedBills;
    }

    private String getCosponsorsJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Politician politician : cosponsors) {
            builder.append(isFirst ? "" : ",").append(politician.toJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "{\"chamber\":\"" + chamber + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"sponsor\":" + sponsor.toJSON() + "," +
                "\"summary\":\"" + summary + "\"," +
                "\"policyArea\":\"" + (policyArea != null ? policyArea.getTag() : "null") + "\"," +
                "\"subjects\":" + subjects.toString() + "," +
                "\"cosponsors\":" + getCosponsorsJSON() + "," +
                "\"actions\":" + actions.toString() + "," +
                "\"url\":\"" + url + "\"," +
                "\"pdfURL\":\"" + pdfURL + "\"" +
                "}";
    }
}
